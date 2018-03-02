package io.github.pleuvoir.smartresponse.impl;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import io.github.pleuvoir.constant.TulingRobot;
import io.github.pleuvoir.exception.RobotException;
import io.github.pleuvoir.smartresponse.SmartResponseService;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service("tulingRobot")
public class TulingRobotService implements SmartResponseService {
	
	private static Logger logger = LoggerFactory.getLogger(TulingRobotService.class);

	@Resource(name="tulingRobotConfig")
	private TulingRobot robotConfig;

	
	@Override
	public String sendMessage(String text) {
		logger.debug("[*] 图灵机器人接收到原始消息：{}，即将发送", text);
		
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(robotConfig.getOpenApi()).append("?key=").append(robotConfig.getApiKey())
													.append("&info=").append(text);
		OkHttpClient client = new OkHttpClient();
		logger.debug("[*] 图灵机器人API地址：{}", urlBuilder.toString());
		Request request = new Request.Builder().url(urlBuilder.toString()).build();
		try (Response response = client.newCall(request).execute()) {
			String rsp = response.body().string();
			logger.debug("[*] 图灵机器人收到响应：{}", rsp);
			return JSON.parseObject(rsp, TulingResponse.class).getText();
		} catch (IOException e) {
			throw new RobotException(e);
		}
	}
	

  	@Data
  	public static class TulingResponse{
  		String code;
  		String text;
  	}
}
