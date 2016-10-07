package com.yyquan.jzh.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.view.DialogView;
import com.yyquan.jzh.xmpp.XmppTool;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class ChangePasswordActivity extends Activity implements View.OnClickListener {

    LinearLayout ll_back;
    LinearLayout ll_enter;
    EditText et_yuan_password;
    EditText et_new_password;
    EditText et_new_password_again;
    Intent intent;
    String yuan_password;
    String user;
    String url = Ip.ip + "/YfriendService/DoGetUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_message);
        DialogView.Initial(this, "正在修改密码......");
        intent = getIntent();
        yuan_password = intent.getStringExtra("password");
        user = intent.getStringExtra("user");
        ll_back = (LinearLayout) findViewById(R.id.change_password_layout_back);
        ll_enter = (LinearLayout) findViewById(R.id.change_password_layout_enter);
        et_yuan_password = (EditText) findViewById(R.id.change_password_editText_yuan_password);
        et_new_password = (EditText) findViewById(R.id.change_password_editText_new_password);
        et_new_password_again = (EditText) findViewById(R.id.change_password_editText_new_password_again);
        ll_back.setOnClickListener(this);
        ll_enter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password_layout_back:
                finish();
                break;

            case R.id.change_password_layout_enter:
                final String new_password = et_new_password.getText().toString();
                String new_password_again = et_new_password_again.getText().toString();
                if (!yuan_password.equals(et_yuan_password.getText().toString())) {
                    Toast.makeText(this, "原密码输入不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (new_password.length() < 6 || new_password_again.length() < 6) {
                    Toast.makeText(this, "新密码的长度不能小于6", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!new_password.equals(new_password_again)) {
                    Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogView.show();
                ll_enter.setEnabled(false);
                if (XmppTool.getInstance().changePassword(new_password)) {
                    RequestParams params = new RequestParams();
                    params.put("user", user);
                    params.put("password", new_password);
                    params.put("action", "update_password");
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String str = new String(responseBody);
                            if (str != null) {
                                try {
                                    ll_enter.setEnabled(true);
                                    JSONObject object = new JSONObject(str);
                                    if (object.getString("code").equals("success")) {
                                        User user = SaveUserUtil.loadAccount(ChangePasswordActivity.this);
                                        user.setPassword(new_password);
                                        SaveUserUtil.saveAccount(ChangePasswordActivity.this, user);
                                        Toast.makeText(ChangePasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                        DialogView.dismiss();
                                        finish();
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "密码修改失败，请重试", Toast.LENGTH_SHORT).show();
                                        DialogView.dismiss();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    DialogView.dismiss();
                                    ll_enter.setEnabled(true);
                                }
                            } else {
                                DialogView.dismiss();
                                ll_enter.setEnabled(true);
                            }
                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(ChangePasswordActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                            ll_enter.setEnabled(true);
                            DialogView.dismiss();
                        }
                    });
                } else {
                    DialogView.dismiss();
                    ll_enter.setEnabled(true);
                    Toast.makeText(ChangePasswordActivity.this, "密码修改失败，请重试。", Toast.LENGTH_SHORT).show();
                }


                break;
        }
    }
}
