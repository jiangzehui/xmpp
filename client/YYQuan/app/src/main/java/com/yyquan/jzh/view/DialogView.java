package com.yyquan.jzh.view;

import android.content.Context;
import android.graphics.Color;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by jzh on 2015/11/2.
 */
public class DialogView {

    public static SweetAlertDialog pDialog;

    public static void Initial(Context context, String message) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#1ed6ff"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);

    }

    public static void show() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }

    }

    public static void dismiss() {

        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }

    }
}
