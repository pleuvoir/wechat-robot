package io.github.pleuvoir.wechat;

import io.github.pleuvoir.domain.SyncCheckResponse;
import io.github.pleuvoir.domain.SyncResponse;

/**
 * 微信服务端内部通信接口
 * @author pleuvoir
 */
public interface WechatInternalService {

	/**
	 * 获取二维码UUID
	 * <p>说明: 微信Web版本不使用用户名和密码登录，而是采用二维码登录，所以服务器需要首先分配一个唯一的会话ID，用来标识当前的一次登录
	 */
	void getUUID();
	
	byte[] getQrcode();
	
	/**
	 * 检查微信登录状态，登录成功返回true
	 * <p>说明：因为二维码展示后，微信服务端并不知道用户是否已经扫描 故可以通过请求此接口后获取扫描状态
	 * 当window.code=200时  同时会返回redirectUrl，该地址请求后会返回服务端认证参数，供整个微信登录生命周期使用
	 * <p>此接口需要轮询，直至返回redirectUrl
	 */
	boolean checkLogin();
	
	/**
	 * 准备认证参数，准备完成后会保存至缓存中
	 * <p>说明：该地址请求后会返回服务端认证参数，供整个微信生命周期使用
	 */
	void initLoginSession();
	
	/**
	 * 初始化微信
	 * <p>说明：初始化微信即获取最近联系人，群组信息等。
	 */
	void wechatInit();
	
	/**
	 * 一旦在Web端采取了某些操作，就通知移动端
	 * <p>说明：因为网页版登录后，会关闭微信的通知，所以需要启用。注意：纯属猜的
	 */
	void statusNotify();
	
	/**
	 * 获取微信好友列表
	 */
	void getContacts();
	
	/**
	 * 同步检查消息状态
	 * <p>说明：也可以认为是微信服务端心跳检查，
	 * 消息检查返回码 格式 window.synccheck={retcode:"0",selector:"0"}
	 * retcode = 0 正常；retcode = 1002 cookie失效
	 * selector = 0 正常； selector = 2 新消息； selector = 7 进入/退出聊天界面
	 * 依靠此接口的返回，可以做很多事情。比如当收到消息时调用图灵机器人，并且将图灵机器人的返回内容 再次调用发送消息接口
	 */
	SyncCheckResponse syncCheck();
	
	/**
	 * 同步微信服务端消息
	 * <p>说明：此接口返回内容中可以获取到新的消息等。
	 * @return
	 */
	SyncResponse sync();
	
	/**
	 * 发送文本消息
	 * <p>说明：发送普通文本消息
	 * @param toUserName 发给谁，username
	 * @param message    消息内容
	 */
	void sendMessage(String toUserName, String message);
	
	/**
	 * 退出微信
	 */
	void logout();
}
