package com.yyquan.jzh.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;

import android.view.View;
import android.view.ViewGroup;

public class LoadViewPagerAdapter extends PagerAdapter {

	private List<View> list;

	public LoadViewPagerAdapter(List<View> list) {
		
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == (View) arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(list.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = list.get(position);
		container.addView(view);
		return view;
	}

}
