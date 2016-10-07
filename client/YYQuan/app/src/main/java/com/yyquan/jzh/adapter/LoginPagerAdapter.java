package com.yyquan.jzh.adapter;



import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.yyquan.jzh.R;

public class LoginPagerAdapter extends PagerAdapter {

	int[] img;
	Context context;
	ViewPager pager;

	public LoginPagerAdapter(int[] img, Context context, ViewPager pager) {
		super();
		this.img = img;
		this.context = context;
		this.pager = pager;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img.length;
	}

	@Override
	public Object instantiateItem(View view, int position) {

		View arg0 = LayoutInflater.from(context).inflate(
				R.layout.login_viewpager_item, null);
		ImageView iv = (ImageView) arg0
				.findViewById(R.id.login_viewpageritem_img);

		iv.setBackgroundResource(img[position]);
		pager.addView(arg0);
		return arg0;
	}

	@Override
	public void destroyItem(View view, int position, Object object) {
		View arg0 = LayoutInflater.from(context).inflate(
				R.layout.login_viewpager_item, null);
		ImageView iv = (ImageView) arg0
				.findViewById(R.id.login_viewpageritem_img);

		iv.setBackgroundResource(img[position]);

		
		pager.removeView(arg0);

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;

	}

	private class ViewHolder {
		ImageView iv;
	}

}
