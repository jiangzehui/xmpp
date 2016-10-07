package com.yyquan.jzh.xmpp;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.ChatActivity;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppChat;
import com.yyquan.jzh.entity.XmppFriend;
import com.yyquan.jzh.entity.XmppMessage;
import com.yyquan.jzh.entity.XmppUser;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.util.TimeUtil;

import java.util.HashMap;
import java.util.List;


/**
 * Created by jzh on 2016/1/8.
 */
public class XmppReceiver extends BroadcastReceiver {

    updateActivity ua = null;
    public NotificationManager manager = null;


    public XmppReceiver(updateActivity ua) {
        this.ua = ua;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        String type = intent.getStringExtra("type");
        if (type.equals("chat")) {


            XmppChat xc = (XmppChat) intent.getSerializableExtra("chat");
            if (ChatActivity.ca != null) {
                //在chat界面更新信息
                Log.i("xmpppppp", ChatActivity.xf.getUser().getUser() + "\t" + xc.getNickname());
                if (ChatActivity.xf.getUser().getUser().equals(xc.getUser())) {
                    ua.update(xc);
                }
                chatDatas(xc.getMain(), xc.getUser(), xc.getToo(), xc.getContent());

            } else {
                int num = chatData(xc.getMain(), xc.getUser(), xc.getToo(), xc.getContent());
                if (XmppService.vibrator != null && SharedPreferencesUtil.getBoolean(context, "tishi", "zhendong", true)) {
                    XmppService.vibrator.vibrate(500);
                }
                if (!isAppOnForeground(context)) {

                    //在message界面更新信息
                    if (manager == null) {
                        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    }
                    Intent intent1 = new Intent(context, ChatActivity.class);
                    User users = new User();
                    users.setUser(xc.getUser());
                    users.setNickname(xc.getNickname());
                    intent1.putExtra("xmpp_friend", new XmppFriend(users));
                    PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notify = new Notification.Builder(context)
                            .setAutoCancel(true)
                            .setTicker("有新消息")
                            .setSmallIcon(R.mipmap.ic_icon2)
                            .setContentTitle("来自" + xc.getNickname() + "的消息")
                            .setContentText(xc.getContent())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setWhen(System.currentTimeMillis())
                            .setNumber(num)
                            .setContentIntent(pi).build();
                    manager.notify(0, notify);
                } else {
                    if (XmppService.pool != null && SharedPreferencesUtil.getBoolean(context, "tishi", "music", true)) {
                        XmppService.pool.play(1, 1, 1, 0, 0, 1);
                    }
                }


            }


        }
        ua.update(type);


    }


    public interface updateActivity {
        public void update(String type);


        public void update(XmppChat xc);
    }

    public int chatData(final String main, final String users, final String to, final String content) {

        Cursor cursor = XmppService.resolver.query(XmppContentProvider.CONTENT_MESSAGES_URI, null,
                "main=? and type=?", new String[]{main, "chat"}, null);

        if (!cursor.moveToFirst()) {
            //插入
            List<XmppUser> list1 = XmppTool.getInstance().searchUsers(users);
            Log.i("XmppService_add", list1.get(0).getUserName() + "\n" + list1.get(0).getName());
            XmppMessage xm = new XmppMessage(to,
                    "chat",
                    new XmppUser(list1.get(0).getUserName(), list1.get(0).getName()),
                    TimeUtil.getDate(),
                    content,
                    1,
                    main
            );
            XmppContentProvider.add_message(xm);

            return 1;
        } else {
            //更新
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int result = cursor.getInt(cursor.getColumnIndex("result"));
            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("time", TimeUtil.getDate());
            values.put("result", (result + 1));
            XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, values, "id=?", new String[]{id + ""});
            return (result + 1);
        }

    }


    public void chatDatas(final String main, final String users, final String to, final String content) {

        Cursor cursor = XmppService.resolver.query(XmppContentProvider.CONTENT_MESSAGES_URI, null,
                "main=? and type=?", new String[]{main, "chat"}, null);

        if (!cursor.moveToFirst()) {
            //插入
            List<XmppUser> list1 = XmppTool.getInstance().searchUsers(users);
            Log.i("XmppService_add", list1.get(0).getUserName() + "\n" + list1.get(0).getName());
            XmppMessage xm = new XmppMessage(to,
                    "chat",
                    new XmppUser(list1.get(0).getUserName(), list1.get(0).getName()),
                    TimeUtil.getDate(),
                    content,
                    0,
                    main
            );
            XmppContentProvider.add_message(xm);
        } else {
            //更新
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            ContentValues values = new ContentValues();
            values.put("content", content);
            values.put("time", TimeUtil.getDate());
            values.put("result", 0);
            XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, values, "id=?", new String[]{id + ""});

        }

    }

    public boolean isAppOnForeground(Context context) {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


}
