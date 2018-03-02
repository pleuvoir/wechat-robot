package io.github.pleuvoir.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 4359311730485417201L;

	private Integer Ret;

	@JSONField(name = "Ret")
	public Integer getRet() {
		return Ret;
	}

	public void setRet(Integer ret) {
		Ret = ret;
	}
	
	
}
