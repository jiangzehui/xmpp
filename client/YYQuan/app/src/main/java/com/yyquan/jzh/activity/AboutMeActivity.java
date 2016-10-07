package com.yyquan.jzh.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.yyquan.jzh.R;

/**
 * Created by Administrator on 2015/11/24.
 */
public class AboutMeActivity extends BaseActivity {
    LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        initialView();
    }

    @Override
    public void initialView() {
        ll_back= (LinearLayout) findViewById(R.id.aboutme_layout_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
