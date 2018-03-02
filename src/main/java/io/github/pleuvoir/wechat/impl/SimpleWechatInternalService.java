package io.github.pleuvoir.wechat.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;
import io.github.pleuvoir.cache.RobotCache;
import io.github.pleuvoir.constant.WechatApiURL;
import io.github.pleuvoir.domain.BaseRequest;
import io.github.pleuvoir.domain.ContactsResponse;
import io.github.pleuvoir.domain.LoginSession;
import io.github.pleuvoir.domain.RetCode;
import io.github.pleuvoir.domain.StatusNotifyRequest;
import io.github.pleuvoir.domain.SyncCheckResponse;
import io.github.pleuvoir.domain.SyncResponse;
import io.github.pleuvoir.domain.WebInitResponse;
import io.github.pleuvoir.domain.WebwxSyncRequest;
import io.github.pleuvoir.exception.WechatException;
import io.github.pleuvoir.net.OkhttpUtil;
import io.github.pleuvoir.time.Clock;
import io.github.pleuvoir.utils.HttpClientUtils;
import io.github.pleuvoir.utils.RegexUtil;
import io.github.pleuvoir.utils.UrlFormatUtil;
import io.github.pleuvoir.wechat.WechatInternalService;
import lombok.SneakyThrows;

@Service
public class SimpleWechatInternalService implements WechatInternalService {

	private static Logger logger =  LoggerFactory.getLogger(SimpleWechatInternalService.class);
	
