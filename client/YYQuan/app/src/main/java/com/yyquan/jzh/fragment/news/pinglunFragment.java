package com.yyquan.jzh.fragment.news;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.demievil.library.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.NewsContentActivity;
import com.yyquan.jzh.adapter.PingLunListViewAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;
import com.yyquan.jzh.entity.News_pinglun;
import com.yyquan.jzh.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/28.
 */
public class pinglunFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener, View.OnClickListener {

    private View view;
    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_time;
    private TextView tv_total;
    private Intent intent;
    private News_content content;
    private User user;
    private ListView listview;
    private RefreshLayout mRefreshLayout;
    ArrayList<News_pinglun> list;
    PingLunListViewAdapter adapter;
    View footerLayout;
    private TextView tv_more;
    private ProgressBar pb;
    private String search_url = Ip.ip + "/YfriendService/DoGetPingLun";
    int pinglun_size;
    int index = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            list = new ArrayList<>();
            view = inflater.inflate(R.layout.fragment_pinglun, container, false);
            footerLayout = getActivity().getLayoutInflater().inflate(R.layout.list_item_more, null);
            tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
            tv_more.setOnClickListener(this);
            pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
            intent = getActivity().getIntent();
            content = (News_content) intent.getSerializableExtra("news_content");
            tv_title = (TextView) view.findViewById(R.id.news_content_textView_title);
            tv_author = (TextView) view.findViewById(R.id.news_content_textView_author);
            tv_time = (TextView) view.findViewById(R.id.news_content_textView_time);
            tv_total = (TextView) view.findViewById(R.id.news_content_textView_total);
            listview = (ListView) view.findViewById(R.id.news_content_listView);
            listview.addFooterView(footerLayout);
            mRefreshLayout = (RefreshLayout) view.findViewById(R.id.fragment_content_swipe_container);
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setOnLoadListener(this);
            mRefreshLayout.setChildView(listview);
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_light,
                    android.R.color.black);
            tv_title.setText(content.getCtitle());
            tv_author.setText(content.getCauthor());
            tv_time.setText(content.getCtime());
            user = ((NewsContentActivity) getActivity()).user;
            getData(content.getCid(), 0);

        }


        return view;
    }

    /**
     * 根据文章id获取评论
     *
     * @param pcid
     */
    public void getData(int pcid, int limit) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("pcid", pcid);
        params.put("user", user.getUser());
        params.put("limit", limit);
        // params.put("cid", content.getCid());
        params.put("action", "search");
        client.post(search_url, params, new AsyncHttpResponseHandler() {
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
                                    users.setSex(object.getString("sex"));
                                    users.setIcon(object.getString("icon"));
                                    pinglun.setIspzan(object.getString("ispzan"));
                                    JSONObject ob = object.getJSONObject("pdata");

                                    pinglun_size = object.getInt("size");
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
                                if (((NewsContentActivity) getActivity()) == null) {
                                    return;

                                } else {
                                    ((NewsContentActivity) getActivity()).tv_pinglun.setText(pinglun_size + "评");
                                }


                                tv_total.setText("热门评论(" + pinglun_size + ")");
                                ((NewsContentActivity) getActivity()).pl_size = pinglun_size;
                                if (index == 0) {
                                    adapter = new PingLunListViewAdapter(getActivity(), list, user.getUser());
                                    listview.setAdapter(adapter);
                                    listview.setVisibility(View.VISIBLE);

                                } else {

                                    adapter.setList(list);
                                    adapter.notifyDataSetChanged();
                                    tv_more.setVisibility(View.VISIBLE);
                                    pb.setVisibility(View.GONE);
                                }


                            } else {
                                tv_total.setText("暂无评论");
                                listview.setVisibility(View.GONE);
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


    @Override
    public void onLoad() {
        loadData();
    }

    @Override
    public void onRefresh() {
        refreshData();
    }


    /**
     * 刷新数据
     */
    public void refreshData() {
        index = 0;
        tv_more.setEnabled(true);
        tv_more.setText("加载更多");
        list = new ArrayList<>();
        getData(content.getCid(), 0);
        tv_more.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        mRefreshLayout.setLoading(false);
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
        getData(content.getCid(), index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_more:
                loadData();
                break;
        }
    }
}
