package com.yyquan.jzh.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demievil.library.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.yyquan.jzh.R;
import com.yyquan.jzh.adapter.LunTanPingLunListViewAdapter;
import com.yyquan.jzh.adapter.PingLunListViewAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_luntan;
import com.yyquan.jzh.entity.News_pinglun;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.location.Location;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.view.DialogView;
import com.yyquan.jzh.xmpp.XmppTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jzh on 2015/11/8.
 */
public class ShowLuntanActivity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    RefreshLayout mRefreshLayout;
    LinearLayout ll_back;
    ListView mlistview;

    View footerLayout;

    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    Intent intent;
    News_luntan news_luntan;
    //String user;
    private TextView tv_location;

    private TextView tv_pinglun_total;
    private TextView tv_uppinglun;
    private EditText et_pinglun;

    private CircleImageView iv_icon;
    GridView gv;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tv_content;
    String url = Ip.ip + "/YfriendService/DoGetLunTan";
    ArrayList<News_pinglun> list;
    LunTanPingLunListViewAdapter adapter;
    private TextView tv_more;
    private ProgressBar pb;

    int pinglun_size;
    int index = 0;
    Location lt;
    InputMethodManager imm;//键盘管理器
    User users;

    XGPushClickedResult result;
    boolean xg_bool = false;
    public static ShowLuntanActivity instance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_luntan);
        instance=this;
        users = SaveUserUtil.loadAccount(this);
        result = XGPushManager.onActivityStarted(ShowLuntanActivity.this);
        if (result != null) {
            xg_bool = true;
            // 获取自定义key-value
            String customContent = result.getCustomContent();
            if (customContent != null && customContent.length() != 0) {
                try {
                    JSONObject obj = new JSONObject(customContent);
                    // key1为前台配置的key
                    if (!obj.isNull("data")) {
                        news_luntan = new News_luntan();
                        String data = obj.getString("data");
                        JSONObject object = new JSONObject(data);
                        User userr = new User();
                        userr.setUser(object.getString("user"));
                        userr.setNickname(object.getString("nickname"));
                        userr.setIcon(object.getString("icon"));
                        userr.setSex(object.getString("sex"));
                        news_luntan.setUser(userr);
                        news_luntan.setLid(object.getInt("lid"));
                        news_luntan.setContent(object.getString("content"));
                        news_luntan.setImage(object.getString("image"));
                        news_luntan.setTime(object.getString("time"));
                        news_luntan.setLocation(object.getString("location"));


                    }
                    // ...
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            intent = getIntent();
            news_luntan = (News_luntan) intent.getSerializableExtra("news_luntan");

        }


        initialView();

    }

    private void initialView() {
        imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        DialogView.Initial(this, "正在评论......");
        lt = new Location(this);
        list = new ArrayList<>();
        final String[] grid_img = news_luntan.getImage().split(";");

        ll_back = (LinearLayout) findViewById(R.id.show_luntan_layout_back);
        ll_back.setOnClickListener(this);
        footerLayout = getLayoutInflater().inflate(R.layout.list_item_more, null);
        if (news_luntan.getImage().equals("")) {

        } else {
            gv = (GridView) findViewById(R.id.show_luntan_gridview);
            gv.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return grid_img.length;
                }

                @Override
                public Object getItem(int position) {
                    return grid_img[position];
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    ViewHolder holder;
                    if (convertView == null) {
                        holder = new ViewHolder();
                        convertView = LayoutInflater.from(ShowLuntanActivity.this).inflate(R.layout.layout_imagview, null);
                        holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
                        holder.iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ShowLuntanActivity.this, ShowImageActivity.class);
                                intent.putExtra("str[]", grid_img);
                                intent.putExtra("type", "luntan");
                                intent.putExtra("number", position);
                                startActivity(intent);
                            }
                        });
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    final String urlpath = Ip.ip + "/YfriendService/DoGetLunTan?action=search_image&name=" + grid_img[position];
                    Picasso.with(ShowLuntanActivity.this)
                            .load(urlpath)
                            .resize(200, 200).centerCrop()
                            .placeholder(R.mipmap.aio_image_default_round)
                            .error(R.mipmap.aio_image_default_round)
                            .into(holder.iv);
                    return convertView;
                }
            });
        }


        tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
        tv_more.setOnClickListener(this);
        pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);

        iv_icon = (CircleImageView) findViewById(R.id.show_luntan_imageview_icon);
        tv_name = (TextView) findViewById(R.id.show_luntan_textView_name);

        tv_pinglun_total = (TextView) findViewById(R.id.show_luntan_pinglun_total);
        tv_uppinglun = (TextView) findViewById(R.id.show_luntan_text_enterpinglun);
        tv_uppinglun.setOnClickListener(this);
        et_pinglun = (EditText) findViewById(R.id.show_luntan_editText_pinglun);

        tv_location = (TextView) findViewById(R.id.show_luntan_textView_location);
        tv_time = (TextView) findViewById(R.id.show_luntan_textView_time);
        tv_content = (TextView) findViewById(R.id.show_luntan_textView_content);
        tv_name.setText(news_luntan.getUser().getNickname());

        if (news_luntan.getLocation().equals("")) {
            tv_location.setVisibility(View.GONE);
        } else {
            tv_location.setVisibility(View.VISIBLE);

            tv_location.setText(" " + news_luntan.getLocation());
        }
        if (news_luntan.getUser().getIcon().equals("")) {
            if (news_luntan.getUser().getSex().equals("男")) {
                iv_icon.setImageResource(R.mipmap.me_icon_man);
            } else {
                iv_icon.setImageResource(R.mipmap.me_icon_woman);
            }
        } else {
            if (news_luntan.getUser().getIcon().substring(0, 4).equals("http")) {
                Picasso.with(this).load(news_luntan.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(iv_icon);
            } else {
                Picasso.with(this).load(url_icon + news_luntan.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(iv_icon);
            }
        }
        tv_time.setText(news_luntan.getTime());
        tv_content.setText(news_luntan.getContent());


        mlistview = (ListView) findViewById(R.id.show_luntan_listview);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.show_luntan_freshLayout);

        mlistview.addFooterView(footerLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadListener(this);

        mRefreshLayout.setChildView(mlistview);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_dark);
        getData(news_luntan.getLid(), 0);

    }

    /**
     * 根据文章id获取评论
     *
     * @param plid
     */
    public void getData(int plid, int limit) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("plid", plid);
        params.put("user", users.getUser());
        params.put("limit", limit);
        params.put("action", "search_pinglun");
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                {
                    String str = new String(responseBody);
                    if (str != null) {
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.getString("code").equals("success")) {

                                JSONArray array = object.getJSONArray("data");
                                for (int i = 0; i < array.length(); i++) {
                                    object = array.getJSONObject(i);
                                    News_pinglun pinglun = new News_pinglun();
                                    User users = new User();
                                    users.setNickname(object.getString("nickname"));
                                    pinglun_size = object.getInt("size");
                                    users.setSex(object.getString("sex"));
                                    users.setIcon(object.getString("icon"));
                                    pinglun.setIspzan(object.getString("ispzan"));

                                    JSONObject ob = object.getJSONObject("pdata");
                                    pinglun.setPcid(ob.getInt("pcid"));
                                    pinglun.setPid(ob.getInt("pid"));
                                    pinglun.setPcontent(ob.getString("pcontent"));
                                    pinglun.setPlocation(ob.getString("plocation"));
                                    pinglun.setPtime(ob.getString("ptime"));
                                    pinglun.setPzan(ob.getString("pzan"));
                                    users.setUser(ob.getString("user"));
                                    pinglun.setUser(users);
                                    list.add(pinglun);
                                }

                                if (list.size() < 0) {
                                    return;
                                } else {

                                }


                                tv_pinglun_total.setText("热门评论(" + pinglun_size + ")");
                                // ((NewsContentActivity)getActivity()).pl_size=pinglun_size;
                                if (index == 0) {

                                    adapter = new LunTanPingLunListViewAdapter(ShowLuntanActivity.this, list, news_luntan.getUser().getUser());
                                    mlistview.setAdapter(adapter);
                                    mlistview.setVisibility(View.VISIBLE);
                                    if (xg_bool) {
                                        xg_bool = false;
                                        mlistview.setSelection(list.size() - 1);
                                    }

                                } else {

                                    adapter.setList(list);
                                    adapter.notifyDataSetChanged();
                                    tv_more.setVisibility(View.VISIBLE);
                                    pb.setVisibility(View.GONE);
                                }


                            } else {
                                //  tv_total.setText("暂无评论");

                            }
                            mRefreshLayout.setRefreshing(false);
                            mRefreshLayout.setLoading(false);
                        } catch (JSONException e) {
                            mRefreshLayout.setRefreshing(false);
                            mRefreshLayout.setLoading(false);
                            e.printStackTrace();
                        }
                    } else {
                        mRefreshLayout.setLoading(false);
                        mRefreshLayout.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.setRefreshing(false);
                mRefreshLayout.setLoading(false);
            }
        });
    }


    /**
     * 提交评论
     */
    private void upPingLun() {


        String pinglun = et_pinglun.getText().toString();
        if (pinglun.equals("")) {
            Toast.makeText(ShowLuntanActivity.this, "请先评论", Toast.LENGTH_SHORT).show();

            return;
        }
        DialogView.show();
        tv_uppinglun.setEnabled(false);
        Date date = new Date();
        long ptime = date.getTime();
        RequestParams params = new RequestParams();
        params.put("plid", news_luntan.getLid());
        params.put("author", news_luntan.getUser().getUser());
        params.put("action", "save_pinglun");
        params.put("user", users.getUser());
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

                            Toast.makeText(ShowLuntanActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            et_pinglun.setText("");
                            if (imm.isActive()) {//关闭键盘
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            refreshData();

                            //更新评论内容

                        } else {
                            Toast.makeText(ShowLuntanActivity.this, "评论失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        tv_uppinglun.setEnabled(true);
                        DialogView.dismiss();
                        e.printStackTrace();
                        Toast.makeText(ShowLuntanActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tv_uppinglun.setEnabled(true);
                DialogView.dismiss();
                Toast.makeText(ShowLuntanActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.show_luntan_layout_back:

                if (result != null) {
                    if (MainActivity.main == null) {
                        new Thread() {

                            @Override
                            public void run() {
                                boolean result = XmppTool.getInstance().login(users.getUser(), users.getPassword(), ShowLuntanActivity.this);
                                if (result) {

                                    Intent intent = new Intent(ShowLuntanActivity.this, MainActivity.class);
                                    intent.putExtra("user", users);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    finish();
                                }
                            }

                        }.start();


                    } else {
                        finish();
                    }


                } else {
                    finish();
                }

                break;
            case R.id.show_luntan_text_enterpinglun:
                upPingLun();
                break;
            case R.id.text_more:
                loadData();
                break;
        }

    }

    @Override
    public void onLoad() {
        loadData();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    /**
     * 加载更多数据
     */
    private void loadData() {
        index += 10;
        if (pinglun_size == list.size()) {
            tv_more.setText("数据已加载完毕");
            tv_more.setEnabled(false);

            return;
        }
        tv_more.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        getData(news_luntan.getLid(), index);
    }


    /**
     * 刷新数据
     */
    public void refreshData() {
        index = 0;
        tv_more.setEnabled(true);
        tv_more.setText("加载更多");
        list = new ArrayList<>();
        getData(news_luntan.getLid(), 0);
        tv_more.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        mRefreshLayout.setLoading(false);
    }

    class ViewHolder {
        ImageView iv;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lt.stopLocation();
        instance=null;
    }


    @Override
    public void onBackPressed() {
        instance=null;
        if (result != null) {
            if (MainActivity.main == null) {
                new Thread() {

                    @Override
                    public void run() {
                        boolean result = XmppTool.getInstance().login(users.getUser(), users.getPassword(), ShowLuntanActivity.this);
                        if (result) {

                            Intent intent = new Intent(ShowLuntanActivity.this, MainActivity.class);
                            intent.putExtra("user", users);
                            startActivity(intent);
                            finish();
                        } else {

                            finish();
                        }
                    }

                }.start();


            } else {
                finish();
            }


        } else {
            finish();
        }

    }
}
