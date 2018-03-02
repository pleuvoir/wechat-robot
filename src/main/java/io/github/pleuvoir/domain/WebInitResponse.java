package io.github.pleuvoir.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 微信初始化响应，只保留了些许参数
 * @author pleuvoir
 */
public class WebInitResponse implements Serializable{

	private static final long serialVersionUID = 1400128393780914671L;

	private SyncKey syncKey;

	private User User;

	private String sKey;

	@JSONField(name = "SyncKey")
	public SyncKey getSyncKey() {
		return syncKey;
	}

	public void setSyncKey(SyncKey syncKey) {
		this.syncKey = syncKey;
	}

	@JSONField(name = "User")
	public User getUser() {
		return User;
	}

	public void setUser(User user) {
		User = user;
	}

	@JSONField(name = "SKey")
	public String getsKey() {
		return sKey;
	}

	public void setsKey(String sKey) {
		this.sKey = sKey;
	}

}
