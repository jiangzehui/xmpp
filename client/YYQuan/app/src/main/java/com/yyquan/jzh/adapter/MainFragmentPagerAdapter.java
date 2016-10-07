package com.yyquan.jzh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.yyquan.jzh.entity.News_type;

import java.util.List;

/**
 * Created by jzh on 2015/9/26.
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    List<News_type> list;
    List<Fragment> childFragments;

    public MainFragmentPagerAdapter(FragmentManager fm, List<News_type> list, List<Fragment> childFragments) {
        super(fm);

        this.list = list;
        this.childFragments = childFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return childFragments.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getType_name();
    }
}
