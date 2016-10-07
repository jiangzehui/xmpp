package com.yyquan.jzh.xmpp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yyquan.jzh.entity.XmppChat;

/**
 * Created by Administrator on 2016/1/10.
 */
public class XmppFriendMessageProvider extends ContentProvider {

    static final String PROVIDER_NAME =
            "com.yyquan.jzh.xmpp.chat";
    //用来区分操作的是单条数据还是多条数据
    private static final int CHAT = 1;
    private static final int CHATS = 2;
    private static UriMatcher matcher;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    public static final Uri CONTENT_CHAT_URI = Uri.parse("content://" + PROVIDER_NAME + "/chat");
    public static final Uri CONTENT_CHATS_URI = Uri.parse("content://" + PROVIDER_NAME + "/chats");


    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //为UriMatcher注册一个uri
        matcher.addURI(PROVIDER_NAME, "chat", CHAT);
        matcher.addURI(PROVIDER_NAME, "chats", CHATS);
    }

    /**
     * 插入一条消息
     *
     * @param xc
     */
    public static void add_message(XmppChat xc) {
        ContentValues values = new ContentValues();
        values.put("main", xc.getMain());
        values.put("user", xc.getUser());
        values.put("nickname", xc.getNickname());
        values.put("icon", xc.getIcon());
        values.put("type", xc.getType());
        values.put("content", xc.getContent());
        values.put("sex", xc.getSex());
        values.put("too", xc.getToo());
        values.put("sex", xc.getSex());
        values.put("viewtype", xc.getViewType());
        values.put("time", xc.getTime());
        XmppService.resolver.insert(XmppFriendMessageProvider.CONTENT_CHATS_URI, values);

    }


    @Override
    public boolean onCreate() {
        dbHelper = new MyDataBaseHelper(this.getContext());
        db = dbHelper.getWritableDatabase();
        return db == null ? false : true;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case CHAT:
                return "vnd.android.cursor.item/com.yyquan.jzh";
            case CHATS:
                return "vnd.android.cursor.dir/com.yyquan.jzh";
            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (matcher.match(uri)) {
            case CHAT:
                Log.i("provider", "数据查询成功");
                return db.query("chat", projection, selection, selectionArgs, null, null, sortOrder);
            case CHATS:
                Log.i("provider", "数据查询成功");
                return db.query("chat", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }


    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)) {

            case CHATS:
                long rowId = db.insert("chat", null, values);
                if (rowId > 0) {
                    Uri wordUri = ContentUris.withAppendedId(uri, rowId);
                    Log.i("provider", "数据插入成功");
                    getContext().getContentResolver().notifyChange(wordUri, null);
                    return wordUri;
                }else{
                    Log.i("provider", "数据插入失败");
                }
                break;

            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int num = 0;
        switch (matcher.match(uri)) {

            case CHAT:
                long id = ContentUris.parseId(uri);
                String whereClause = "id=" + id;
                if (selection != null && !selection.equals("")) {
                    whereClause = whereClause + "and" + selection;
                }
                num = db.delete("chat", whereClause, selectionArgs);

                break;

            case CHATS:
                num = db.delete("chat", selection, selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int num = 0;
        switch (matcher.match(uri)) {

            case CHAT:
                long id = ContentUris.parseId(uri);
                String whereClause = "id=" + id;
                if (selection != null && !selection.equals("")) {
                    whereClause = whereClause + "and" + selection;
                }
                num = db.update("chat", values, whereClause, selectionArgs);
                break;

            case CHATS:
                num = db.update("chat", values, selection, selectionArgs);

                break;

            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }


    private class MyDataBaseHelper extends SQLiteOpenHelper {


        public MyDataBaseHelper(Context context) {
            super(context, "xmpp_chat.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //String user, String nickname, String icon, int type, String content, String sex, String too, int viewType, long time
            String sql = "create table chat(id integer primary key autoincrement,main text,user text,nickname text,icon text,type integer,content text,sex text,too text,viewtype integer,time text)";
            db.execSQL(sql); // 执行一个sql语句
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
