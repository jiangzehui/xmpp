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
import com.yyquan.jzh.entity.XmppMessage;
import com.yyquan.jzh.util.TimeUtil;

/**
 * Created by Administrator on 2016/1/10.
 */
public class XmppContentProvider extends ContentProvider {

    static final String PROVIDER_NAME =
            "com.yyquan.jzh.xmpp";
    //用来区分操作的是单条数据还是多条数据
    private static final int MESSAGE = 1;
    private static final int MESSAGES = 2;
    private static UriMatcher matcher;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    public static final Uri CONTENT_MESSAGE_URI = Uri.parse("content://" + PROVIDER_NAME + "/message");
    public static final Uri CONTENT_MESSAGES_URI = Uri.parse("content://" + PROVIDER_NAME + "/messages");


    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //为UriMatcher注册一个uri
        matcher.addURI(PROVIDER_NAME, "message", MESSAGE);
        matcher.addURI(PROVIDER_NAME, "messages", MESSAGES);
    }

    /**
     * 插入一条消息
     *
     * @param xm
     */
    public static void add_message(XmppMessage xm) {
        ContentValues values = new ContentValues();
        values.put("main", xm.getMain());
        values.put("name", xm.getUser().getName());
        values.put("username", xm.getUser().getUserName());
        values.put("too", xm.getTo());
        values.put("type", xm.getType());
        values.put("content", xm.getContent());
        values.put("time", xm.getTime());
        values.put("result", xm.getResult());


        XmppService.resolver.insert(XmppContentProvider.CONTENT_MESSAGES_URI, values);

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
            case MESSAGE:
                return "vnd.android.cursor.item/com.yyquan.jzh";
            case MESSAGES:
                return "vnd.android.cursor.dir/com.yyquan.jzh";
            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch (matcher.match(uri)) {
            case MESSAGE:
                Log.i("provider", "数据查询成功");
                return db.query("message", projection, selection, selectionArgs, null, null, sortOrder);
            case MESSAGES:
                Log.i("provider", "数据查询成功");
                return db.query("message", projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }


    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)) {

            case MESSAGES:
                long rowId = db.insert("message", null, values);
                if (rowId > 0) {
                    Uri wordUri = ContentUris.withAppendedId(uri, rowId);
                    Log.i("provider", "数据插入成功");
                    getContext().getContentResolver().notifyChange(wordUri, null);
                    return wordUri;
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

            case MESSAGE:
                long id = ContentUris.parseId(uri);
                String whereClause = "id=" + id;
                if (selection != null && !selection.equals("")) {
                    whereClause = whereClause + "and" + selection;
                }
                num = db.delete("message", whereClause, selectionArgs);

                break;

            case MESSAGES:
                num = db.delete("message", selection, selectionArgs);

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

            case MESSAGE:
                long id = ContentUris.parseId(uri);
                String whereClause = "id=" + id;
                if (selection != null && !selection.equals("")) {
                    whereClause = whereClause + "and" + selection;
                }
                num = db.update("message", values, whereClause, selectionArgs);
                Log.i("provider", "数据更新成功");
                break;

            case MESSAGES:
                num = db.update("message", values, selection, selectionArgs);
                Log.i("provider", "数据更新成功");
                break;

            default:
                throw new IllegalArgumentException("未知uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }


    private class MyDataBaseHelper extends SQLiteOpenHelper {


        public MyDataBaseHelper(Context context) {
            super(context, "xmpp.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table message(id integer primary key autoincrement,main text,too text,name text,username text,type text,content text,time text,result integer)";
            db.execSQL(sql); // 执行一个sql语句
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
