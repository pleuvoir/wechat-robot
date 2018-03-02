package io.github.pleuvoir.message.handle;

import org.apache.commons.mail.EmailException;
import org.springframework.stereotype.Service;

import io.github.pleuvoir.domain.Message;
import io.github.pleuvoir.email.EmailSender;
import io.github.pleuvoir.utils.WechatUtil;

@Service
public class DefaultMessageHandler extends AbstactMessageHandler {

	@Override
	public void onGroupTextMessage(Message message) {
		logger.info("\r\n接收到群消息：{}", WechatUtil.getClearGroupMessage(message.getContent()));
	}

	@Override
	public void onGroupTextMessageAtMe(Message message) {
		logger.info("\r\n有人@我文本信息，内容：{}", WechatUtil.getClearGroupMessage(message.getContent()));
		replyByRobot(message.getFromUserName(), WechatUtil.getClearGroupMessage(message.getContent()));
	}

	@Override
	public void onPrivateTextMessage(Message message) {
		logger.info("\r\n{}：{}", WechatUtil.getClearNameByUsername(message.getFromUserName()), message.getContent());
		replyByRobot(message.getFromUserName(), message.getContent());
	}

	@Override
	public void onMeSendTextToPerson(Message message) {
		logger.info("\r\n我发送文本信息给[{}]，内容：{}", WechatUtil.getClearNameByUsername(message.getToUserName()), message.getContent());
	}

	@Override
	public void onMeSendTextToFileHelper(Message message) {
		logger.info("\r\n我发送文本信息[文件助手]，内容：{}", message.getContent());
		try {
			EmailSender.sendTextToMe("[文件助手消息转发]", message.getContent());
		} catch (EmailException e) {
			logger.error("发送邮件失败，可能是授权码过期了，请重新配置公共邮箱。{}", e);
		}
	}

}
