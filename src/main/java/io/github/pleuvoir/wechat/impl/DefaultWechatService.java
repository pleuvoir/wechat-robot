package io.github.pleuvoir.wechat.impl;

import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.time.SleepUtil;
import io.github.pleuvoir.utils.QRCodeUtils;
import io.github.pleuvoir.wechat.WechatInternalService;
import io.github.pleuvoir.wechat.WechatService;
import lombok.SneakyThrows;

@Service
public class DefaultWechatService implements WechatService {

	private static Logger logger = LoggerFactory.getLogger(DefaultWechatService.class);
	
	@Autowired
	private WechatInternalService wechatInternalService;
	
	@Override
	public void login() {
		logger.info("登录微信");
		showQrCode();
		logger.info("[*] 开始轮询检查登录状态");
		while (true) {
			SleepUtil.sleep(3);
			if(wechatInternalService.checkLogin()) break;
		}
		logger.info("[*] 轮询检查登录状态完成");
		
		logger.info("[*] 开始准备登录参数");
		wechatInternalService.initLoginSession();
		logger.info("[*] 登录参数已准备就绪");
		
		logger.info("[*] 开始初始化微信");
		wechatInternalService.wechatInit();
		logger.info("[*] 微信初始化完成");
		
		logger.info("[*] 开启微信通知");
		wechatInternalService.statusNotify();
		logger.info("[*] 微信通知开启完成");
		
		logger.info("[*] 开始获取联系人");
		wechatInternalService.getContacts();
		logger.info("[*] 获取联系人完成");
	}
	
	
	@Override
	public void logout() {
		logger.info("准备退出微信");
		wechatInternalService.logout();
		logger.info("微信已退出，再见");
	}


	@Override
	@SneakyThrows
	public void showQrCode() {
		logger.debug("[*] 开始获取二维码UUID");
		wechatInternalService.getUUID();
		logger.debug("[*] 获取二维码UUID完成");
		logger.info("[*] 开始获取二维码并显示");
		ByteArrayInputStream stream = new ByteArrayInputStream(wechatInternalService.getQrcode());
        String qrUrl = QRCodeUtils.decode(stream);
        stream.close();
        String qr = QRCodeUtils.generateQR(qrUrl, 40, 40);
        logger.info("\r\n" + qr);
		logger.info("[*] 二维码已显示，请注意扫描");
	}

}
