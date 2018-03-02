package io.github.pleuvoir.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginSession implements Serializable {

	private static final long serialVersionUID = 4917752846879171558L;
	
	private String userName;
	private String nickName;
	private String url;
	private String deviceId;
	private String sKey;
	private String wxSid;
	private String wxUin;
	private String passTicket;
	private String syncKeyStr;
	private Integer inviteStartCount;
	private BaseRequest baseRequest;
	private SyncKey syncKey;

	/**
	 * 服务端返回时，同时设置对应的syncKeyStr，方便后面取
	 */
	public void setSyncKey(SyncKey syncKey) {
		this.syncKey = syncKey;
		StringBuilder syncKeyBuf = new StringBuilder();
		for (SyncKeyPair item : syncKey.getList()) {
			syncKeyBuf.append("|").append(item.getKey()).append("_").append(item.getVal());
		}
		if (syncKeyBuf.length() > 0) {
			this.syncKeyStr = syncKeyBuf.substring(1);
		}
	}

}
