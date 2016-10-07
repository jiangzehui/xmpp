package com.yyquan.jzh.activity;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppChat;
import com.yyquan.jzh.fragment.friend.FriendFragment;
import com.yyquan.jzh.fragment.friend.MessageFragment;
import com.yyquan.jzh.fragment.luntan.LuntanFragment;
import com.yyquan.jzh.fragment.news.NewsFragment;
import com.yyquan.jzh.util.Base64Coder;
import com.yyquan.jzh.util.ImageCompressUtils;
import com.yyquan.jzh.util.PhotoSelectedHelper;
import com.yyquan.jzh.util.SetImageUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.view.BadgeView;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.xmpp.XmppReceiver;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends FragmentActivity implements View.OnClickListener, PopupWindow.OnDismissListener, AMapLocalWeatherListener {

    public static MainActivity main;
    @Bind(R.id.main_iv_status)
    ImageView mainIvStatus;
    ImageView iv_me_status;

    private TextView tv_news;
    private TextView tv_luntan;
    private TextView tv_friend;
    private TextView tv_message;
    private LinearLayout ll_to;
    private CircleImageView iv_me;
    private CircleImageView iv_mes;
    private TextView tv_me_name;

    private NewsFragment news_fragment;
    private LuntanFragment luntan_fragment;
    private FriendFragment friend_fragment;
    public MessageFragment message_fragment;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Intent intent;
    public User user;
    SweetAlertDialog pDialog;


    //popwindow
    RelativeLayout rl_pop_null;
    TextView tv_pop_quxiao;
    TextView tv_pop_status;
    TextView tv_pop_chakan;
    TextView tv_pop_change;
    RelativeLayout rl_pop_nulls;
    TextView tv_pop_photo;
    TextView tv_pop_camera;
    TextView tv_pop_quxiaos;
    ImageView iv_tianqi;
    ImageView iviv;
    ImageView iv_addfriend;
    TextView tv_tianqi;
    TextView tv_date;

    RelativeLayout rl_pop_nullss;
    TextView tv_pop_online;
    TextView tv_pop_qme;
    TextView tv_pop_busy;
    TextView tv_pop_wurao;
    TextView tv_pop_leave;
    TextView tv_pop_yinshen;
    TextView tv_pop_quxiaoss;
    PopupWindow pop;
    PopupWindow pops;
    PopupWindow popss;
    private static int code = 1;
    private static int codes = 2;
    private static int codess = 3;
    String path;
    PhotoSelectedHelper mPhotoSelectedHelper;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    private LocationManagerProxy mLocationManagerProxy;

    //xmpp
    public XmppReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        main = this;
        mPhotoSelectedHelper = new PhotoSelectedHelper(MainActivity.this);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        initialView();
        initialPopup();
        initialDialog();

    }


    /**
     * 初始化状态框
     */
    private void initialDialog() {


        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("正在更换头像......");
        pDialog.setCancelable(false);

    }

    /**
     * 初始化控件
     */
    private void initialView() {

        receiver = new XmppReceiver(ua);
        registerReceiver(receiver, new IntentFilter("xmpp_receiver"));
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestWeatherUpdates(
                LocationManagerProxy.WEATHER_TYPE_LIVE, this);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        iv_me_status = (ImageView) mNavigationView.findViewById(R.id.me_status);
        XmppTool.getInstance().setPresence(mainIvStatus, iv_me_status, this, user.getUser());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
        tv_news = (TextView) findViewById(R.id.tv_news);
        tv_luntan = (TextView) findViewById(R.id.tv_luntan);
        tv_friend = (TextView) findViewById(R.id.tv_friend);
        tv_message = (TextView) findViewById(R.id.tv_message);
        ll_to = (LinearLayout) findViewById(R.id.main_layout_to);
        iv_me = (CircleImageView) findViewById(R.id.main_CircleImageView);
        iv_mes = (CircleImageView) mNavigationView.findViewById(R.id.me_icon);
        iviv = (ImageView) mNavigationView.findViewById(R.id.iviv);
        tv_me_name = (TextView) mNavigationView.findViewById(R.id.me_name);
        iv_tianqi = (ImageView) mNavigationView.findViewById(R.id.main_imageview_tianqi);
        iv_addfriend = (ImageView) findViewById(R.id.main_imageView_addfriend);
        tv_tianqi = (TextView) mNavigationView.findViewById(R.id.main_textview_tianqi);
        tv_date = (TextView) mNavigationView.findViewById(R.id.main_textview_date);

        tv_me_name.setText(user.getNickname());
        if (user.getIcon().equals("")) {//加载默认头像
            if (user.getSex().equals("女")) {
                iv_me.setImageResource(R.mipmap.me_icon_woman);
                iv_mes.setImageResource(R.mipmap.me_icon_woman);
            } else if (user.getSex().equals("男")) {
                iv_me.setImageResource(R.mipmap.me_icon_man);
                iv_mes.setImageResource(R.mipmap.me_icon_man);
            }
        } else { //加载网络头像
            if (user.getIcon().substring(0, 4).equals("http")) {
                Picasso.with(MainActivity.this).load(user.getIcon()).resize(200, 200).centerInside().into(iv_me);
                Picasso.with(MainActivity.this).load(user.getIcon()).resize(200, 200).centerInside().into(iv_mes);
            } else {
                Picasso.with(MainActivity.this).load(url_icon + user.getIcon()).resize(200, 200).centerInside().into(iv_me);
                Picasso.with(MainActivity.this).load(url_icon + user.getIcon()).resize(200, 200).centerInside().into(iv_mes);
            }

        }
        iv_mes.setOnClickListener(this);
        iv_addfriend.setOnClickListener(this);

        tv_news.setOnClickListener(this);
        tv_luntan.setOnClickListener(this);
        tv_friend.setOnClickListener(this);
        tv_message.setOnClickListener(this);
        ll_to.setOnClickListener(this);

        selection(0);
    }

    /**
     * 初始化popupwindow
     */
    private void initialPopup() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.third_image_popupwindow, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pop.setOnDismissListener(this);
        rl_pop_null = (RelativeLayout) view
                .findViewById(R.id.third_popupwindow_layout_null);
        tv_pop_status = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_status);
        tv_pop_quxiao = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_quxiao);
        tv_pop_chakan = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_look);
        tv_pop_change = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_change);
        rl_pop_null.setOnClickListener(this);
        tv_pop_quxiao.setOnClickListener(this);
        tv_pop_status.setOnClickListener(this);
        tv_pop_chakan.setOnClickListener(this);
        tv_pop_change.setOnClickListener(this);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        initialPopups();

    }

    /**
     * 初始化popupwindow
     */
    private void initialPopups() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.third_image_popupwindows, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        pops = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pops.setOnDismissListener(this);
        rl_pop_nulls = (RelativeLayout) view
                .findViewById(R.id.third_popupwindow_layout_nulls);
        tv_pop_quxiaos = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_quxiaoo);
        tv_pop_photo = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_photo);
        tv_pop_camera = (TextView) view
                .findViewById(R.id.third_popupwindow_textView_camera);
        rl_pop_nulls.setOnClickListener(this);
        tv_pop_quxiaos.setOnClickListener(this);
        tv_pop_photo.setOnClickListener(this);
        tv_pop_camera.setOnClickListener(this);
        // 需要设置一下此参数，点击外边可消失
        pops.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pops.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pops.setFocusable(true);
        initialPopupss();
    }

    /**
     * 初始化popupwindow
     */
    private void initialPopupss() {

        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.third_image_popupwindowss, null);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow对象
        popss = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popss.setOnDismissListener(this);
        rl_pop_nullss = (RelativeLayout) view.findViewById(R.id.third_popupwindow_layout_nullss);
        tv_pop_quxiaoss = (TextView) view.findViewById(R.id.third_popupwindow_textView_quxiaooo);
        tv_pop_online = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_online);
        tv_pop_qme = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_qme);
        tv_pop_busy = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_busy);
        tv_pop_wurao = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_wurao);
        tv_pop_leave = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_leave);
        tv_pop_yinshen = (TextView) view.findViewById(R.id.third_popupwindow_textView_status_yinshen);

        rl_pop_nullss.setOnClickListener(this);
        tv_pop_quxiaoss.setOnClickListener(this);
        tv_pop_online.setOnClickListener(this);
        tv_pop_qme.setOnClickListener(this);
        tv_pop_busy.setOnClickListener(this);
        tv_pop_wurao.setOnClickListener(this);
        tv_pop_leave.setOnClickListener(this);
        tv_pop_yinshen.setOnClickListener(this);
        // 需要设置一下此参数，点击外边可消失
        pops.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pops.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pops.setFocusable(true);
    }

    /**
     * 显示popupwindow
     */
    private void showPopupWindow(int c) {
        if (c == code) {
            if (pop.isShowing()) {
                // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                pop.dismiss();
            } else {
                // 显示窗口
                // pop.showAsDropDown(v);
                // 获取屏幕和PopupWindow的width和height
                pop.setAnimationStyle(R.style.MenuAnimationFade);
                pop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                pop.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                pop.showAsDropDown(iviv, 0, 0);

                WindowManager.LayoutParams lp = getWindow()
                        .getAttributes();
                lp.alpha = 0.7f;
                getWindow().setAttributes(lp);

            }
        } else if (c == codes) {
            if (pop != null) {
                pop.dismiss();
                if (pops.isShowing()) {
                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                    pops.dismiss();
                } else {
                    // 显示窗口
                    // pop.showAsDropDown(v);
                    // 获取屏幕和PopupWindow的width和height
                    pops.setAnimationStyle(R.style.MenuAnimationFade);
                    pops.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    pops.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                    pops.showAsDropDown(iviv, 0, 0);

                    WindowManager.LayoutParams lp = getWindow()
                            .getAttributes();
                    lp.alpha = 0.7f;
                    getWindow().setAttributes(lp);

                }

            }
        } else if (c == codess) {
            if (pop != null) {
                pop.dismiss();
                if (popss.isShowing()) {
                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                    popss.dismiss();
                } else {
                    // 显示窗口
                    // pop.showAsDropDown(v);
                    // 获取屏幕和PopupWindow的width和height
                    popss.setAnimationStyle(R.style.MenuAnimationFade);
                    popss.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    popss.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
                    popss.showAsDropDown(iviv, 0, 0);

                    WindowManager.LayoutParams lp = getWindow()
                            .getAttributes();
                    lp.alpha = 0.7f;
                    getWindow().setAttributes(lp);

                }

            }
        }

    }


    /**
     * 初始化图片,文字
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initialImage() {
        tv_news.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.tab_comprehensive_icon, 0, 0);
        tv_news.setTextColor(getResources().getColor(R.color.tab_text_bg));
        tv_luntan.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_found_icon, 0, 0);
        tv_luntan.setTextColor(getResources().getColor(R.color.tab_text_bg));
        tv_friend.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.tab_me_icon, 0, 0);
        tv_friend.setTextColor(getResources().getColor(R.color.tab_text_bg));
        tv_message.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.tab_move_icon, 0, 0);
        tv_message.setTextColor(getResources().getColor(R.color.tab_text_bg));


    }


    XmppReceiver.updateActivity ua = new XmppReceiver.updateActivity() {
        @Override
        public void update(String type) {

            switch (type) {


                case "status":
                    if (friend_fragment != null) {
                        friend_fragment.getData();
                    }
                    break;

                case "tongyi":
                    if (message_fragment != null) {
                        message_fragment.initialData();
                    }
                    if (friend_fragment != null) {
                        friend_fragment.getData();
                    }
                    break;
                case "add":
                case "jujue":
                case "chat":
                    if (message_fragment != null) {
                        message_fragment.initialData();
                    }
                    break;
            }
        }


        @Override
        public void update(XmppChat xc) {

        }
    };


    /**
     * 点击不同的按钮做出不同的处理
     */
    private void selection(int index) {
        initialImage();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                ft.hide(f);
            }
        }

        Fragment fragment;
        switch (index) {

            case 0:
                iv_addfriend.setVisibility(View.GONE);
                tv_message.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_move_pressed_icon, 0, 0);
                tv_message.setTextColor(tv_message.getResources().getColor(R.color.title));
                fragment = getSupportFragmentManager().findFragmentByTag("message_fragment");
                if (fragment == null) {
                    message_fragment = new MessageFragment();
                    ft.add(R.id.fg_content, message_fragment, "message_fragment");
                } else {
                    ft.show(fragment);
                }
                break;
            case 1:
                iv_addfriend.setVisibility(View.VISIBLE);
                tv_friend.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_me_pressed_icon, 0, 0);
                tv_friend.setTextColor(tv_friend.getResources().getColor(R.color.title));
                fragment = getSupportFragmentManager().findFragmentByTag("friend_fragment");
                if (fragment == null) {
                    friend_fragment = new FriendFragment();
                    ft.add(R.id.fg_content, friend_fragment, "friend_fragment");
                } else {
                    ft.show(fragment);
                }
                break;

            case 2:
                iv_addfriend.setVisibility(View.GONE);
                tv_news.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_comprehensive_pressed_icon, 0, 0);
                tv_news.setTextColor(tv_news.getResources().getColor(R.color.title));
                fragment = getSupportFragmentManager().findFragmentByTag("news_fragment");
                if (fragment == null) {
                    news_fragment = new NewsFragment();
                    ft.add(R.id.fg_content, news_fragment, "news_fragment");
                } else {
                    ft.show(fragment);
                }
                break;
            case 3:
                iv_addfriend.setVisibility(View.GONE);
                tv_luntan.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.tab_found_pressed_icon, 0, 0);
                tv_luntan.setTextColor(tv_luntan.getResources().getColor(R.color.title));
                fragment = getSupportFragmentManager().findFragmentByTag("luntan_fragment");
                if (fragment == null) {
                    luntan_fragment = new LuntanFragment();
                    ft.add(R.id.fg_content, luntan_fragment, "luntan_fragment");
                } else {
                    ft.show(fragment);
                }
                break;
        }
        ft.commit();
    }

