package io.github.pleuvoir.domain;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ContactsResponse implements Serializable {

	private static final long serialVersionUID = -7039191609011861119L;

	private BaseResponse BaseResponse;

	private List<Contacts> MemberList;
	
	private Integer MemberCount;
	
	@JSONField(name = "MemberCount")
	public Integer getMemberCount() {
		return MemberCount;
	}

	@JSONField(name = "BaseResponse")
	public BaseResponse getBaseResponse() {
		return BaseResponse;
	}

	@JSONField(name = "MemberList")
	public List<Contacts> getMemberList() {
		return MemberList;
	}

	public void setMemberList(List<Contacts> memberList) {
		MemberList = memberList;
	}
	
	public void setBaseResponse(BaseResponse baseResponse) {
		BaseResponse = baseResponse;
	}
	
	public void setMemberCount(Integer memberCount) {
		MemberCount = memberCount;
	}

}
