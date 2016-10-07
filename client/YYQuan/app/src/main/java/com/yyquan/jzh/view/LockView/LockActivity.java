package com.yyquan.jzh.view.LockView;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.LoginActivity;
import com.yyquan.jzh.activity.MainActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.view.DialogView;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * 图形解锁类
 *
 * @author jiangzehui
 */
public class LockActivity extends Activity implements
        LockPatternView.OnPatternListener {
    private static final String TAG = "LockActivity";
    private String url = Ip.ip + "/YfriendService/DoGetUser";
    private List<LockPatternView.Cell> lockPattern;
    private LockPatternView lockPatternView;

    TextView tv;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SharedPreferences preferences = getSharedPreferences("user_message",
                MODE_PRIVATE);
        String patternString = preferences.getString("lock_password", null);
        if (patternString == null) {
            finish();
            return;
        }


        lockPattern = LockPatternView.stringToPattern(patternString);
        setContentView(R.layout.activity_lock);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        tv = (TextView) findViewById(R.id.lock_text);
        // 监听解锁界面右下角的账号登录
        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LockActivity.this, LoginActivity.class));
                finish();

            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // disable back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        Log.d(TAG, "onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
        Log.d(TAG, "onPatternCellAdded");
        Log.e(TAG, LockPatternView.patternToString(pattern));
        // Toast.makeText(this, LockPatternView.patternToString(pattern),
        // Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        Log.d(TAG, "onPatternDetected");

        if (pattern.equals(lockPattern)) {
            lodingData();
            // startActivity(new Intent(this, MainActivity.class));

            // finish();
        } else {
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            Toast.makeText(this, R.string.lockpattern_error, Toast.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * 验证用户名和密码是否正确
     *
     * @param
     * @param
     */
    private void lodingData() {

        User user = SaveUserUtil.loadAccount(LockActivity.this);
        login(user.getUser(), user.getPassword());

//

    }

    /**
     * 登录
     */
    private void login(String user, String password) {

        RequestParams params = new RequestParams();
        params.put("user", user);
        params.put("password", password);
        params.put("action", "login");
        AsyncHttpClient client = new AsyncHttpClient();

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {

                            object = object.getJSONObject("data");
                            User user = new User();
                            user.setUser(object.getString("user"));
                            user.setPassword(object.getString("password"));
                            user.setQq(object.getString("qq"));
                            user.setIcon(object.getString("icon"));
                            user.setNickname(object.getString("nickname"));
                            user.setCity(object.getString("city"));
                            user.setSex(object.getString("sex"));
                            user.setYears(object.getString("years"));
                            user.setQianming(object.getString("qianming"));
                            Message m = h.obtainMessage(1);
                            m.obj = user;
                            h.sendMessage(m);


                        } else {
                            startActivity(new Intent(LockActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(LockActivity.this, "账号或密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        startActivity(new Intent(LockActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(LockActivity.this, LoginActivity.class));
                    finish();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LockActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LockActivity.this, LoginActivity.class));
                finish();
            }
        });
    }


    Handler h = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {


                case 1:

                    final User users = (User) msg.obj;
                    new Thread() {

                        @Override
                        public void run() {
                            boolean result = XmppTool.getInstance().login(users.getUser(), users.getPassword(), LockActivity.this);
                            if (result) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//
                                        startService(new Intent(LockActivity.this, XmppService.class));
                                        Intent intent = new Intent(LockActivity.this, MainActivity.class);
                                        intent.putExtra("user", users);
                                        SaveUserUtil.saveAccount(LockActivity.this, users);
                                        SharedPreferencesUtil.setBoolean(LockActivity.this, "user_message", "login", true);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(LockActivity.this, "登陆成功", Toast.LENGTH_LONG).show();


                                    }
                                });

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LockActivity.this, "登陆失败,请重试", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LockActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                });

                            }
                        }

                    }.start();


                    break;


            }
        }

    };


}
