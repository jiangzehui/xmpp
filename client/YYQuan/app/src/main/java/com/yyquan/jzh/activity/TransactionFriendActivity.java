package com.yyquan.jzh.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppMessage;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.util.ToastUtil;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.xmpp.XmppContentProvider;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import org.jivesoftware.smack.packet.Presence;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/14.
 */
public class TransactionFriendActivity extends BaseActivity implements View.OnClickListener {


    XmppMessage message;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    @Bind(R.id.layout_back)
    LinearLayout ll_Back;
    @Bind(R.id.imageview_icon)
    CircleImageView iv_Icon;
    @Bind(R.id.textView_name)
    TextView tv_Name;
    @Bind(R.id.textView_sex)
    TextView tv_Sex;
    @Bind(R.id.textview_shenqing)
    TextView tv_Shenqing;
    @Bind(R.id.tv_tongyi)
    TextView tvTongyi;
    @Bind(R.id.tv_jujue)
    TextView tvJujue;
    @Bind(R.id.ll_btn)
    LinearLayout llBtn;
    @Bind(R.id.tv_tishi)
    TextView tvTishi;
    Presence presence1;
    @Bind(R.id.rl_msg)
    RelativeLayout rlMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionfriend);
        ButterKnife.bind(this);
        initialView();
    }

    @Override
    public void initialView() {
        tvTongyi.setOnClickListener(this);
        ll_Back.setOnClickListener(this);
        tvJujue.setOnClickListener(this);
        rlMsg.setOnClickListener(this);
        Intent intent = getIntent();
        message = (XmppMessage) intent.getSerializableExtra("xmpp_user");
        Log.i("message", message.toString());
        //String str[] = message.getUser().getName().split(";");
        User users = new Gson().fromJson(message.getUser().getName(), User.class);
        tv_Name.setText(users.getNickname());
        tv_Shenqing.setText("申请时间：" + message.getTime());


        if (message.getType().equals("add")) {
            if (message.getResult() == 1) {
                llBtn.setVisibility(View.VISIBLE);
                tvTishi.setVisibility(View.GONE);

            } else {
                llBtn.setVisibility(View.GONE);
                if (message.getResult() == 0) {
                    tvTishi.setText("已同意" + "该申请");
                } else if (message.getResult() == -1) {
                    tvTishi.setText("已拒绝" + "该申请");
                }
                tvTishi.setVisibility(View.VISIBLE);

            }
        } else if (message.getType().equals("tongyi") || message.getType().equals("jujue")) {
            llBtn.setVisibility(View.GONE);
            tvTishi.setVisibility(View.VISIBLE);
            tvTishi.setText(message.getContent());
            if (message.getResult() == 1) {
                ContentValues values = new ContentValues();
                values.put("result", 0);
                XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, values, "id=?", new String[]{message.getId() + ""});
                SharedPreferencesUtil.setFriendMessageNumber_subone(this, message.getTo());
                Intent intents = new Intent("xmpp_receiver");
                intents.putExtra("type", message.getType());
                sendBroadcast(intents);


            }


        }


        Drawable nav_up = null;
        if (users.getSex().equals("男")) {
            tv_Sex.setText("男");
            nav_up = getResources().getDrawable(R.mipmap.man);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            tv_Sex.setCompoundDrawables(null, null, nav_up, null);
        } else {
            tv_Sex.setText("女");
            nav_up = getResources().getDrawable(R.mipmap.woman);
            nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
            tv_Sex.setCompoundDrawables(null, null, nav_up, null);
        }
        if (users.getIcon().equals("")) {
            if (users.getSex().equals("男")) {
                iv_Icon.setImageResource(R.mipmap.me_icon_man);
                tv_Sex.setText("男");
            } else {
                tv_Sex.setText("女");
                iv_Icon.setImageResource(R.mipmap.me_icon_woman);
            }
        } else {
            if (users.getIcon().substring(0, 4).equals("http")) {
                Picasso.with(this).load(users.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(iv_Icon);
            } else {
                Picasso.with(this).load(url_icon + users.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(iv_Icon);
            }
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.rl_msg:
                if (XmppTool.getInstance().isConnection() == false) {
                    ToastUtil.show(this, "已断开,正在重连中....");
                    return;
                }
                Intent in = new Intent(this, ShowMessageActivity.class);
                in.putExtra("user", message.getUser().getUserName());
                startActivity(in);

                break;
            case R.id.tv_tongyi:

                presence1 = new Presence(Presence.Type.subscribed);// 同意是subscribed
                // 拒绝是unsubscribe
                presence1.setTo(message.getUser().getUserName() + "@" + XmppTool.getInstance().getCon().getServiceName());// 接收方jid
                presence1.setFrom(message.getTo() + "@" + XmppTool.getInstance().getCon().getServiceName());// 发送方jid
                XmppTool.getInstance().getCon().sendPacket(presence1);// connection是你自己的XMPPConnection链接
                if (XmppTool.getInstance().addUser(
                        message.getUser().getUserName() + "@" + XmppTool.getInstance().getCon().getServiceName(),
                        message.getUser().getName(), "我的好友")) {
                    XmppTool.getInstance().addUserToGroup(
                            message.getUser().getUserName() + "@" + XmppTool.getInstance().getCon().getServiceName(),
                            "我的好友");
                    Log.i("transaction", "添加好友");

                }
                ContentValues values = new ContentValues();
                values.put("result", 0);
                XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, values, "id=?", new String[]{message.getId() + ""});
                SharedPreferencesUtil.setFriendMessageNumber_subone(this, message.getTo());
                llBtn.setVisibility(View.GONE);
                tvTishi.setVisibility(View.VISIBLE);
                tvTishi.setText("已同意该申请");
                Intent intent = new Intent("xmpp_receiver");
                intent.putExtra("type", "tongyi");
                sendBroadcast(intent);

                break;
            case R.id.tv_jujue:
                presence1 = new Presence(Presence.Type.unsubscribe);// 同意是subscribed
                // 拒绝是unsubscribe
                presence1.setTo(message.getUser().getUserName() + "@" + XmppTool.getInstance().getCon().getServiceName());// 接收方jid
                presence1.setFrom(message.getTo() + "@" + XmppTool.getInstance().getCon().getServiceName());// 发送方jid
                XmppTool.getInstance().getCon().sendPacket(presence1);// connection是你自己的XMPPConnection链接
                ContentValues valuess = new ContentValues();
                valuess.put("result", -1);
                XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, valuess, "id=?", new String[]{message.getId() + ""});
                SharedPreferencesUtil.setFriendMessageNumber_subone(this, message.getTo());
                llBtn.setVisibility(View.GONE);
                tvTishi.setVisibility(View.VISIBLE);
                tvTishi.setText("已拒绝该申请");
                Intent intents = new Intent("xmpp_receiver");
                intents.putExtra("type", "jujue");
                sendBroadcast(intents);
                break;
        }

    }
}