	private static final Pattern UUID_PATTERN          = Pattern.compile("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";");
	private static final Pattern CHECK_LOGIN_PATTERN   = Pattern.compile("window.code=(\\d+)");
    private static final Pattern REDIRECT_URL_PATTERN  = Pattern.compile("window.redirect_uri=\"(\\S+)\";");
    private static final Pattern SYNC_CHECK_PATTERN    = Pattern.compile("window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"}");
	
	@Autowired
    private RobotCache robotCache;
    
	
	@Override
	@SneakyThrows
	public void getUUID() {
		Map<String, String> map = new HashMap<>();
		map.put("appid", "wx782c26e4c19acffb");
		map.put("redirect_uri", "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage");
		map.put("fun", "new");
		map.put("lang", "zh_CN");
		map.put("_", Clock.now());
		String response = HttpClientUtils.doGet(WechatApiURL.GET_UUID, map);
		Matcher matcher = UUID_PATTERN.matcher(response);
		if (matcher.find()) {
			String uuid = matcher.group(2);
			robotCache.setUuid(uuid);
		} else {
			throw new WechatException("获取UUID失败");
		}
	}

	
	@Override
	public byte[] getQrcode() {
		return HttpClientUtils.getAsBytes(WechatApiURL.SHOW_QRCODE + robotCache.getUuid());
	}

	
	@Override
	@SneakyThrows
	public boolean checkLogin() {
		Map<String, String> map = new HashMap<>();
		map.put("uuid", robotCache.getUuid());
		map.put("loginicon", "true");
		map.put("tip", "0");
		map.put("r", Clock.now());
		map.put("_", Clock.now());
		String response = HttpClientUtils.doGet(WechatApiURL.WAIT_LOGIN, map);
		Matcher matcher = CHECK_LOGIN_PATTERN.matcher(response);
		Matcher redirectUrlMatcher = REDIRECT_URL_PATTERN.matcher(response);
		// 登录状态码
		String code = null;
		if (matcher.find()) {
			code = matcher.group(1);
			logger.debug("登录状态码：{}", matcher.group(1));
			switch (code) {
			case "200":
				if (redirectUrlMatcher.find()) {
					robotCache.setRedirecturl(redirectUrlMatcher.group(1));
					logger.info("[*] 已确认登录");
					return true;
				}
				throw new WechatException("检查登录状态异常，获取redirecturl失败");
			case "408":
				logger.info("[*] 等待扫描二维码");
				return false;
			case "201":
				logger.info("[*] 已扫描，但未点击确认");
				return false;
			case "400":
				logger.info("[*] 二维码超时");
				return false;
			default:
				logger.info("[*] 检测到未知的登录状态");
				return false;
			}
		}
		throw new WechatException("检查登录状态异常，获取登录状态失败");
	}

	
	@Override
	@SneakyThrows
	public void initLoginSession() {
		LoginSession loginSession = new LoginSession();
		String response = HttpClientUtils.doGet(robotCache.getRedirecturl() + "&fun=new&version=v2", Maps.newHashMap());
		BaseRequest baseRequest = new BaseRequest();
		loginSession.setSKey(RegexUtil.match("<skey>(\\S+)</skey>", response));
		loginSession.setWxSid(RegexUtil.match("<wxsid>(\\S+)</wxsid>", response));
		loginSession.setWxUin(RegexUtil.match("<wxuin>(\\S+)</wxuin>", response));
		loginSession.setPassTicket(RegexUtil.match("<pass_ticket>(\\S+)</pass_ticket>", response));
		loginSession.setDeviceId("e" + String.valueOf(System.currentTimeMillis()));
		baseRequest.setSkey(loginSession.getSKey());
		baseRequest.setSid(loginSession.getWxSid());
		baseRequest.setUin(loginSession.getWxUin());
		baseRequest.setDeviceID(loginSession.getDeviceId());
		loginSession.setBaseRequest(baseRequest);
		robotCache.setLoginSession(loginSession);
		logger.debug("已缓存loginSession");
		String cookie = HttpClientUtils.getCookie();
		robotCache.setCookie(cookie);
		logger.debug("已缓存cookie");
	}
	
	
	@Override
	@SneakyThrows
	public void wechatInit() {
		LoginSession loginSession = robotCache.getLoginSession();
		String url = UrlFormatUtil.format(WechatApiURL.WEBWX_INIT, Clock.now(), loginSession.getPassTicket());
		Map<String, Object> baseRequestMap = Maps.newHashMap();
		baseRequestMap.put("BaseRequest", loginSession.getBaseRequest());
		String response = HttpClientUtils.doPost(url, JSON.toJSONString(baseRequestMap));
		WebInitResponse webInitResponse = JSON.parseObject(response, WebInitResponse.class);
		logger.debug("微信初始化接收到响应：{}", JSON.toJSONString(webInitResponse));
		// 服务端返回时，同时设置对应的syncKeyStr，方便后面同步检查使用
		loginSession.setSyncKey(webInitResponse.getSyncKey());
		loginSession.setUserName(webInitResponse.getUser().getUserName());
		loginSession.setNickName(webInitResponse.getUser().getNickName());
		logger.info("[*] 欢迎回来，尊贵的：{}", robotCache.getLoginSession().getNickName());
	}
	
	
	@Override
	@SneakyThrows
	public void statusNotify() {
		LoginSession loginSession = robotCache.getLoginSession();
		String url = UrlFormatUtil.format(WechatApiURL.STATUS_NOTIFY, loginSession.getPassTicket());
		StatusNotifyRequest notifyRequest = new StatusNotifyRequest();
		notifyRequest.setBaseRequest(loginSession.getBaseRequest());
		notifyRequest.setFromUserName(loginSession.getUserName());
		notifyRequest.setToUserName(loginSession.getUserName());
		notifyRequest.setClientMsgId(new Long(System.currentTimeMillis()).intValue());
		String response = HttpClientUtils.doPost(url, JSONObject.toJSONString(notifyRequest));
		String baseResponseString = JSONObject.parseObject(response).getString("BaseResponse");
		JSONObject baseResponse = JSONObject.parseObject(baseResponseString);
		if (RetCode.NORMAL.getCode() != baseResponse.getInteger("Ret")) {
			logger.error("开启微信通知失败");
			throw new WechatException("开启微信通知失败");
		} else {
			logger.info("开启微信通知成功");
		}
	}
	
	
	@Override
	@SneakyThrows
	public SyncCheckResponse syncCheck() {
		LoginSession loginSession = robotCache.getLoginSession();
		Map<String, String> params = new HashMap<>();
		params.put("r", Clock.now());
		params.put("skey", loginSession.getSKey());
		params.put("sid", loginSession.getWxSid());
		params.put("uin", loginSession.getWxUin());
		params.put("deviceid", loginSession.getDeviceId());
		params.put("synckey", loginSession.getSyncKeyStr());
		params.put("_", Clock.now());
		params.put("r", Clock.now());
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(WechatApiURL.SYNC_CHECK).append("?r=").append(Clock.now()).
							append("&skey=").append(loginSession.getSKey()).
							append("&sid=").append(loginSession.getWxSid()).append("&uin=").append(loginSession.getWxUin())
							.append("&deviceid=").append(loginSession.getDeviceId()).append("&synckey=").append(loginSession.getSyncKeyStr()).
							append("&_=").append(Clock.now());
															
		//似乎用okhhtp更为稳定一些 不容易返回1102
		String response = OkhttpUtil.getAsStringWithCookies(urlBuilder.toString(), robotCache.getCookie());
		Matcher m = SYNC_CHECK_PATTERN.matcher(response);
		if (m.find()) {
			SyncCheckResponse syncCheckResponse = new SyncCheckResponse();
			syncCheckResponse.setRetcode(Integer.valueOf(m.group(1)));
			syncCheckResponse.setSelector(Integer.valueOf(m.group(2)));
			return syncCheckResponse;
		} else {
			throw new WechatException("同步检查消息状态失败，未匹配到响应码");
		}
	}
	
	
	@Override
	@SneakyThrows
	public SyncResponse sync() {
		LoginSession loginSession = robotCache.getLoginSession();
		String url = UrlFormatUtil.format(WechatApiURL.WEBWX_SYNC, loginSession.getWxSid(), loginSession.getSKey());
		WebwxSyncRequest webwxSyncRequest = new WebwxSyncRequest();
		webwxSyncRequest.setBaseRequest(loginSession.getBaseRequest());
		webwxSyncRequest.setSyncKey(loginSession.getSyncKey());
		webwxSyncRequest.setRr(new Long(~System.currentTimeMillis()).intValue());
		String jsonRequest = JSONObject.toJSONString(webwxSyncRequest);
		String response = HttpClientUtils.doPost(url, jsonRequest);
		// 这里需要更新wechatinit方法中的同步密钥，每请求一次数组会增大一次
		SyncResponse syncResponse = JSON.parseObject(response, SyncResponse.class);
		// 更新对应的syncKeyStr，下次后面同步检查再用到时已经是新的值了
		loginSession.setSyncKey(syncResponse.getSyncKey());
		return syncResponse;
	}

	
	@Override
	@SneakyThrows
	public void sendMessage(String toUserName, String content) {
		LoginSession loginSession = robotCache.getLoginSession();
		String randomId = Clock.now();
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			randomId += random.nextInt(10);
		}
		JSONObject msg = new JSONObject();
		msg.put("Type", 1);
		msg.put("Content", content);
		msg.put("FromUserName", loginSession.getUserName());
		msg.put("ToUserName", toUserName);
		msg.put("LocalID", randomId);
		msg.put("ClientMsgId", randomId);
		JSONObject sengMsg = new JSONObject();
		sengMsg.put("BaseRequest", loginSession.getBaseRequest());
		sengMsg.put("Msg", msg);
		Map<String, String> cookieMap = new HashMap<>();
		cookieMap.put("Cookie", robotCache.getCookie());
		HttpClientUtils.setHeader(cookieMap);
		String response = HttpClientUtils.doPost(WechatApiURL.SEND_MSG, JSONObject.toJSONString(sengMsg));
		logger.debug("发送消息响应：{}", response);
		JSONObject responseObj = JSONObject.parseObject(response);
		if (RetCode.NORMAL.getCode() != responseObj.getIntValue("Ret")) {
			logger.error("消息发送失败");
		} else {
			logger.debug("消息发送成功");
		}
	}
	

