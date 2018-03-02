package io.github.pleuvoir.domain;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class SyncKey implements Serializable {

	private static final long serialVersionUID = -3063355147820614097L;

	private Integer count;

	private List<SyncKeyPair> list;

	@JSONField(name = "Count")
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@JSONField(name = "List")
	public List<SyncKeyPair> getList() {
		return list;
	}

	public void setList(List<SyncKeyPair> list) {
		this.list = list;
	}

}
