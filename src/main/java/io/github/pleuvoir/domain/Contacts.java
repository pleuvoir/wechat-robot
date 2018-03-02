package io.github.pleuvoir.domain;

import java.io.Serializable;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

public class Contacts implements Serializable {

	private static final long serialVersionUID = 4388357583913144713L;

	private String UserName;
	private String NickName;
	private String RemarkName;
	
	
	public String getClearName() {
		return !TextUtils.isBlank(RemarkName) ? RemarkName : NickName;
	}

	@JSONField(name = "UserName")
	public String getUserName() {
		return UserName;
	}

	@JSONField(name = "NickName")
	public String getNickName() {
		return NickName;
	}

	@JSONField(name = "RemarkName")
	public String getRemarkName() {
		return RemarkName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public void setNickName(String nickName) {
		NickName = nickName;
	}

	public void setRemarkName(String remarkName) {
		RemarkName = remarkName;
	}

}
