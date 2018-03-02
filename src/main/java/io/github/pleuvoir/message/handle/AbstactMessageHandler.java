package io.github.pleuvoir.message.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSON;
import io.github.pleuvoir.domain.Message;
import io.github.pleuvoir.factory.RobotRouteFactory;
import io.github.pleuvoir.utils.WechatUtil;
import io.github.pleuvoir.wechat.WechatService;


public abstract class AbstactMessageHandler implements MessageHandler {

	protected static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	
	@Autowired
	private RobotRouteFactory robotRouteFactory;
	@Autowired
	private WechatService wechatService;
	
	@Override
	public void onNewMessage(Message msg) {
		int msgType = msg.getMsgType();					//消息类型
		if (msgType == 1) {
			//普通文字消息处理
			textMessageHandle(msg);
		}else{
			//有可能是图片，视频等其他消息，暂不处理
			logger.debug("有可能是图片，视频等其他消息，暂不处理");
		}
	}
	
	
	private void textMessageHandle(Message msg) {
		String toUserName = msg.getToUserName(); 		// 发给谁的
		String fromUserName = msg.getFromUserName(); 	// 谁发的
		String content = msg.getContent(); 				// 消息内容
		
		// 如果是群聊消息
		if (WechatUtil.isGroupMessage(fromUserName)) {
			// 从带有其他符号的内容中获得真实的消息内容
			String clearGroupMessage = WechatUtil.getClearGroupMessage(content);
			// 如果有人@我
			if (WechatUtil.isAtMe(clearGroupMessage)) {
				onGroupTextMessageAtMe(msg);
			} else {
				onGroupTextMessage(msg);
			}
			return;
		}
		
		// 如果是别人私聊我
		if (WechatUtil.isPrivateMessage(toUserName)) {
			onPrivateTextMessage(msg);
			return;
		}
		
		// 如果是我发消息给文件助手
		if(WechatUtil.isMeChatToFileHelper(fromUserName, toUserName)){
			onMeSendTextToFileHelper(msg);
			return;
		}
		
		// 如果是我私聊别人
		if(WechatUtil.isMeChatToPerson(fromUserName)){
			onMeSendTextToPerson(msg);
			return;
		}
		
		logger.warn("这是一个神奇的消息，到了这里的到底是什么？{}", JSON.toJSON(msg));
	}
	
	
	/**
	 * 调用智能机器人完成消息的回复
	 * @param toUserName	收件人
	 * @param content		待发送的内容
	 */
	protected void replyByRobot(String toUserName, String content) {
		String response = robotRouteFactory.route().sendMessage(content);
		wechatService.sendMessage(toUserName, response);
	}

}
