package io.github.pleuvoir.smartresponse;

/**
 * 智能响应
 * <p>通过调用第三方机器人接口，完成消息的回复
 * @author pleuvoir
 *
 */
public interface SmartResponseService {

	/**
	 * 发送消息
	 * @param text 发送给机器人接口的文字内容
	 * @return 响应信息
	 */
	String sendMessage(String text);
}
