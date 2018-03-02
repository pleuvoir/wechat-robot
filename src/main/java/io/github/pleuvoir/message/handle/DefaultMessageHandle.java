package io.github.pleuvoir.message.handle;

import org.springframework.stereotype.Service;

import io.github.pleuvoir.domain.Message;
import io.github.pleuvoir.utils.WechatUtil;

@Service("defaultMessageHandle")
public class DefaultMessageHandle extends AbstactMessageHandle {

	@Override
	public void onGroupTextMessage(Message message) {
		logger.info("\r\n接收到群消息：{}", WechatUtil.getClearGroupMessage(message.getContent()));
	}

	@Override
	public void onGroupTextMessageAtMe(Message message) {
		replyByRobot(message.getFromUserName(), WechatUtil.getClearGroupMessage(message.getContent()));
	}

	@Override
	public void onPrivateTextMessage(Message message) {
		logger.info("\r\n{}：{}", WechatUtil.getClearNameByUsername(message.getFromUserName()), message.getContent());
		replyByRobot(message.getFromUserName(), message.getContent());
	}

	@Override
	public void onMeSendTextToPerson(Message message) {
		logger.info("\r\n我私聊{}文本信息，内容：{}", WechatUtil.getClearNameByUsername(message.getToUserName()), message.getContent());
	}

}
