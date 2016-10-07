package com.yyquan.jzh.util;

import android.util.Log;

public class SLog {
	
	private static final boolean isTest = true;
	private static final String TAG = "SmackDemo";
	
	public static void i(String tag , String msg){
		if (isTest) 
			Log.i(TAG, tag+"==>"+msg);
	}
	
	public static void e(String tag , String msg){
		if (isTest) 
			Log.e(TAG,tag+"==>"+msg);
	}
	

}
