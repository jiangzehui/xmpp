package com.jzh.news.xmpp;

import java.util.HashMap;

import java.util.Map;

import org.jivesoftware.smack.AccountManager;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;

import org.jivesoftware.smack.Roster;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.google.gson.Gson;
import com.jzh.news.entity.User;

/**
 * <b>function:</b> 利用Smack框架完成 XMPP 协议通信
 * 
 * @author hoojo
 * @createDate 2012-5-22 上午10:28:18
 * @file ConnectionServerTest.java
 * @package com.hoo.smack.conn
 * @project jwchat
 * @blog http://blog.csdn.net/IBM_hoojo
 * @email hoojo_@126.com
 * @version 1.0
 */
public class XmppTool {

	private static Connection connection;
	private static ConnectionConfiguration config;

	 public static String server = "123.207.145.194";
	//public static String server = "127.0.0.1";

	public static boolean create(User user) {
		if (connection == null) {
			init();
		}

		// String strs = user.getUser() + ";" + user.getNickname() + ";"
		// + user.getIcon() + ";" + user.getSex();
		String pswd = user.getPassword();
		user.setPassword(null);
		String strs = new Gson().toJson(user);
		System.out.println(strs);
		AccountManager accountManager = connection.getAccountManager();
		try {
			/**
			 * 创建一个用户boy，密码为boy；你可以在管理员控制台页面http://192.168.8.32:9090/user-
			 * summary.jsp查看用户/组的相关信息，来查看是否成功创建用户
			 */
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", strs);
			map.put("email", "admin@jzh.com");
			accountManager.createAccount(user.getUser(), pswd, map);
			System.out.println(user.getUser() + "\t" + pswd + "在xmpp注册成功");
			return true;
			/** 修改密码 */
			// accountManager.changePassword("abc");
		} catch (XMPPException e) {

			e.printStackTrace();
			return true;
		}
	}

	/**
	 * 登录
	 * 
	 * @param user
	 * @param password
	 */
	public static void login(String user, String password) {

		if (connection == null) {
			init();
		}
		try {
			/** 用户登陆，用户名、密码 */
			connection.login(user, password);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		/** 获取当前登陆用户 */
		fail("User:", connection.getUser());
		addGroup(connection.getRoster(), "我的好友");
		addGroup(connection.getRoster(), "黑名单");
		System.out.println("OK");

	}

	/** * 添加一个组 */
	public static boolean addGroup(Roster roster, String groupName) {
		try {
			roster.createGroup(groupName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// -----------以下不用看-----------------------------------------------------

	private final static void fail(Object o) {
		if (o != null) {
			System.out.println(o);
		}
	}

	private final static void fail(Object o, Object... args) {
		if (o != null && args != null && args.length > 0) {
			String s = o.toString();
			for (int i = 0; i < args.length; i++) {
				String item = args[i] == null ? "" : args[i].toString();
				if (s.contains("{" + i + "}")) {
					s = s.replace("{" + i + "}", item);
				} else {
					s += " " + item;
				}
			}
			System.out.println(s);
		}
	}

	/**
	 * <b>function:</b> 初始Smack对openfire服务器链接的基本配置
	 * 
	 * @author hoojo
	 * @createDate 2012-6-25 下午04:06:42
	 */

	public static void init() {
		try {
			// connection = new XMPPConnection(server);
			// connection.connect();

			/**
			 * 5222是openfire服务器默认的通信端口，你可以登录http://192.168.8.32:9090/
			 * 到管理员控制台查看客户端到服务器端口
			 */
			config = new ConnectionConfiguration(server, 5222);

			/** 是否启用压缩 */
			config.setCompressionEnabled(true);
			/** 是否启用安全验证 */
			config.setSASLAuthenticationEnabled(false);
			/** 是否启用调试 */
			config.setDebuggerEnabled(false);
			// config.setReconnectionAllowed(true);
			// config.setRosterLoadedAtLogin(true);

			/** 创建connection链接 */
			connection = new XMPPConnection(config);
			/** 建立连接 */
			connection.connect();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		// fail(connection);
		// fail(connection.getConnectionID());
	}

}