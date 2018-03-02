package io.github.pleuvoir.utils;

import java.util.List;

import io.github.pleuvoir.cache.RobotCache;
import io.github.pleuvoir.domain.Contacts;
import io.github.pleuvoir.wrapper.ApplicationContextWrap;

public class WechatUtil {
	
	
	/**
	 * 获取群聊信息实际内容，剔除无用符号
	 * @param content 原始内容
	 * @return
	 */
	public static String getClearGroupMessage(String content) {
		if (content == null) {
			throw new IllegalArgumentException("content");
		}
		return content.replaceAll("^(@([0-9]|[a-z])+):", "").replaceAll("<br/>", "");
	}
	
	/**
	 * 是@我的消息
	 * @param content 消息内容
	 * @return
	 */
	public static boolean isAtMe(String content) {
		String nickName = ApplicationContextWrap.getBean(RobotCache.class).getLoginSession().getNickName();
		return content.contains("@" + nickName);
	}
	
	/**
	 * 是我私聊别人
	 * @param fromUserName
	 * @return
	 */
	public static boolean isMeChatToPerson(String fromUserName){
		String me = ApplicationContextWrap.getBean(RobotCache.class).getLoginSession().getUserName();
		return fromUserName.equals(me);
	}
	
	/**
	 * 是群消息
	 * @param fromUserName 发信人（谁发的）
	 * @return 是群消息返回true
	 */
	public static boolean isGroupMessage(String fromUserName) {
		return fromUserName.startsWith("@@");
	}

	
	/**
	 * 是私聊消息
	 * @param toUserName 发给谁的
	 * @return 是私信返回true
	 */
	public static boolean isPrivateMessage(String toUserName) {
		RobotCache robotCache = ApplicationContextWrap.getBean(RobotCache.class);
		return toUserName.equals(robotCache.getLoginSession().getUserName());
	}
	
	
	/**
	 * 获取名称，如果有备注则返回备注的名称
	 */
	public static String getClearNameByUsername(String username) {
		RobotCache robotCache = ApplicationContextWrap.getBean(RobotCache.class);
		List<Contacts> memberList = robotCache.getMemberList();
		for (Contacts contact : memberList) {
			if (contact.getUserName().equals(username)) {
				return contact.getClearName();
			}
		}
		return "神秘人物";
	}

	/**
	 * 获取名称，如果有备注则返回备注的名称
	 */
	public static String getClearNameByNickname(String nickname) {
		RobotCache robotCache = ApplicationContextWrap.getBean(RobotCache.class);
		List<Contacts> memberList = robotCache.getMemberList();
		for (Contacts contact : memberList) {
			if (contact.getNickName().equals(nickname)) {
				return contact.getClearName();
			}
		}
		return "神秘人物";
	}

}
