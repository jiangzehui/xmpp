package com.yyquan.jzh.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.os.Process;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yyquan.jzh.R;
import com.yyquan.jzh.util.DataCleanUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.view.LockView.LockSetupActivity;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jzh on 2015/11/2.
 */
public class SystemOptionActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    RelativeLayout rl_clear;
    RelativeLayout rl_update;

    LinearLayout ll_back;
    CheckBox iv_password;
    TextView tv_update;
    TextView tv_zhuxiao;
    TextView tv_versioncode;
    TextView tv_memory;
    boolean bool;
    @Bind(R.id.systemoption_cb_media)
    CheckBox CbMedia;
    @Bind(R.id.systemoption_cb_zhendong)
    CheckBox CbZhendong;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);
        tv_zhuxiao = (TextView) findViewById(R.id.systemoption_tv_zhuxiao);
        tv_zhuxiao.setOnClickListener(this);
        initialView();
    }


    /**
     * 初始化控件
     */
    private void initialView() {
        //setBoolean(this,"tishi","music",isChecked);
        boolean bo_music = SharedPreferencesUtil.getBoolean(this, "tishi", "music", true);
        boolean bo_zhendong = SharedPreferencesUtil.getBoolean(this, "tishi", "zhendong", true);
        CbMedia.setChecked(bo_music);
        CbZhendong.setChecked(bo_zhendong);
        rl_clear = (RelativeLayout) findViewById(R.id.systemoption_layout_clear);
        rl_update = (RelativeLayout) findViewById(R.id.systemoption_layout_update);
        iv_password = (CheckBox) findViewById(R.id.systemoption_imageview_shoushi);
        tv_update = (TextView) findViewById(R.id.systemoption_textview_update);
        tv_zhuxiao = (TextView) findViewById(R.id.systemoption_tv_zhuxiao);
        ll_back = (LinearLayout) findViewById(R.id.systemoption_layout_back);
        tv_versioncode = (TextView) findViewById(R.id.systemoption_textview_versioncode);
        tv_memory = (TextView) findViewById(R.id.systemoption_text_memory);
        CbMedia.setOnCheckedChangeListener(this);
        CbZhendong.setOnCheckedChangeListener(this);
        ll_back.setOnClickListener(this);
        tv_zhuxiao.setOnClickListener(this);
        rl_clear.setOnClickListener(this);
        rl_update.setOnClickListener(this);
        iv_password.setOnClickListener(this);
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            tv_versioncode.setText("V" + packageInfo.versionName + "");
            tv_memory.setText(DataCleanUtil.getTotalCacheSize(this));
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.systemoption_tv_zhuxiao:
                if (MainActivity.main != null) {
                    MainActivity.main.finish();
                }
                stopService(new Intent(SystemOptionActivity.this, XmppService.class));
                SharedPreferences preferences = getSharedPreferences("user_message", LoginActivity.MODE_PRIVATE);
                preferences.edit().clear().commit();
                XmppTool.disConnectServer();
                startActivity(new Intent(this, LoginActivity.class));
                android.os.Process.killProcess(Process.myPid());
                break;
            case R.id.systemoption_layout_back:
                finish();
                break;

            case R.id.systemoption_imageview_shoushi:


                Intent intent = new Intent(SystemOptionActivity.this, LockSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.systemoption_layout_clear:
                new AlertDialog.Builder(SystemOptionActivity.this).setTitle("清除缓存")
                        .setMessage("清除缓存后使用的流量可能会额外增加，确定清除？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clear();
                            }


                        }).setNegativeButton("取消", null).show();


                break;
            case R.id.systemoption_layout_update:

                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("user_message", SystemOptionActivity.MODE_PRIVATE);
        bool = preferences.getBoolean("lock", false);
        if (bool) {
            iv_password.setChecked(true);

        } else {
            iv_password.setChecked(false);

        }
        try {
            tv_memory.setText(DataCleanUtil.getTotalCacheSize(this));
        } catch (Exception e1) {

            e1.printStackTrace();
        }
    }


    /**
     * 清理缓存
     */
    private void clear() {
        DataCleanUtil.clearAllCache(SystemOptionActivity.this);
        try {
            tv_memory.setText(DataCleanUtil.getTotalCacheSize(this));
            Toast.makeText(SystemOptionActivity.this, "清除缓存成功...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.systemoption_cb_media:
                SharedPreferencesUtil.setBoolean(this, "tishi", "music", isChecked);
                break;
            case R.id.systemoption_cb_zhendong:
                SharedPreferencesUtil.setBoolean(this, "tishi", "zhendong", isChecked);
                break;
        }
    }
}
