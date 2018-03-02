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
	
	
	/**
	 * 发送文本消息
	 * <p>说明：发送普通文本消息
	 * @param toUserName 发给谁，username
	 * @param message    消息内容
	 */
	void sendMessage(String toUserName, String message);
	
	
	/**
	 * 给文件助手发送消息
	 * @param message 消息内容
	 */
	void sendMessageTofilehelper(String message);
}
