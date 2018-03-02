package io.github.pleuvoir.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class SyncKeyPair implements Serializable {

	private static final long serialVersionUID = 4398208949940609727L;

	private Integer key;

	private Integer val;

	@JSONField(name = "Key")
	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	@JSONField(name = "Val")
	public Integer getVal() {
		return val;
	}

	public void setVal(Integer val) {
		this.val = val;
	}

}
