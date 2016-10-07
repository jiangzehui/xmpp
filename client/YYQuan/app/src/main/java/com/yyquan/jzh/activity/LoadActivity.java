package com.yyquan.jzh.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yyquan.jzh.R;
import com.yyquan.jzh.adapter.LoadViewPagerAdapter;


public class LoadActivity extends Activity {

    private ImageView[] points = new ImageView[4];
    private Button load_4_btn;

    private LoadViewPagerAdapter adapter;
    private ViewPager pager;
    private SharedPreferences ferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        ferences = getSharedPreferences("load", LoadActivity.MODE_PRIVATE);
        editor = ferences.edit();
        boolean bool = ferences.getBoolean("boolean", false);
        if (bool) {
            LoadActivity.this.startActivity(new Intent(LoadActivity.this,
                    LogoActivity.class));
            finish();
        }

        points[0] = (ImageView) findViewById(R.id.load_image_round1);
        points[1] = (ImageView) findViewById(R.id.load_image_round2);
        points[2] = (ImageView) findViewById(R.id.load_image_round3);
        points[3] = (ImageView) findViewById(R.id.load_image_round4);

        setPoint(0);

        pager = (ViewPager) findViewById(R.id.load_viewPager);
        List<View> list = new ArrayList<View>();
        list.add(getLayoutInflater().inflate(R.layout.load_image1, null));
        list.add(getLayoutInflater().inflate(R.layout.load_image2, null));
        list.add(getLayoutInflater().inflate(R.layout.load_image3, null));
        View v4 = getLayoutInflater().inflate(R.layout.load_image4, null);
        list.add(v4);
        v4.setOnClickListener(onClickListener);
        adapter = new LoadViewPagerAdapter(list);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(listener);

    }

    /**
     * 监听按钮
     */
    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            LoadActivity.this.startActivity(new Intent(LoadActivity.this,
                    LogoActivity.class));
            editor.putBoolean("boolean", true);
            editor.commit();
            finish();

        }
    };

    private void setPoint(int index) {
//        for (int i = 0; i < points.length; i++) {
//            if (i == index) {
//                points[i].setImageResource(R.drawable.load_round_2);
//            } else {
//                points[i].setImageResource(R.drawable.load_round);
//            }
//        }
        switch (index) {
            case 0:
                points[0].setImageResource(R.drawable.load_round);
                points[1].setImageResource(R.drawable.load_round_2);
                points[2].setImageResource(R.drawable.load_round_2);
                points[3].setImageResource(R.drawable.load_round_2);
                break;
            case 1:
                points[0].setImageResource(R.drawable.load_round);
                points[1].setImageResource(R.drawable.load_round);
                points[2].setImageResource(R.drawable.load_round_2);
                points[3].setImageResource(R.drawable.load_round_2);
                break;
            case 2:
                points[0].setImageResource(R.drawable.load_round);
                points[1].setImageResource(R.drawable.load_round);
                points[2].setImageResource(R.drawable.load_round);
                points[3].setImageResource(R.drawable.load_round_2);
                break;
            case 3:
                points[0].setImageResource(R.drawable.load_round);
                points[1].setImageResource(R.drawable.load_round);
                points[2].setImageResource(R.drawable.load_round);
                points[3].setImageResource(R.drawable.load_round);
                break;
        }
    }

    /**
     * 监听滑动页面
     */
    private OnPageChangeListener listener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            setPoint(arg0);

            //Toast.makeText(LoadActivity.this, "这是第" + arg0, 1).show();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

}
