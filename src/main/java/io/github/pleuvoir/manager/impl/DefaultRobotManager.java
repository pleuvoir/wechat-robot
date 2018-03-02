package io.github.pleuvoir.manager.impl;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import io.github.pleuvoir.cache.RobotCache;
import io.github.pleuvoir.manager.RobotManager;
import io.github.pleuvoir.message.listener.MessageListener;
import io.github.pleuvoir.wechat.WechatService;

@Service
public class DefaultRobotManager implements RobotManager, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(DefaultRobotManager.class);

	@Autowired
	private RobotCache robotCache;
	@Autowired
	private WechatService wechatService;
	@Autowired
	private MessageListener messageListener;

	@Override
	public void start() throws NotFoundException, IOException, WriterException {
		logger.info("[*] 开始启动机器人");
		wechatService.login();
		logger.info("[*] 机器人启动完成");
		robotCache.setRobotIsAlive(true);
		logger.info("[*] 开启消息监听");
		messageListener.onEvent();
	}

	@Override
	public void destroy() {
		logger.info("[*] 开始关闭机器人");
		if (robotIsAlive()) {
			wechatService.logout();
		}
		logger.info("[*] 关闭机器人完成");
	}

	@Override
	public boolean robotIsAlive() {
		return robotCache.robotIsAlive();
	}

}
