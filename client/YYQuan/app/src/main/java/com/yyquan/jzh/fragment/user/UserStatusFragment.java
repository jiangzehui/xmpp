package com.yyquan.jzh.fragment.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demievil.library.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.LuntanToStateActivity;
import com.yyquan.jzh.activity.ShowLuntanActivity;
import com.yyquan.jzh.adapter.LuntanListViewAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_luntan;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/2/4.
 */
public class UserStatusFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    View view;
    User user;

    @Bind(R.id.fragment_luntan_listview)
    ListView mlistview;
    @Bind(R.id.fragment_luntan_freshLayout)
    RefreshLayout mRefreshLayout;
    @Bind(R.id.tv)
    TextView tv;

    private TextView tv_more;
    private ProgressBar pb;
    int index = 0;
    int news_size;
    View footerLayout;


    private ArrayList<News_luntan> mlist;
    LuntanListViewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_status, container, false);
            initialView();
            getUserMessage(0);
        }

        return view;
    }

    /**
     * 初始化控件
     */
    void initialView() {
        ButterKnife.bind(this, view);
        Bundle bd = getArguments();
        if (bd != null) {
            user = (User) bd.getSerializable("user");
        }
        footerLayout = getActivity().getLayoutInflater().inflate(R.layout.luntan_list_item_more, null);
        mlistview = (ListView) view.findViewById(R.id.fragment_luntan_listview);
        mRefreshLayout = (RefreshLayout) view.findViewById(R.id.fragment_luntan_freshLayout);
        tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
        pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
        tv_more.setOnClickListener(this);
        mlistview.setOnItemClickListener(this);
        mlistview.addFooterView(footerLayout);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadListener(this);
        mRefreshLayout.setChildView(mlistview);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_dark);
    }


    /**
     * 获取用户资料
     */
    void getUserMessage(int limit) {
        if (limit == 0) {
            mlist = new ArrayList<>();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user", user.getUser());
        params.put("action", "search_user");
        params.put("limit", limit);
        client.post(Ip.ip_user_status, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                mRefreshLayout.setLoading(false);
                mRefreshLayout.setRefreshing(false);

                try {

                    String str = new String(responseBody);
                    if (str != null) {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            mRefreshLayout.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.GONE);
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                object = array.getJSONObject(i);
                                News_luntan news = new News_luntan();
                                User u = new User();
                                u.setNickname(object.getString("nickname"));
                                u.setSex(object.getString("sex"));
                                u.setIcon(object.getString("icon"));
                                u.setUser(object.getString("user"));
                                news.setLid(object.getInt("lid"));
                                news.setUser(u);
                                news.setTime(object.getString("time"));
                                news.setContent(object.getString("content"));
                                news.setImage(object.getString("image"));
                                news.setLocation(object.getString("location"));
                                news.setPinglun(object.getString("pinglun_size"));
                                news_size = object.getInt("state_size");
                                mlist.add(news);
                            }

                            if (index == 0) {
                                adapter = new LuntanListViewAdapter(getActivity(), mlist);
                                mlistview.setAdapter(adapter);

                            } else {
                                adapter.setList(mlist);
                                adapter.notifyDataSetChanged();
                                tv_more.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.GONE);

                            }
                        } else {
                            mRefreshLayout.setVisibility(View.GONE);
                            tv.setVisibility(View.VISIBLE);

                        }
                    } else {
                        Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.setLoading(false);
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_more:
                loadData();
                break;
        }
    }

    private void loadData() {
        index += 10;
        if (news_size == mlist.size()) {
            tv_more.setText("数据已加载完毕");
            tv_more.setEnabled(false);
            return;
        }
        tv_more.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        getUserMessage(index);

    }

    @Override
    public void onLoad() {
        loadData();
    }

    @Override
    public void onRefresh() {
        index = 0;
        tv_more.setEnabled(true);
        tv_more.setText("加载更多");
        tv_more.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        mRefreshLayout.setLoading(false);
        getUserMessage(index);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (ShowLuntanActivity.instance != null) {
            getActivity().finish();
        }
        Intent intent = new Intent(getActivity(), ShowLuntanActivity.class);
        intent.putExtra("news_luntan", mlist.get(position));
        startActivity(intent);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (view != null) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
        }
    }
}
