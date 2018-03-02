package io.github.pleuvoir.domain;

import java.io.Serializable;
import com.alibaba.fastjson.annotation.JSONField;

public class SyncResponse implements Serializable {

	private static final long serialVersionUID = -468224859346129692L;

	private SyncKey syncKey;

	private int AddMsgCount;

	private Message[] AddMsgList;

	@JSONField(name = "SyncKey")
	public SyncKey getSyncKey() {
		return syncKey;
	}

	public void setSyncKey(SyncKey syncKey) {
		this.syncKey = syncKey;
	}

	@JSONField(name = "AddMsgCount")
	public int getAddMsgCount() {
		return AddMsgCount;
	}

	public void setAddMsgCount(int addMsgCount) {
		AddMsgCount = addMsgCount;
	}

	@JSONField(name = "AddMsgList")
	public Message[] getAddMsgList() {
		return AddMsgList;
	}

	public void setAddMsgList(Message[] addMsgList) {
		AddMsgList = addMsgList;
	}
	
	
	
	

}
