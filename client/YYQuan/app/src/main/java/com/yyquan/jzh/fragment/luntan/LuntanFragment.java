package com.yyquan.jzh.fragment.luntan;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.demievil.library.RefreshLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.LuntanToStateActivity;
import com.yyquan.jzh.activity.ShowLuntanActivity;
import com.yyquan.jzh.adapter.LuntanListViewAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;
import com.yyquan.jzh.entity.News_luntan;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.ToastUtil;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class LuntanFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    private View view;
    RefreshLayout mRefreshLayout;
    ListView mlistview;
    private TextView tv_more;
    private ProgressBar pb;
    int index = 0;
    int news_size;
    View footerLayout;
    View headLayout;

    private ArrayList<News_luntan> mlist;
    LuntanListViewAdapter adapter;

    FloatingActionButton fab;
    Intent intent;
    public User user;

    CircleImageView iv_icon;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    String url = Ip.ip + "/YfriendService/DoGetLunTan";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            DialogView.Initial(getActivity(), "正在加载动态......");
            intent = getActivity().getIntent();
            user = (User) intent.getSerializableExtra("user");
            view = inflater.inflate(R.layout.fragment_luntan, container, false);
            footerLayout = getActivity().getLayoutInflater().inflate(R.layout.luntan_list_item_more, null);
            headLayout = getActivity().getLayoutInflater().inflate(R.layout.luntan_list_item_view, null);
            iv_icon = (CircleImageView) headLayout.findViewById(R.id.luntan_imageview_icon);
            if (user.getIcon().equals("")) {
                if (user.getSex().equals("男")) {
                    iv_icon.setImageResource(R.mipmap.me_icon_man);
                } else {
                    iv_icon.setImageResource(R.mipmap.me_icon_woman);
                }
            } else {
                if (user.getIcon().substring(0, 4).equals("http")) {
                    Picasso.with(getActivity()).load(user.getIcon()).resize(200, 200).centerInside().placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).into(iv_icon);
                } else {
                    Picasso.with(getActivity()).load(url_icon + user.getIcon()).resize(200, 200).centerInside().placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).into(iv_icon);
                }
            }
            mlistview = (ListView) view.findViewById(R.id.fragment_luntan_listview);
            fab = (FloatingActionButton) view.findViewById(R.id.fab);
            fab.attachToListView(mlistview);
            fab.setOnClickListener(this);
            fab.setColorNormal(getResources().getColor(R.color.title));
            fab.setColorPressed(getResources().getColor(R.color.title));
            fab.setColorRipple(getResources().getColor(R.color.title));
            fab.setShadow(true);
            mRefreshLayout = (RefreshLayout) view.findViewById(R.id.fragment_luntan_freshLayout);

            tv_more = (TextView) footerLayout.findViewById(R.id.text_more);
            pb = (ProgressBar) footerLayout.findViewById(R.id.load_progress_bar);
            tv_more.setOnClickListener(this);
            mlistview.setOnItemClickListener(this);
            mlistview.addFooterView(footerLayout);
            mlistview.addHeaderView(headLayout);

            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setOnLoadListener(this);

            mRefreshLayout.setChildView(mlistview);
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_dark);


            mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    // Toast.makeText(getActivity(),i+"",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0) {

                        fab.show();

                    } else {

                        fab.hide();
                    }
                }
            });
            DialogView.show();
            Message m = h.obtainMessage(1);
            m.arg1 = 0;
            h.sendMessage(m);

        }


        return view;

    }

    public void updateData(String icon) {
        Picasso.with(getActivity()).load(url_icon + icon).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerCrop().into(iv_icon);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void update() {

        Message m = h.obtainMessage(1);
        m.arg1 = 0;
        h.sendMessage(m);
        if (user.getIcon().equals("")) {
            if (user.getSex().equals("男")) {
                iv_icon.setImageResource(R.mipmap.me_icon_man);
            } else {
                iv_icon.setImageResource(R.mipmap.me_icon_woman);
            }
        } else {
            if (user.getIcon().substring(0, 4).equals("http")) {
                Picasso.with(getActivity()).load(user.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerCrop().into(iv_icon);
            } else {
                Picasso.with(getActivity()).load(url_icon + user.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerCrop().into(iv_icon);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent i = new Intent(getActivity(), LuntanToStateActivity.class);
                i.putExtra("user", user);
                getActivity().startActivityForResult(i, 200);
                break;
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
        Message m = h.obtainMessage(1);
        m.arg1 = index;
        h.sendMessage(m);
    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int limit = msg.arg1;
                RequestParams params = new RequestParams();
                params.put("action", "search");
                params.put("limit", limit + "");
                if (limit == 0) {
                    mlist = new ArrayList<>();
                }
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            DialogView.dismiss();
                            String str = new String(responseBody);
                            if (str != null) {
                                JSONObject object = new JSONObject(str);
                                if (object.getString("code").equals("success")) {
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
                                        mRefreshLayout.setRefreshing(false);
                                    } else {
                                        adapter.setList(mlist);
                                        adapter.notifyDataSetChanged();
                                        tv_more.setVisibility(View.VISIBLE);
                                        pb.setVisibility(View.GONE);
                                        mRefreshLayout.setLoading(false);
                                    }


                                } else {

                                }
                            } else {
                                Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
                                mRefreshLayout.setLoading(false);
                                mRefreshLayout.setRefreshing(false);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                            mRefreshLayout.setLoading(false);
                            mRefreshLayout.setRefreshing(false);
                            DialogView.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        DialogView.dismiss();
                        mRefreshLayout.setLoading(false);
                        mRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    };

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
        Message m = h.obtainMessage(1);
        m.arg1 = index;
        h.sendMessage(m);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position <= mlist.size()) {
            if (position == 0) {

            } else {
                Intent intent = new Intent(getActivity(), ShowLuntanActivity.class);
                intent.putExtra("news_luntan", mlist.get(position - 1));
                User user = (User) getActivity().getIntent().getSerializableExtra("user");
                intent.putExtra("user", user.getUser());
                startActivity(intent);
            }

        }


    }
}
