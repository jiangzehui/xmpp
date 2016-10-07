package com.yyquan.jzh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ContentFragmentPageAadpter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentsList;

    public ContentFragmentPageAadpter(FragmentManager fm) {
        super(fm);
    }

    public ContentFragmentPageAadpter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }


    @Override
    public Fragment getItem(int index) {
        return fragmentsList.get(index);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

}
