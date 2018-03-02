package io.github.pleuvoir.message.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import io.github.pleuvoir.cache.RobotCache;
import io.github.pleuvoir.domain.Message;
import io.github.pleuvoir.domain.RetCode;
import io.github.pleuvoir.domain.Selector;
import io.github.pleuvoir.domain.SyncCheckResponse;
import io.github.pleuvoir.domain.SyncResponse;
import io.github.pleuvoir.exception.WechatException;
import io.github.pleuvoir.message.handle.MessageHandler;
import io.github.pleuvoir.time.SleepUtil;
import io.github.pleuvoir.wechat.WechatInternalService;
import io.github.pleuvoir.wechat.impl.DefaultWechatService;

@Service
public class DefaultMessageListener implements MessageListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultWechatService.class);
	
	@Autowired
	private WechatInternalService wechatInternalService;
	@Autowired
	private MessageHandler messageHandle;
	@Autowired
	private RobotCache robotCache;
	
	@Override
	public void onEvent() {
		logger.info("[*] 心跳轮询拉取消息开始");
		while (robotCache.robotIsAlive()) {
			SleepUtil.sleep(2);
			SyncCheckResponse syncCheck = wechatInternalService.syncCheck();
			logger.debug(JSON.toJSONString(syncCheck));
			int retCode = syncCheck.getRetcode();
			int selector = syncCheck.getSelector();
			if (retCode == RetCode.NORMAL.getCode()) {
		        //有新消息
		        if (selector == Selector.NEW_MESSAGE.getCode()) {
		        	logger.debug("收到新消息，开始同步消息");
		        	SyncResponse syncResponse = wechatInternalService.sync();
					for (Message message : syncResponse.getAddMsgList()) {
						messageHandle.onNewMessage(message);
		        	}
		        }else{ 
		        	logger.debug("不是收到新消息已忽略");
		        	continue;
		        }
		    } else {
		        throw new WechatException("心跳异常，retCode：" + retCode);
		    }
		}
	}

}
