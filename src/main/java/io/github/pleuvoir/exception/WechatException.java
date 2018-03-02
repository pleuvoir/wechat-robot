package io.github.pleuvoir.exception;

/**
 * 微信异常
 * @author pleuvoir
 */
public class WechatException extends RuntimeException {

	private static final long serialVersionUID = -444831687210200174L;

	public WechatException() {
		super();
	}

	public WechatException(String message) {
		super(message);
	}

	public WechatException(Throwable arg0) {
		super(arg0);
	}

}
