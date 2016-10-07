package com.yyquan.jzh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2015/11/24.
 */
public abstract class BaseActivity extends Activity {

    public boolean full_screen=true;

    /**
     * 初始化控件
     */
    public abstract void initialView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        if(full_screen){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }
}
