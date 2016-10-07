package com.yyquan.jzh.activity;

import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.LocationManagerProxy;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.yyquan.jzh.R;
import com.yyquan.jzh.adapter.ContentFragmentPageAadpter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.fragment.news.contentFragment;
import com.yyquan.jzh.fragment.news.pinglunFragment;
import com.yyquan.jzh.location.Location;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class NewsContentActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private EditText et_pinglun;
    private TextView tv_uppinglun;
    public TextView tv_pinglun;


    private LinearLayout back;
    private contentFragment cf;
    private pinglunFragment pf;
    //定义一个ViewPager容器
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ContentFragmentPageAadpter mAdapter;
    //定义FragmentManager对象
    public FragmentManager fManager;
    Intent intent;
    public User user;
    public News_content content;
    String url = Ip.ip + "/YfriendService/DoGetPingLun";
    InputMethodManager imm;//键盘管理器

    Location lt;
    public int pl_size;
    int page_select;
    public String content_url = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        content = (News_content) intent.getSerializableExtra("news_content");
        content_url = intent.getStringExtra("url");
        //  Toast.makeText(this,user.getNickname(),Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_news_content);
        initialView();
        lt = new Location(this);

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
        initViewPager();
        DialogView.Initial(this, "正在评论......");


        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        mPager = (ViewPager) findViewById(R.id.news_content_vPager);
        mPager.setOnPageChangeListener(this);

        et_pinglun = (EditText) findViewById(R.id.news_content_editText_pinglun);
        tv_pinglun = (TextView) findViewById(R.id.news_content_text_showpinglun);
        tv_uppinglun = (TextView) findViewById(R.id.news_content_text_enterpinglun);
        tv_pinglun.setText(content.getCpinglun() + "评");

        back = (LinearLayout) findViewById(R.id.news_content_back);
        back.setOnClickListener(this);

        tv_pinglun.setOnClickListener(this);
        tv_uppinglun.setOnClickListener(this);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);
    }


    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        fManager = getSupportFragmentManager();

        if (cf == null) {
            cf = new contentFragment();
        }
        if (pf == null) {
            pf = new pinglunFragment();
        }

        fragmentsList = new ArrayList<Fragment>();
        fragmentsList.add(cf);
        fragmentsList.add(pf);

        mAdapter = new ContentFragmentPageAadpter(fManager, fragmentsList);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news_content_back:
                finish();

                break;

            case R.id.news_content_text_enterpinglun:
                upPingLun();

                break;
            case R.id.news_content_text_showpinglun:

                if (page_select == 0) {

                    mPager.setCurrentItem(1);
                } else {

                    mPager.setCurrentItem(0);
                }


                break;
        }
    }

    /**
     * 提交评论
     */
    private void upPingLun() {


        String pinglun = et_pinglun.getText().toString();
        if (pinglun.equals("")) {
            Toast.makeText(NewsContentActivity.this, "请先评论", Toast.LENGTH_SHORT).show();

            return;
        }
        DialogView.show();
        tv_uppinglun.setEnabled(false);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String ptime = sdf.format(new Date());
        Date date = new Date();
        long ptime = date.getTime();
        RequestParams params = new RequestParams();
        params.put("pcid", content.getCid());
        params.put("action", "save");
        params.put("user", user.getUser());
        params.put("plocation", lt.city);
        params.put("ptime", ptime);
        params.put("pcontent", pinglun);


        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                tv_uppinglun.setEnabled(true);
                DialogView.dismiss();
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {

                            Toast.makeText(NewsContentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            et_pinglun.setText("");
                            if (imm.isActive()) {//关闭键盘
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            pf.refreshData();

                            //更新评论内容

                        } else {
                            Toast.makeText(NewsContentActivity.this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        tv_uppinglun.setEnabled(true);
                        DialogView.dismiss();
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tv_uppinglun.setEnabled(true);
                DialogView.dismiss();
                Toast.makeText(NewsContentActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        page_select = position;
        if (page_select == 0) {
            tv_pinglun.setText(pl_size + "评");

        } else {
            tv_pinglun.setText("原文");

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {
            int i = mPager.getCurrentItem();
            mPager.setCurrentItem(i);
        }

    }


}
