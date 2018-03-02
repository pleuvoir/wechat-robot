package io.github.pleuvoir.message.handle;

import io.github.pleuvoir.domain.Message;

/**
 * 消息处理接口
 * @author pleuvoir
 *
 */
public interface MessageHandle {
	
	/**
	 * 当收到消息时触发
	 */
	void onNewMessage(Message message);

	/**
	 * 当收到文本群聊消息时触发
	 */
	void onGroupTextMessage(Message message);
	
	/**
	 * 当收到文本群聊消息并且有人@我时触发
	 */
	void onGroupTextMessageAtMe(Message message);
	
	/**
	 * 当收到私聊时触发
	 */
	void onPrivateTextMessage(Message message);
	
	/**
	 * 当我私聊别人文本消息时触发
	 */
	void onMeSendTextToPerson(Message message);
	
}
