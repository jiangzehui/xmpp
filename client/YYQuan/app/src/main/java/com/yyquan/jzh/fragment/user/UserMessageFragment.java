package com.yyquan.jzh.fragment.user;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.User;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/2/4.
 */
public class UserMessageFragment extends Fragment {
    View view;
    User user;
    @Bind(R.id.myself_textview_sex)
    TextView myselfTextviewSex;
    @Bind(R.id.myself_textview_age)
    TextView myselfTextviewAge;
    @Bind(R.id.myself_textview_qq)
    TextView myselfTextviewQq;
    @Bind(R.id.myself_textview_id)
    TextView myselfTextviewId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_message, container, false);
            ButterKnife.bind(this, view);
            Bundle bd = getArguments();

            if (bd != null) {
                user = (User) bd.getSerializable("user");
                Drawable nav_up = null;
                if (user.getSex().equals("男")) {
                    myselfTextviewSex.setText("男");
                    nav_up = getResources().getDrawable(R.mipmap.man);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                    myselfTextviewSex.setCompoundDrawables(null, null, nav_up, null);
                } else {
                    myselfTextviewSex.setText("女");
                    nav_up = getResources().getDrawable(R.mipmap.woman);
                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                    myselfTextviewSex.setCompoundDrawables(null, null, nav_up, null);
                }
                myselfTextviewAge.setText(user.getYears());
                myselfTextviewQq.setText(user.getQq());
                myselfTextviewId.setText(user.getId()+"");

            }


        }


        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