//    private void hideFragments(FragmentTransaction ft) {
//        if (news_fragment != null) {
//            ft.hide(news_fragment);
//        }
//    }

    int i = 0;

    /**
     * 监听按钮
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_news:
                selection(2);
                break;
            case R.id.tv_luntan:
                selection(3);
                break;
            case R.id.tv_message:
                selection(0);
                break;
            case R.id.tv_friend:
                selection(1);
                break;
            case R.id.main_imageView_addfriend:
                startActivity(new Intent(MainActivity.this, AddFriendActivity.class));
                break;

            case R.id.main_layout_to:
                mDrawerLayout.openDrawer(Gravity.LEFT);//开启抽屉

                break;
            case R.id.me_icon:

                showPopupWindow(code);
                // mDrawerLayout.closeDrawers();//关闭抽屉
                break;
            case R.id.third_popupwindow_layout_null:
                if (pop != null) {
                    pop.dismiss();

                }

                break;
            case R.id.third_popupwindow_layout_nulls:
                if (pops != null) {
                    pops.dismiss();

                }

                break;
            case R.id.third_popupwindow_layout_nullss:
                if (popss != null) {
                    popss.dismiss();

                }

                break;
            case R.id.third_popupwindow_textView_quxiao:
                if (pop != null) {
                    pop.dismiss();

                }

                break;
            case R.id.third_popupwindow_textView_quxiaoo:
                if (pops != null) {
                    pops.dismiss();

                }

                break;
            case R.id.third_popupwindow_textView_quxiaooo:
                if (popss != null) {
                    popss.dismiss();

                }

                break;
            case R.id.third_popupwindow_textView_look:
                if (pop != null) {
                    pop.dismiss();

                }
                intent = new Intent(this, ShowImageActivity.class);
                if (user.getIcon().equals("")) {
                    path = user.getSex();
                } else {
                    path = user.getIcon();
                }
                intent.putExtra("path", path);
                intent.putExtra("type", "icon");
                startActivity(intent);

                break;
            case R.id.third_popupwindow_textView_change:

                showPopupWindow(codes);


                break;
            case R.id.third_popupwindow_textView_status://设置状态

                showPopupWindow(codess);


                break;
            case R.id.third_popupwindow_textView_photo:
                if (pops != null) {
                    pops.dismiss();

                }
                if (user != null) {
                    mPhotoSelectedHelper.imageSelection(user.getUser(), "pic");
                }

                break;

            case R.id.third_popupwindow_textView_camera:
                if (pops != null) {
                    pops.dismiss();

                }
                if (user != null) {
                    mPhotoSelectedHelper.imageSelection(user.getUser(), "take");
                }

                break;
            case R.id.third_popupwindow_textView_status_online:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(0);
                break;
            case R.id.third_popupwindow_textView_status_qme:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(1);
                break;
            case R.id.third_popupwindow_textView_status_busy:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(2);
                break;
            case R.id.third_popupwindow_textView_status_wurao:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(3);
                break;
            case R.id.third_popupwindow_textView_status_leave:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(4);
                break;
            case R.id.third_popupwindow_textView_status_yinshen:
                if (popss != null) {
                    popss.dismiss();

                }
                setPresence(5);
                break;
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {


        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {


                        switch (menuItem.getItemId()) {
                            case R.id.item_one:
                                intent = new Intent(MainActivity.this, MyselfMessageActivity.class);

                                startActivityForResult(intent, 199);
                                // Toast.makeText(MainActivity.this, "个人资料", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.item_two:
                                intent = new Intent(MainActivity.this, SystemOptionActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.item_3:
                                intent = new Intent(MainActivity.this, AboutMeActivity.class);
                                startActivity(intent);
                                break;


                        }
                        menuItem.setChecked(false);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == mPhotoSelectedHelper.TAKE_PHOTO) {
            if (!(resultCode == RESULT_OK)) {
                return;
            }

            if (data != null) {
                mPhotoSelectedHelper.cropImageUri(data.getData(), 200, 200, user.getUser());
            } else {
                mPhotoSelectedHelper.cropImageUri(mPhotoSelectedHelper.getCaptureUri(), 200, 200, user.getUser());
            }


        } else if (requestCode == mPhotoSelectedHelper.PHOTO_CROP) {
            if (!(resultCode == RESULT_OK)) {
                return;
            }
            final String cropPath = mPhotoSelectedHelper.getCropPath();
            if (cropPath != null) {
                pDialog.show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        upload(cropPath, "tack");
                    }
                }.start();


            }

        } else if (requestCode == mPhotoSelectedHelper.PIC_PHOTO) {
            if (data == null) {
                return;
            } else {
                Uri uri = data.getData();
                if (uri != null) {
                    path = SetImageUtil.getPath(this, uri);
                    if (path != null) {
                        pDialog.show();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                upload(path, "pic");
                            }
                        }.start();


                    }

                }
            }
        } else if (requestCode == 199) {
            if (data == null) {
                return;
            }
            String nickname = data.getStringExtra("nickname");
            String sex = data.getStringExtra("sex");
            String qq = data.getStringExtra("qq");
            String years = data.getStringExtra("years");
            user.setNickname(nickname);
            user.setSex(sex);
            user.setQq(qq);
            user.setYears(years);
            tv_me_name.setText(nickname);
        } else if (resultCode == 200) {


            if (luntan_fragment != null) {

                luntan_fragment.update();
            }

        }

    }


    long newTime;

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            if (System.currentTimeMillis() - newTime > 2000) {
                newTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            } else {
                stopService(new Intent(MainActivity.this, XmppService.class));
                unregisterReceiver(receiver);
                XmppTool.disConnectServer();
                android.os.Process.killProcess(Process.myPid());
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }

    // 上传
    public void upload(String paths, String type) {

        String filename = paths.substring(paths.lastIndexOf("/") + 1);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ImageCompressUtils.getimage(paths).compress(Bitmap.CompressFormat.JPEG,
                50, stream);
        byte[] b = stream.toByteArray();
        // 将图片流以字符串形式存储下来
        String file = new String(Base64Coder.encodeLines(b));
        HttpClient client = new DefaultHttpClient();
        // 设置上传参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("file", file));
        if (type.equals("pic")) {
            filename = user.getUser() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        }

        formparams.add(new BasicNameValuePair("filename", filename));
        formparams.add(new BasicNameValuePair("user", user.getUser()));
        formparams.add(new BasicNameValuePair("action", "update_icon"));

        HttpPost post = new HttpPost(Ip.ip + "/YfriendService/DoGetUser");
        UrlEncodedFormEntity entity;
        try {
            entity = new UrlEncodedFormEntity(formparams, "UTF-8");
            post.addHeader("Accept",
                    "text/javascript, text/html, application/xml, text/xml");
            post.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
            post.addHeader("Accept-Encoding", "gzip,deflate,sdch");
            post.addHeader("Connection", "Keep-Alive");
            post.addHeader("Cache-Control", "no-cache");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            System.out.println(response.getStatusLine().getStatusCode());
            HttpEntity e = response.getEntity();
            System.out.println(EntityUtils.toString(e));
            if (200 == response.getStatusLine().getStatusCode()) {
                path = paths;
                user.setIcon(filename);
                System.out.println("上传完成");
                Message m = new Message();
                m.what = 0;
                m.obj = path;

                h.sendMessage(m);
            } else {
                System.out.println("上传失败");
                Message m = new Message();
                m.what = 1;

                h.sendMessage(m);
            }
            client.getConnectionManager().shutdown();
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();
        }
    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                pDialog.dismiss();
                if (luntan_fragment != null) {
                    luntan_fragment.updateData(user.getIcon());
                }
                if (luntan_fragment != null) {
                    luntan_fragment.update();
                }

                String filename = (String) msg.obj;
                Toast.makeText(MainActivity.this, "头像修改成功", Toast.LENGTH_SHORT).show();
                Picasso.with(MainActivity.this).load(new File(filename)).resize(200, 200).centerCrop().into(iv_mes);
                Picasso.with(MainActivity.this).load(new File(filename)).resize(200, 200).centerCrop().into(iv_me);
            } else if (msg.what == 1) {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            String city = aMapLocalWeatherLive.getCity();//城市
            String weather = aMapLocalWeatherLive.getWeather();//天气情况
            String windDir = aMapLocalWeatherLive.getWindDir();//风向
            String windPower = aMapLocalWeatherLive.getWindPower();//风力
            String humidity = aMapLocalWeatherLive.getHumidity();//空气湿度
            String reportTime = aMapLocalWeatherLive.getReportTime();//数据发布时间
            String wendu = aMapLocalWeatherLive.getTemperature();//温度
            tv_tianqi.setText(city + ":\t" + wendu + "℃");
            tv_date.setText(weather + "\n" + reportTime);

            if (weather.contains("雨")) {
                iv_tianqi.setImageResource(R.mipmap.yu_60);
            } else if (weather.equals("阴")) {
                iv_tianqi.setImageResource(R.mipmap.yun_26);
            } else if (weather.contains("云")) {
                iv_tianqi.setImageResource(R.mipmap.c_28);
            } else if (weather.contains("雪")) {
                iv_tianqi.setImageResource(R.mipmap.c_14);
            } else if (weather.equals("晴")) {
                iv_tianqi.setImageResource(R.mipmap.c_32);
            } else if (weather.contains("沙")) {
                iv_tianqi.setImageResource(R.mipmap.c_62);
            } else if (weather.contains("霾") || weather.contains("雾")) {
                iv_tianqi.setImageResource(R.mipmap.c_63);
            }
            //  tv_me_name.setText(wendu+"\n"+city+"\n"+weather+"\n"+windDir+"\n"+windPower+"\n"+humidity+"\n"+reportTime);

        } else {
            // 获取天气预报失败

        }
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {
        Toast.makeText(this, "获取天气预报失败:" + aMapLocalWeatherForecast.getAMapException().getErrorMessage(), Toast.LENGTH_SHORT).show();
    }


    private void setPresence(int status) {
        XmppTool.getInstance().setPresence(status);
        SharedPreferencesUtil.setInt(MainActivity.this, "status", user.getUser() + "status", status);
        XmppTool.getInstance().setPresence(mainIvStatus, iv_me_status, this, user.getUser());

    }


}
