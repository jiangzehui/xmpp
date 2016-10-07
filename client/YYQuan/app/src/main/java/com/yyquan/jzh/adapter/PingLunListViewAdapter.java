package com.yyquan.jzh.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_pinglun;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/28.
 */
public class PingLunListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<News_pinglun> news;
    String user;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";

    public PingLunListViewAdapter(Context context, ArrayList<News_pinglun> news, String user) {
        this.context = context;
        this.news = news;
        this.user = user;

    }

    public void setList(ArrayList<News_pinglun> list) {
        news = list;
    }

    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public Object getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_pinglun_item, null);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.listview_pinglun_item_imageview_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_name);
            holder.tv_location = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_location);
            holder.tv_time = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_content);
            holder.tv_zan = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_zan);
            holder.tv_lou = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_lou);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String stime = "";
        holder.tv_name.setText(news.get(position).getUser().getNickname());
        if (news.get(position).getUser().getUser().contains("http")) {
            stime = news.get(position).getPtime();
        } else {
            Date date = new Date();
            long time = date.getTime();
            long sytime = time - Long.parseLong(news.get(position).getPtime());
            long ltime = sytime / 1000;

            if (ltime >= 0 && ltime < 60) {
                if (ltime == 0) {
                    stime = "刚刚";
                } else {
                    stime = ltime + "秒前";
                }

            } else if (ltime >= 60 && ltime < 3600) {
                stime = ltime / 60 + "分钟前";
            } else if (ltime >= 3600 && ltime < 3600 * 24) {
                stime = ltime / 3600 + "小时前";
            } else if (ltime >= 3600 * 24 && ltime < 3600 * 48) {
                stime = "昨天";
            } else if (ltime >= 3600 * 48 && ltime < 3600 * 72) {
                stime = "前天";
            } else if (ltime >= 3600 * 72) {
                stime = ltime / 86400 + "天前";
            } else {
                stime = "1212122";
            }
        }

        holder.tv_time.setText(stime);
        holder.tv_content.setText(news.get(position).getPcontent());
        holder.tv_zan.setText(news.get(position).getPzan() + " ");
        holder.tv_lou.setVisibility(View.GONE);
        if (news.get(position).getPlocation().equals("")) {
            holder.tv_location.setVisibility(View.GONE);
        } else {

            holder.tv_location.setVisibility(View.VISIBLE);
            if (news.get(position).getPlocation().contains("null")) {
                holder.tv_location.setText(" 未知星球");
            } else {

                holder.tv_location.setText(news.get(position).getPlocation());

            }
        }

        Drawable nav_up = null;
        if (news.get(position).getIspzan().equals("1")) {
            nav_up = context.getResources().getDrawable(R.mipmap.pic_btn_liked);
            holder.tv_zan.setEnabled(false);

        } else {
            nav_up = context.getResources().getDrawable(R.mipmap.pic_btn_like);
            holder.tv_zan.setEnabled(true);
        }
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        holder.tv_zan.setCompoundDrawables(null, null, nav_up, null);
        holder.tv_zan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (news.get(position).getIspzan().equals("1")) {

                } else {
                    holder.tv_zan.setEnabled(false);
                    Drawable nav_upp = context.getResources().getDrawable(R.mipmap.pic_btn_liked);
                    nav_upp.setBounds(0, 0, nav_upp.getMinimumWidth(), nav_upp.getMinimumHeight());
                    holder.tv_zan.setCompoundDrawables(null, null, nav_upp, null);
                    holder.tv_zan.setText(Integer.parseInt((news.get(position).getPzan())) + 1 + " ");
                    update_zan(news.get(position).getPid(), position, holder.tv_zan);

                }

            }
        });
        if (news.get(position).getUser().getIcon().equals("")) {
            if (news.get(position).getUser().getSex().equals("男")) {
                holder.iv_icon.setImageResource(R.mipmap.me_icon_man);
            } else {
                holder.iv_icon.setImageResource(R.mipmap.me_icon_woman);
            }
        } else {
            if (news.get(position).getUser().getIcon().substring(0, 4).equals("http")) {
                Picasso.with(context).load(news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(holder.iv_icon);
            } else {
                Picasso.with(context).load(url_icon + news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(holder.iv_icon);
            }

            // Picasso.with(context).load(url_icon+news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerCrop().into(holder.iv_icon);


        }
//

        return convertView;
    }


    private class ViewHolder {
        private TextView tv_name;
        private TextView tv_location;
        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_zan;
        private TextView tv_lou;
        private ImageView iv_icon;
    }


    private void update_zan(int pid, final int position, final TextView tv) {
        RequestParams params = new RequestParams();
        params.put("action", "update");
        params.put("pid", pid);
        params.put("user", user);
        //Toast.makeText(context, pid + "", Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Ip.ip + "/YfriendService/DoGetPingLun", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                news.get(position).setIspzan("1");
                news.get(position).setPzan(Integer.parseInt((news.get(position).getPzan())) + 1 + " ");
                tv.setEnabled(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tv.setEnabled(true);
            }
        });
    }
}
