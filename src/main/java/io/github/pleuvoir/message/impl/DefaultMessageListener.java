package io.github.pleuvoir.message.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import io.github.pleuvoir.domain.Message;
import io.github.pleuvoir.domain.RetCode;
import io.github.pleuvoir.domain.Selector;
import io.github.pleuvoir.domain.SyncCheckResponse;
import io.github.pleuvoir.domain.SyncResponse;
import io.github.pleuvoir.exception.WechatException;
import io.github.pleuvoir.factory.RobotRouteFactory;
import io.github.pleuvoir.message.MessageListener;
import io.github.pleuvoir.utils.SleepUtil;
import io.github.pleuvoir.utils.WechatUtil;
import io.github.pleuvoir.wechat.WechatInternalService;
import io.github.pleuvoir.wechat.impl.DefaultWechatService;

@Service
public class DefaultMessageListener implements MessageListener {

	private static Logger logger = LoggerFactory.getLogger(DefaultWechatService.class);
	
	@Autowired
	private WechatInternalService wechatInternalService;
	@Autowired
	private RobotRouteFactory robotRouteFactory;
	
	
	@Override
	public String receive() {
		logger.info("[*] 心跳轮询拉取消息开始");
		pullMessage();
		return null;
	}
	
	
	private void pullMessage(){
		while (true) {
			SleepUtil.sleep(2000L);
			SyncCheckResponse syncCheck = wechatInternalService.syncCheck();
			logger.debug(JSON.toJSONString(syncCheck));
			int retCode = syncCheck.getRetcode();
			int selector = syncCheck.getSelector();
			if (retCode == RetCode.NORMAL.getCode()) {
		        //有新消息
		        if (selector == Selector.NEW_MESSAGE.getCode()) {
		        	logger.debug("收到新消息，开始同步消息");
		        	SyncResponse syncResponse = wechatInternalService.sync();
					for (Message msg : syncResponse.getAddMsgList()) {
		        		int msgType = msg.getMsgType();					//消息类型
		        		String toUserName = msg.getToUserName();		//发给谁的
		        		String fromUserName = msg.getFromUserName();	//谁发的
		        		String content = msg.getContent();				//消息内容
		        		
		        		//普通消息 
						if (msgType == 1) {
							if (WechatUtil.isGroupMessage(fromUserName)) {
								String clearGroupMessage = WechatUtil.getClearGroupMessage(content);
								logger.info("\r\n接收到群消息：{}", clearGroupMessage);
								
								if(WechatUtil.isAtMe(clearGroupMessage)) {
									logger.info("有人@我，回复");
									//wechatInternalService.sendMessage(fromUserName, "您好，我现在有事不在。请稍后联系。");
									onNewMessage(fromUserName, clearGroupMessage);
								}
								continue;
							}
							
							if (WechatUtil.isPrivateMessage(toUserName)) {	
								logger.info("\r\n{}：{}", WechatUtil.getClearNameByUsername(fromUserName), content);
								 //发送消息
								onNewMessage(fromUserName, content);
								continue;
							}
						}else if(msgType == 10002) { 
							logger.info("神秘人物撤回了一条消息: {}", content);
							//可以将这个消息再次发送出去
							
						}else{
							logger.debug("非普通消息已忽略");
							continue;
						}
						
		        	}
		        }else{ 
		        	logger.info("不是收到新消息已忽略");
		        	continue;
		        }
		    } else {
		        throw new WechatException("心跳异常，retCode：" + retCode);
		    }
		}
	}
	
	
	/**
	 * 回复消息
	 */
	private void onNewMessage(String toUserName,String content) {
		//调用智能机器人
		String response = robotRouteFactory.route().sendMessage(content);
		wechatInternalService.sendMessage(toUserName, response);
	}
	

}
