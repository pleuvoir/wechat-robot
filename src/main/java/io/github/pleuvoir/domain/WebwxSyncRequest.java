package io.github.pleuvoir.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;


public class WebwxSyncRequest implements Serializable {

	private static final long serialVersionUID = -3238993720537786444L;

	private BaseRequest BaseRequest;

	private SyncKey SyncKey;

	private Integer rr;

	@JSONField(name = "BaseRequest")
	public BaseRequest getBaseRequest() {
		return BaseRequest;
	}

	public void setBaseRequest(BaseRequest baseRequest) {
		BaseRequest = baseRequest;
	}

	@JSONField(name = "SyncKey")
	public SyncKey getSyncKey() {
		return SyncKey;
	}

	public void setSyncKey(SyncKey syncKey) {
		SyncKey = syncKey;
	}

	@JSONField(name = "rr")
	public Integer getRr() {
		return rr;
	}

	public void setRr(Integer rr) {
		this.rr = rr;
	}

}
