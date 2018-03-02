package io.github.pleuvoir.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message implements Serializable {

	private static final long serialVersionUID = 4222989100006170208L;

	private String MsgId;

	private String FromUserName;

	private String ToUserName;

	private int MsgType;

	private String Content;

	private String OriContent;

}
