package com.yyquan.jzh.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/1.
 */
public class ToastUtil {

    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
