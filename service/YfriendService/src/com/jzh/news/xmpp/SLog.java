package com.jzh.news.xmpp;

public class SLog {

	private static final boolean isTest = true;
	private static final String TAG = "SmackDemo";

	public static void i(String tag, String msg) {
		if (isTest)
			System.out.println(tag + "==>" + msg);

	}

	public static void e(String tag, String msg) {
		if (isTest)
			System.out.println(tag + "==>" + msg);
	}

}
