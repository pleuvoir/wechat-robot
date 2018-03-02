package io.github.pleuvoir.constant;

/**
 * 微信服务端内部通信接口地址
 * @author pleuvoir
 */
public interface WechatApiURL {

	//获取UUID的接口
	String GET_UUID 				= "https://login.weixin.qq.com/jslogin"; 			
	
	//显示二维码
	String SHOW_QRCODE 				= "https://login.weixin.qq.com/qrcode/"; 							
	
	//等待登陆接口
	String WAIT_LOGIN 				= "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login"; 			
	
	//微信初始化 
	String WEBWX_INIT 				= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit?r=-{0}&pass_ticket={1}"; 				
	
	//开启微信状态通知
	String STATUS_NOTIFY 			= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify?lang=zh_CN&pass_ticket={0}"; 		
	
	//获取好友列表
	String GET_CONTACT 				= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact?lang=zh_CN&pass_ticket={0}&r={1}&seq=0&skey={2}"; 			
									   
	//获取微信群组
	String GET_GROUP_CONTACT 		= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact?type=ex&r={0}&lang=zh_CN&pass_ticket={1}"; 
	
	//消息检查
	String SYNC_CHECK 				= "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck"; 		
	
	//获取最新消息
	String WEBWX_SYNC 				= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync?sid={0}&skey={1}"; 				
	
	//发送消息
	String SEND_MSG 				= "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg";
	
	//退出微信
	String WX_LOGOUT                = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxlogout?redirect=1&type=0&skey={0}";
}
