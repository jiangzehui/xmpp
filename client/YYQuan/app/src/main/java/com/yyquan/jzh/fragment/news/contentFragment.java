package com.yyquan.jzh.fragment.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.NewsContentActivity;
import com.yyquan.jzh.activity.ShowImageActivity;
import com.yyquan.jzh.adapter.TitleListViewAdapter;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;
import com.yyquan.jzh.entity.News_type;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/28.
 */
public class contentFragment extends Fragment {
    View layout_view;
    private View view;
    //private View layout_view;
    private TextView tv_title;
    private TextView tv_author;
    private TextView tv_time;

    private LinearLayout ll_content;
    News_content content;
    String url = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            DialogView.Initial(getActivity(),"正在加载内容......");
            view = inflater.inflate(R.layout.fragment_content, container, false);

            content = ((NewsContentActivity) getActivity()).content;
            url = ((NewsContentActivity) getActivity()).content_url;


            tv_title = (TextView) view.findViewById(R.id.news_content_textView_title);
            tv_author = (TextView) view.findViewById(R.id.news_content_textView_author);
            tv_time = (TextView) view.findViewById(R.id.news_content_textView_time);

            ll_content = (LinearLayout) view.findViewById(R.id.news_content_layout_content);
            tv_title.setText(content.getCtitle());
            tv_author.setText(content.getCauthor());
            tv_time.setText(content.getCtime());
            //tv_content.setText(content.getCcontent());
            DialogView.show();
            getData(content.getCid());


        }


        return view;
    }


    /**
     * 获取数据
     */
    private void getData(int cid) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("action", "search_content");
        params.put("cid", cid);
        client.post(Ip.ip + url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String str = new String(responseBody);
                // Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                if (str != null) {

                    try {
                        JSONObject object = new JSONObject(str);

                        if (object.getString("code").equals("success")) {
                            content.setCcontent(object.getString("data"));
                            Message m = Message.obtain(h, 1);
                            h.sendMessage(m);
                        }
                    } catch (JSONException e) {
                        DialogView.dismiss();
                        e.printStackTrace();

                    }

                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络链接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                DialogView.dismiss();

            }
        });

    }

    /**
     * 业务逻辑处理
     */
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DialogView.dismiss();
                    final String[] str = content.getCcontent().split(";;");
                    for (int i = 0; i < str.length; i++) {


                        layout_view = LayoutInflater.from(getActivity()).inflate(R.layout.news_content_item, null);
                        ImageView iv = (ImageView) layout_view.findViewById(R.id.news_content_imageView_item);
                        TextView tv = (TextView) layout_view.findViewById(R.id.news_content_title_item);
                        tv.setTextColor(Color.BLACK);
                        layout_view.setPadding(25, 25, 25, 25);
                        if (i % 2 == 0) {
                            tv.setText(str[i]);
                        } else {
                            if (str[i].substring(0, 4).equals("http")) {
                                Picasso.with(getActivity()).load(str[i]).resize(500, 500).centerInside().placeholder(R.mipmap.aio_image_default_round).error(R.mipmap.aio_image_default_round).into(iv);
                            } else {
                                Picasso.with(getActivity()).load(Ip.ip + str[i]).resize(500, 500).centerInside().placeholder(R.mipmap.aio_image_default_round).error(R.mipmap.aio_image_default_round).into(iv);
                            }

                        }
                        final int finalI = i;
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ShowImageActivity.class);

                                intent.putExtra("path", str[finalI]);
                                intent.putExtra("type", "news");
                                startActivity(intent);
                            }
                        });


                        ll_content.addView(layout_view);

                    }


                    break;
            }
        }
    };
}
