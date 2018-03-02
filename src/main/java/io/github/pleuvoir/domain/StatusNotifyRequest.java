package io.github.pleuvoir.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;


public class StatusNotifyRequest implements Serializable {

	private static final long serialVersionUID = 5725153086739816018L;

	private BaseRequest BaseRequest;

	private Integer Code = 3;

	private String FromUserName;

	private String ToUserName;

	private Integer ClientMsgId;

	@JSONField(name = "BaseRequest")
	public BaseRequest getBaseRequest() {
		return BaseRequest;
	}
	
	@JSONField(name = "Code")
	public Integer getCode() {
		return Code;
	}

	@JSONField(name = "FromUserName")
	public String getFromUserName() {
		return FromUserName;
	}

	@JSONField(name = "ToUserName")
	public String getToUserName() {
		return ToUserName;
	}

	@JSONField(name = "ClientMsgId")
	public Integer getClientMsgId() {
		return ClientMsgId;
	}

	public void setBaseRequest(BaseRequest baseRequest) {
		BaseRequest = baseRequest;
	}

	public void setCode(Integer code) {
		Code = code;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public void setClientMsgId(Integer clientMsgId) {
		ClientMsgId = clientMsgId;
	}

	
}
