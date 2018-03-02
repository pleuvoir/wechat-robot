package io.github.pleuvoir.cache;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import io.github.pleuvoir.domain.Contacts;
import io.github.pleuvoir.domain.LoginSession;
import io.github.pleuvoir.exception.RobotException;
import lombok.Setter;

/**
 * 缓存服务
 * <p>说明：在整个微信通信协议中，有一些参数服务器端通信会多次使用，包括但不限于微信登录，拉取消息，心跳检测
 * 以及其他内部通信接口，登录后获取的联系人，个人信息，群组信息等也可以在此缓存。
 * @author pleuvoir
 */
@Setter
@Service
public class RobotCache {

	boolean robotIsAlive = false;

	String uuid;
	
	String redirecturl;
	
	String cookie;
	
	LoginSession loginSession;
	
	private List<Contacts> MemberList;
	
	public List<Contacts> getMemberList() {
		return getSafety("获取所有联系人", MemberList);
	}
	
	public String getCookie() {
		return getSafety("获取cookie", cookie);
	}
	
	public LoginSession getLoginSession() {
		return getSafety("获取loginSession", loginSession);
	}
	
	public String getRedirecturl() {
		return getSafety("获取redirecturl", redirecturl);
	}

	public boolean robotIsAlive() {
		return getSafety("获取机器人运行状态", robotIsAlive);
	}

	public String getUuid() {
		return getSafety("获取UUID", uuid);
	}

	private <T> T getSafety(String desc, T property) {
		if (ObjectUtils.isEmpty(property))
			throw new RobotException("[" + desc + "]缓存失败。");
		return property;
	}

}
