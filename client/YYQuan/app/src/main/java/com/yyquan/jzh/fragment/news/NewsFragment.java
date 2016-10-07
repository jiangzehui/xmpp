package com.yyquan.jzh.fragment.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yyquan.jzh.R;
import com.yyquan.jzh.adapter.MainFragmentPagerAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_type;
import com.yyquan.jzh.fragment.news.titleFragment;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class NewsFragment extends Fragment {

    private View view;
    private TabLayout lauout;
    private ViewPager mViewpager;
    private MainFragmentPagerAdapter mPageAdapter;

    private List<Fragment> childFragments;
    private List<News_type> list;
    private String url = Ip.ip + "/YfriendService/DoGetType";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_news, container, false);

        initialView();
        getNewsTitle();


        return view;

    }

    /**
     * 获取新闻的类型
     */
    private void getNewsTitle() {
        DialogView.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        list = new ArrayList<News_type>();
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            JSONArray array = object.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                News_type news_type = new News_type();
                                news_type.setId(object1.getInt("id"));
                                news_type.setType_name(object1.getString("type_name"));
                                news_type.setType_url(object1.getString("type_url"));

                                list.add(news_type);
                            }
                            initialData();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    /**
     * 初始化数据
     */
    private void initialData() {
        {

            childFragments = new ArrayList<Fragment>();


            for (int i = 0; i < list.size(); i++) {
                childFragments.add(titleFragment.newInstance(list.get(i)));
            }

            mPageAdapter = new MainFragmentPagerAdapter(getChildFragmentManager(), list, childFragments);
            mViewpager.setAdapter(mPageAdapter);
            lauout.setupWithViewPager(mViewpager);
            lauout.setTabMode(TabLayout.MODE_FIXED);
            DialogView.dismiss();


        }
    }

    /**
     * 初始化控件
     */
    private void initialView() {
        lauout = (TabLayout) view.findViewById(R.id.id_pageindicator);
        mViewpager = (ViewPager) view.findViewById(R.id.id_viewpager);
        DialogView.Initial(getActivity(),"正在加载文章......");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
        }
    }
}
