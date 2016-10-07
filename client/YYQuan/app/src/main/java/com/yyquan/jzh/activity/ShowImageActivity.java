package com.yyquan.jzh.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImageActivity extends Activity {

    ImageView iv;
    String path;
    String type;
    String[] patharr;
    int pagerNumber;
    PhotoViewAttacher mAttacher;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    ViewPager viewPager;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        path = intent.getStringExtra("path");
        patharr = intent.getStringArrayExtra("str[]");
        pagerNumber = intent.getIntExtra("number", 0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (type.equals("luntan")) {
            setContentView(R.layout.activity_load);
            findViewById(R.id.xiaoyuandian).setVisibility(View.GONE);
            viewPager = (ViewPager) findViewById(R.id.load_viewPager);
            text = (TextView) findViewById(R.id.showimage_text);
            text.setVisibility(View.VISIBLE);
            text.setText("1/" + patharr.length);
            viewPager.setAdapter(new PagerAdapter() {


                @Override
                public int getCount() {
                    return patharr.length;
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view.equals(object);
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }

                @Override
                public Object instantiateItem(ViewGroup container, final int position) {
                    final ImageView imageView = new ImageView(ShowImageActivity.this);
                    Picasso.with(ShowImageActivity.this)
                            .load(Ip.ip + "/YfriendService/DoGetLunTan?action=search_image&name=" + patharr[position])
                            .placeholder(R.mipmap.aio_image_default_round)
                            .error(R.mipmap.aio_image_default_round)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mAttacher = new PhotoViewAttacher(imageView);


                                }

                                @Override
                                public void onError() {
                                }
                            });
                    container.addView(imageView);
                    return imageView;
                }
            });
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {


                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    text.setText(position + 1 + "/" + patharr.length);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }


            });
            viewPager.setCurrentItem(pagerNumber);
        } else if (type.equals("icon")) {
            setContentView(R.layout.activity_show_image);

            iv = (ImageView) findViewById(R.id.showimage);

            if (type.equals("icon")) {
                if (path.equals("男")) {
                    iv.setImageResource(R.mipmap.me_icon_man);
                    mAttacher = new PhotoViewAttacher(iv);
                } else if (path.equals("女")) {
                    iv.setImageResource(R.mipmap.me_icon_woman);
                    mAttacher = new PhotoViewAttacher(iv);
                } else {
                    if (path.substring(0, 4).equals("http")) {
                        Picasso.with(this).load(path).into(iv, new Callback() {
                            @Override
                            public void onSuccess() {
                                mAttacher = new PhotoViewAttacher(iv);
                            }

                            @Override
                            public void onError() {
                            }
                        });
                    } else {
                        Picasso.with(this).load(url_icon + path).into(iv, new Callback() {
                            @Override
                            public void onSuccess() {
                                mAttacher = new PhotoViewAttacher(iv);
                            }

                            @Override
                            public void onError() {
                            }
                        });
                    }
                }
            }
        } else if (type.equals("news")) {
            setContentView(R.layout.activity_show_image);

            iv = (ImageView) findViewById(R.id.showimage);
            Picasso.with(this).load(path).into(iv, new Callback() {
                @Override
                public void onSuccess() {
                    mAttacher = new PhotoViewAttacher(iv);
                }

                @Override
                public void onError() {
                }
            });

        }
    }

}