	@Override
	@SneakyThrows
	public void getContacts() {
		LoginSession loginSession = robotCache.getLoginSession();
		String url = UrlFormatUtil.format(WechatApiURL.GET_CONTACT, loginSession.getPassTicket(), Clock.now(),loginSession.getSKey());
		Map<String, String> cookieMap = new HashMap<>();
		cookieMap.put("Cookie", robotCache.getCookie());
		HttpClientUtils.setHeader(cookieMap);
		String response = HttpClientUtils.doGet(url, Maps.newHashMap());
		logger.debug("联系人响应：{}", response);
		ContactsResponse contacts = JSONObject.parseObject(response, ContactsResponse.class);
		if (contacts.getBaseResponse().getRet() != RetCode.NORMAL.getCode()) {
			logger.error("获取联系人失败");
			throw new WechatException("获取联系人失败");
		} else {
			logger.info("共获取到{}位联系人", contacts.getMemberCount());
			logger.debug("已缓存联系人");
			robotCache.setMemberList(contacts.getMemberList());
		}
	}
	
	
	@Override
	@SneakyThrows
	public void logout() {
		LoginSession loginSession = robotCache.getLoginSession();
		String url = UrlFormatUtil.format(WechatApiURL.WX_LOGOUT, loginSession.getSKey());
		String response = HttpClientUtils.doGet(url, Maps.newHashMap());
		logger.debug("退出微信，接收到响应：{}", response);
	}

}
