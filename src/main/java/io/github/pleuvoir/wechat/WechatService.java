package io.github.pleuvoir.wechat;

/**
 * 微信服务
 * @author pleuvoir
 */
public interface WechatService {

	/**
	 * 显示登陆二维码，可扫码登录
	 */
	void showQrCode();

	/**
	 * 登录
	 */
	void login();

	/**
	 * 退出
	 */
	void logout();
}
