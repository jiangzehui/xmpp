package com.yyquan.jzh.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.location.Location;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.util.ToastUtil;
import com.yyquan.jzh.view.DialogView;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cz.msebera.android.httpclient.Header;


public class LoginActivity extends Activity implements View.OnClickListener, PlatformActionListener {

    TextView tv_phone_regster;
    EditText et_user;
    EditText et_password;
    TextView tv_login;
    TextView tv_password;

    private String url = Ip.ip + "/YfriendService/DoGetUser";

    String user = "";
    String password = "";
    RadioButton rb_qq;
    RadioButton rb_weibo;
    Location lt;


    private final int XMPP_LOGIN = 3;
    private final int CREATE_USER = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ShareSDK.initSDK(this);
        lt = new Location(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initialView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lt.stopLocation();
    }

    /**
     * 初始化控件
     */
    private void initialView() {
        DialogView.Initial(LoginActivity.this, "正在登录......");
        tv_phone_regster = (TextView) findViewById(R.id.login_textview_phone_regster);
        tv_password = (TextView) findViewById(R.id.login_textview_forget_password);
        et_user = (EditText) findViewById(R.id.login_editText_user);
        et_password = (EditText) findViewById(R.id.login_editText_password);
        tv_login = (TextView) findViewById(R.id.login_textview_enter);
        rb_qq = (RadioButton) findViewById(R.id.login_imageView_qqlogin);
        rb_weibo = (RadioButton) findViewById(R.id.login_imageView_weibologin);

        tv_phone_regster.setOnClickListener(this);
        tv_password.setOnClickListener(this);
        tv_login.setOnClickListener(this);
        rb_qq.setOnClickListener(this);
        rb_weibo.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_textview_phone_regster:
                gotoPhoneRegster("regster");
                break;
            case R.id.login_textview_forget_password:
                gotoPhoneRegster("password");
                break;

            case R.id.login_imageView_qqlogin:
                tv_login.setEnabled(false);
                DialogView.show();
                qq_login();
                break;
            case R.id.login_imageView_weibologin:
                tv_login.setEnabled(false);
                DialogView.show();
                weibo_login();
                break;
            case R.id.login_textview_enter:
                user = et_user.getText().toString();
                password = et_password.getText().toString();
                if (user.equals("") || password.equals("")) {
                    ToastUtil.show(LoginActivity.this, "用户名或密码不能为空");
                    return;
                }
                DialogView.show();
                tv_login.setEnabled(false);
                login(user, password);
                break;
        }
    }

    /**
     * 新浪微博登录
     */
    private void weibo_login() {
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (weibo.isValid()) {
            weibo.removeAccount();
        }

        weibo.setPlatformActionListener(this); // 设置分享事件回调
        weibo.SSOSetting(true); // 设置false表示使用SSO授权方式
        // weibo.authorize();
        weibo.showUser(null);

    }

    /**
     * QQ登录
     */
    private void qq_login() {
        Platform weibo = ShareSDK.getPlatform(QQ.NAME);
        if (weibo.isValid()) {
            weibo.removeAccount();//删除前一次的授权信息
        }

        weibo.setPlatformActionListener(this); // 设置分享事件回调

        // 设置false表示使用SSO授权方式，意思就是如果有QQ就直接跳到QQ授权，如果没有就跳到网页
        weibo.SSOSetting(false);


        weibo.showUser(null);
    }

    /**
     * 跳转到手机注册界面
     */
    private void gotoPhoneRegster(String type) {
        Intent it = new Intent(this, PhoneRegsterActivity.class);
        it.putExtra("type", type);
        startActivity(it);
    }


    long newTime;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - newTime > 2000) {
            newTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        Log.i("login","chenggong");
        Log.i("login",platform.getName()+"");
        Log.i("login",hashMap.toString());
        Log.i("login", platform.getDb().getUserId()+"");

        Message m = new Message();
        m.what = 1;
        m.obj = new Object[]{platform.getName(), hashMap, platform.getDb().getUserId()};
        h.sendMessage(m);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.i("login","失败");
        Log.i("login",i+"");
        Log.i("login",throwable.getMessage());
        tv_login.setEnabled(true);
        DialogView.dismiss();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        tv_login.setEnabled(true);
        DialogView.dismiss();
    }

    Handler h = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:

                    Object[] objs = (Object[]) msg.obj;
                    String platform = (String) objs[0];
                    String id = (String) objs[2];
                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                    // Toast.makeText(LoginActivity.this,res.toString(),Toast.LENGTH_SHORT).show();
                    if (platform.equals("QQ")) {
                        isRegster(id, res, "QQSJHAAJSHAJSH");
                    } else if (platform.equals("SinaWeibo")) {
                        isRegster(id, res, "SINAHKSJDHSKDH");
                    }

                    break;


                case XMPP_LOGIN:

                    final User users = (User) msg.obj;
                    new Thread() {

                        @Override
                        public void run() {
                            boolean result = XmppTool.getInstance().login(users.getUser(), users.getPassword(), LoginActivity.this);
                            if (result) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SaveUserUtil.saveAccount(LoginActivity.this, users);
                                        startService(new Intent(LoginActivity.this, XmppService.class));
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("user", users);
                                        regster_push(users.getUser());
                                        SharedPreferencesUtil.setBoolean(LoginActivity.this, "user_message", "login", true);
                                        startActivity(intent);
                                        finish();
                                        DialogView.dismiss();
                                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();


                                    }
                                });

                            } else {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogView.dismiss();
                                        tv_login.setEnabled(true);
                                        Toast.makeText(LoginActivity.this, "登陆失败,请重试", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }

                    }.start();


                    break;
                case CREATE_USER:
                    User use = (User) msg.obj;
                    regster(use);
                    break;


            }
        }

    };


    /**
     * 登录
     */
    private void login(String user, String password) {
        RequestParams params = new RequestParams();
        params.put("user", user);
        params.put("password", password);
        params.put("action", "login");
        AsyncHttpClient client = new AsyncHttpClient();
        client.setConnectTimeout(5000);
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
                            Message m = h.obtainMessage(XMPP_LOGIN);
                            m.obj = user;
                            h.sendMessage(m);

                        } else {
                            DialogView.dismiss();
                            tv_login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "账号或密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tv_login.setEnabled(true);
                        DialogView.dismiss();
                    }
                } else {
                    tv_login.setEnabled(true);
                    DialogView.dismiss();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                DialogView.dismiss();
                tv_login.setEnabled(true);
                Toast.makeText(LoginActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 判断该账号是否已注册
     */
    private void isRegster(final String users, final HashMap<String, Object> objs, final String type) {

        RequestParams params = new RequestParams();
        params.put("user", users);
        params.put("action", "search");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            // Intent intent = new Intent(LoginActivity.this, QQRegsterActivity.class);
                            User user = new User();
                            user.setUser(users);
                            if (type.equals("QQSJHAAJSHAJSH")) {
                                user.setNickname((String) objs.get("nickname"));
                                user.setCity((String) objs.get("province") + (String) objs.get("city"));
                                user.setSex((String) objs.get("gender"));
                                user.setIcon((String) objs.get("figureurl_qq_2"));
                                user.setPassword("QQSJHAAJSHAJSH");

                            } else if (type.equals("SINAHKSJDHSKDH")) {
                                user.setNickname((String) objs.get("name"));
                                user.setCity((String) objs.get("location"));
                                user.setPassword("SINAHKSJDHSKDH");
                                String sex = (String) objs.get("gender");
                                if (sex.equals("f")) {
                                    user.setSex("女");
                                } else {
                                    user.setSex("男");
                                }
                                user.setIcon((String) objs.get("avatar_large"));

                            }

                            Message m = h.obtainMessage(CREATE_USER);
                            m.obj = user;
                            h.sendMessage(m);


                        } else if (object.getString("code").equals("failure")) {

                            login(users, type);
                            //  Toast.makeText(LoginActivity.this, "该账号已注册", Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } else {

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();

            }
        });


    }


    /**
     * 注册
     */
    private void regster(final User user) {
        RequestParams params = new RequestParams();
        params.put("user", user.getUser());
        params.put("nickname", user.getNickname());
        params.put("password", user.getPassword());
        params.put("sex", user.getSex());
        params.put("icon", user.getIcon());
        params.put("location", lt.location);
        if(user.getCity()==null||user.getCity().equals("")){
            user.setCity("未知星球");
        }
        params.put("city", user.getCity());
        params.put("years", "");
        params.put("qq", "");
        params.put("action", "save");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            Toast.makeText(LoginActivity.this, "初次登录注册成功", Toast.LENGTH_SHORT).show();
                            Message m = h.obtainMessage(XMPP_LOGIN);
                            m.obj = user;
                            h.sendMessage(m);

                        } else {

                            Toast.makeText(LoginActivity.this, "初次登录注册失败，请重试", Toast.LENGTH_SHORT).show();
                            tv_login.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tv_login.setEnabled(true);
                    }
                } else {
                    tv_login.setEnabled(true);

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                tv_login.setEnabled(true);
                DialogView.dismiss();
            }
        });
    }

    /**
     * 注册信鸽
     */
    private void regster_push(String user) {
        XGPushManager.registerPush(getApplicationContext(), user, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                // Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(Object o, int i, String s) {
                //  Toast.makeText(LogoActivity.this, "注册失败" + i + "\n" + s, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
