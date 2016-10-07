package com.yyquan.jzh.activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;


import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.view.DataPickerView.OptionsPopupWindow;
import com.yyquan.jzh.view.DataPickerView.TimePopupWindow;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class MyselfMessageActivity extends Activity implements View.OnClickListener {

    TextView tv_id;
    TextView tv_user;
    TextView tv_nickname;
    TextView tv_sex;
    TextView tv_years;
    TextView tv_qq;

    RelativeLayout rl_nickname;
    RelativeLayout rl_sex;
    RelativeLayout rl_years;
    RelativeLayout rl_qq;
    RelativeLayout rl_password;
    LinearLayout ll_back;
    LinearLayout ll_enter;
    User users;
    String url = Ip.ip + "/YfriendService/DoGetUser";
    String user;
    boolean bool;
    AlertDialog.Builder mMaterialDialog;
    TimePopupWindow pwTime;
    OptionsPopupWindow pwOptions;
    private ArrayList<String> options1Items = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_myself_message);
        initialView();
        getData();

    }


    private void initialView() {
        bool = false;
        users = SaveUserUtil.loadAccount(this);
        user = users.getUser();
        tv_id = (TextView) findViewById(R.id.myself_textview_id_value);
        tv_user = (TextView) findViewById(R.id.myself_textview_user_value);
        tv_nickname = (TextView) findViewById(R.id.myself_textview_nickname_value);
        tv_sex = (TextView) findViewById(R.id.myself_textview_sex_value);
        tv_years = (TextView) findViewById(R.id.myself_textview_years_value);
        tv_qq = (TextView) findViewById(R.id.myself_textview_qq_value);
        rl_nickname = (RelativeLayout) findViewById(R.id.myself_layout_nickname);
        rl_sex = (RelativeLayout) findViewById(R.id.myself_layout_sex);
        rl_years = (RelativeLayout) findViewById(R.id.myself_layout_years);
        rl_qq = (RelativeLayout) findViewById(R.id.myself_layout_qq);
        rl_password = (RelativeLayout) findViewById(R.id.myself_layout_password);
        if (users.getPassword().equals("QQSJHAAJSHAJSH") || users.getPassword().equals("SINAHKSJDHSKDH")) {
            rl_password.setVisibility(View.GONE);
        }
        ll_back = (LinearLayout) findViewById(R.id.myself_layout__back);
        ll_enter = (LinearLayout) findViewById(R.id.myself_layout_enter);
        rl_nickname.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_years.setOnClickListener(this);
        rl_qq.setOnClickListener(this);
        rl_password.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_enter.setOnClickListener(this);
        tv_user.setText(user);
        pwTime = new TimePopupWindow(this, TimePopupWindow.Type.YEAR_MONTH_DAY);
        pwTime.setRange(1915, 2015);
        pwTime.setOnTimeSelectListener(new TimePopupWindow.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                tv_years.setText(getTime(date));
                users.setYears(getTime(date));
                bool = true;
            }
        });

        //选项选择器
        pwOptions = new OptionsPopupWindow(this);

        //选项1
        options1Items.add("男");
        options1Items.add("女");


        //pwOptions.setLabels("性别");
        //三级联动效果
        pwOptions.setPicker(options1Items);
        pwOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1);
                users.setSex(tx);
                tv_sex.setText(tx);
                bool = true;
            }
        });
    }


    private void getData() {
        RequestParams params = new RequestParams();
        params.put("user", user);
        params.put("action", "search_meeesage");

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
                            users.setNickname(object.getString("nickname"));
                            users.setSex(object.getString("sex"));
                            users.setYears(object.getString("years"));
                            users.setQq(object.getString("qq"));
                            users.setQianming(object.getString("qianming"));
                            tv_id.setText(object.getInt("id") + "");
                            tv_nickname.setText(users.getNickname());
                            tv_sex.setText(users.getSex());
                            tv_years.setText(users.getYears());
                            tv_qq.setText(users.getQq());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MyselfMessageActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    EditText contentView;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myself_layout_nickname:
                contentView = new EditText(this);
                contentView.setGravity(Gravity.CENTER);

                mMaterialDialog = new AlertDialog.Builder(this).setTitle("修改昵称").setView(contentView);
                mMaterialDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickname = contentView.getText().toString();
                        if (nickname.length() > 10 || nickname.length() < 2) {
                            Toast.makeText(MyselfMessageActivity.this, "名字的长度为3~10之间", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            users.setNickname(nickname);
                            tv_nickname.setText(nickname);
                            bool = true;
                            dialog.dismiss();
                        }
                    }
                });
                mMaterialDialog.setNegativeButton("取消", null);

                mMaterialDialog.show();
                break;
            case R.id.myself_layout_sex:
                pwOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.myself_layout_years:
                pwTime.showAtLocation(v, Gravity.BOTTOM, 0, 0, new Date());

                break;
            case R.id.myself_layout_qq:
                contentView = new EditText(this);
                contentView.setInputType(InputType.TYPE_CLASS_NUMBER);
                contentView.setGravity(Gravity.CENTER);

                mMaterialDialog = new AlertDialog.Builder(this).setTitle("修改QQ号码").setView(contentView);
                mMaterialDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String qq = contentView.getText().toString();
                        if (qq.length() < 5) {
                            Toast.makeText(MyselfMessageActivity.this, "你有这么短的QQ号码，我不信", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            users.setQq(qq);
                            tv_qq.setText(qq);
                            bool = true;
                            dialog.dismiss();
                        }
                    }
                });
                mMaterialDialog.setNegativeButton("取消", null);

                mMaterialDialog.show();
                break;
            case R.id.myself_layout_password:
                gotoChangePassword();
                break;
            case R.id.myself_layout__back:
                finish();
                break;
            case R.id.myself_layout_enter:
                if (bool) {


                    Snackbar.make(v, "确定修改资料吗？", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.title)).setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequestParams params = new RequestParams();
                            params.put("action", "update_message");
                            params.put("nickname", users.getNickname());
                            params.put("sex", users.getSex());
                            params.put("years", users.getYears());
                            params.put("qq", users.getQq());
                            params.put("user", users.getUser());
                            params.put("city", users.getCity());
                            params.put("qianming", users.getQianming());
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(url, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String str = new String(responseBody);

                                    if (str != null) {
                                        try {
                                            JSONObject jo = new JSONObject(str);
                                            if (jo.getString("code").equals("success")) {
                                                Intent intent = new Intent(MyselfMessageActivity.this, MainActivity.class);
                                                intent.putExtra("nickname", users.getNickname());
                                                intent.putExtra("sex", users.getSex());
                                                intent.putExtra("years", users.getYears());
                                                intent.putExtra("qq", users.getQq());
                                                setResult(199, intent);
                                                finish();

                                            }
                                            Toast.makeText(MyselfMessageActivity.this, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    //  Toast.makeText(MyselfMessageActivity.this, new String(responseBody), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast.makeText(MyselfMessageActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).show();

                } else {
                    Toast.makeText(MyselfMessageActivity.this, "资料未修改", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void gotoChangePassword() {
        Intent it = new Intent(this, ChangePasswordActivity.class);
        it.putExtra("password", users.getPassword());
        it.putExtra("user", user);
        startActivity(it);
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }
}
