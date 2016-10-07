package com.yyquan.jzh.fragment.news;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demievil.library.RefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.MainActivity;
import com.yyquan.jzh.activity.NewsContentActivity;
import com.yyquan.jzh.adapter.LoginPagerAdapter;
import com.yyquan.jzh.adapter.TitleListViewAdapter;
import com.yyquan.jzh.entity.CommonConstant;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;
import com.yyquan.jzh.entity.News_type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class titleFragment extends Fragment implements  View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    private View view;
    View footerLayout;

    RefreshLayout mRefreshLayout;
    ListView mlistview;
    private TitleListViewAdapter adapter;
    private ArrayList<News_content> list;
    private News_type news;
    private TextView tv_more;
    private ProgressBar pb;
    int index = 0;
    int news_size;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_title, container, false);
            mlistview = (ListView) view.findViewById(R.id.fragment_content_listview);
            mRefreshLayout = (RefreshLayout) view.findViewById(R.id.fragment_content_swipe_container);
            footerLayout = getActivity().getLayoutInflater().inflate(R.layout.list_item_more, null);
            tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
            pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
            tv_more.setOnClickListener(this);
            mlistview.addFooterView(footerLayout);
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setOnLoadListener(this);
            mlistview.setOnItemClickListener(this);
            mRefreshLayout.setChildView(mlistview);
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_light,
                    android.R.color.black);
            list = new ArrayList<>();
            Bundle bd = getArguments();
            if (bd != null) {
                news = (News_type) bd.getSerializable(CommonConstant.TABPAGE_ENTITY);
                getData(news);

            }


        }

        return view;
    }




    /**
     * 获取数据
     */
    private void getData(News_type news) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("type", news.getType_name());
        params.put("limit", index);
        params.put("action", "search_title");
        client.post(Ip.ip + news.getType_url(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                // Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if (str != null) {

                    try {
                        JSONObject object = new JSONObject(str);

                        if (object.getString("code").equals("success")) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                object = array.getJSONObject(i);
                                News_content content = new News_content();
                                content.setCpinglun(object.getString("pinglun"));
                                news_size = object.getInt("wenzhang");
                                object = object.getJSONObject("cdata");
                                content.setCid(object.getInt("cid"));
                                content.setCtitle(object.getString("ctitle"));
                               // content.setCzhaiyao(object.getString("czhaiyao"));
                                content.setCimage(object.getString("cimage"));
                                content.setCauthor(object.getString("cauthor"));
                               // content.setCcontent(object.getString("ccontent"));
                                content.setCtime(object.getString("ctime"));
                                list.add(content);

                            }
                            Message m = Message.obtain(h, 1);
                            h.sendMessage(m);
                        }
                    } catch (JSONException e) {
                        mRefreshLayout.setLoading(false);
                        mRefreshLayout.setRefreshing(false);
                        e.printStackTrace();

                    }

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络链接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                mRefreshLayout.setLoading(false);
                mRefreshLayout.setRefreshing(false);

            }
        });

    }

    /**
     * 业务逻辑处理
     */
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (index == 0) {
                        adapter = new TitleListViewAdapter(getActivity(), list);
                        mlistview.setAdapter(adapter);
                        mRefreshLayout.setRefreshing(false);
                    } else {
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                        tv_more.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        mRefreshLayout.setLoading(false);
                    }


                    break;
            }
        }
    };

    /**
     * 可见时调用
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null) {

            // getData(news);


        } else {

        }
    }

    /**
     * 单例模式，获取数据并实例化
     *
     * @param news
     * @return
     */
    public static titleFragment newInstance(News_type news) {
        Bundle bd = new Bundle();
        bd.putSerializable(CommonConstant.TABPAGE_ENTITY, news);
        titleFragment fragment = new titleFragment();
        fragment.setArguments(bd);
        return fragment;
    }


    /**
     * 上拉加载方法
     */
    @Override
    public void onLoad() {
        loadData();
    }

    /**
     * 下拉刷新方法
     */
    @Override
    public void onRefresh() {
        index = 0;
        tv_more.setEnabled(true);
        tv_more.setText("加载更多");
        list = new ArrayList<News_content>();
        getData(news);
        tv_more.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        mRefreshLayout.setLoading(false);
    }

    /**
     * 加载更多数据
     */
    private void loadData() {
        index += 10;
        if (news_size == list.size()) {
            tv_more.setText("数据已加载完毕");

            tv_more.setEnabled(false);
            return;
        }
        tv_more.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        getData(news);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position < list.size()) {
            Intent intent = new Intent(getActivity(), NewsContentActivity.class);
            intent.putExtra("news_content", list.get(position));
            intent.putExtra("user", ((MainActivity) this.getActivity()).user);
            intent.putExtra("url", news.getType_url());
            startActivity(intent);
        }


        //Toast.makeText(getActivity(), position + "11", Toast.LENGTH_SHORT).show();


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
