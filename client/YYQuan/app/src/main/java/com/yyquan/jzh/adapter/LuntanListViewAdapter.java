package com.yyquan.jzh.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.MainActivity;
import com.yyquan.jzh.activity.ShowImageActivity;
import com.yyquan.jzh.activity.ShowLuntanActivity;
import com.yyquan.jzh.activity.ShowMessageActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_luntan;
import com.yyquan.jzh.entity.News_pinglun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/28.
 */
public class LuntanListViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<News_luntan> news;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    Activity activity;

    public LuntanListViewAdapter(Context context, ArrayList<News_luntan> news) {
        this.context = context;
        this.news = news;
        activity = (Activity) context;


    }

    public void setList(ArrayList<News_luntan> list) {
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
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.luntan_listview_item, null);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.luntan_listitem_imageview_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_name);
            holder.tv_location = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_location);
            holder.tv_time = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_content);
            holder.tv_pinglun = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_pinglun);
            //holder.tv_zan = (TextView) convertView.findViewById(R.id.luntan_listitem_textView_zan);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.luntan_listitem_photo_list);
            // holder.ll = (LinearLayout) convertView.findViewById(R.id.luntan_listitem_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.ll.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "123", Toast.LENGTH_SHORT).show();
//                Intent intent=new Intent(context, ShowLuntanActivity.class);
//                intent.putExtra("news_luntan",news.get(position));
//                context.startActivity(intent);
//            }
//        });
        holder.tv_name.setText(news.get(position).getUser().getNickname());
        holder.tv_pinglun.setText(news.get(position).getPinglun() + "评 ");
        holder.tv_time.setText(news.get(position).getTime());
        holder.tv_content.setText(news.get(position).getContent());
        if (news.get(position).getLocation().equals("")) {
            holder.tv_location.setVisibility(View.GONE);
        } else {
            holder.tv_location.setVisibility(View.VISIBLE);
            if (news.get(position).getLocation().contains("null")) {
                holder.tv_location.setText(" 未知星球");
            } else {

                holder.tv_location.setText(" " + news.get(position).getLocation());

            }
        }


        // holder.tv_zan.setText(news.get(position).getZan());

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
        }
        if (activity instanceof MainActivity) {
            holder.tv_name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowMessageActivity.class);
                    intent.putExtra("user", news.get(position).getUser().getUser());
                    context.startActivity(intent);
                }
            });
            holder.iv_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowMessageActivity.class);
                    intent.putExtra("user", news.get(position).getUser().getUser());
                    context.startActivity(intent);
                }
            });
        }

        holder.linearLayout.removeAllViews();
        if (news.get(position).getImage().equals("")) {
        } else {
            ImageView imageView;
            final String[] grid_img = news.get(position).getImage().split(";");
            for (int i = 0; i < grid_img.length; i++) {
                imageView = new ImageView(context);
                final String urlpath = Ip.ip + "/YfriendService/DoGetLunTan?action=search_image&name=" + grid_img[i];
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 100),
                        (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 100)));
                int padding = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, 3);
                imageView.setPadding(padding, padding, padding, padding);
                final int finalI = i;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowImageActivity.class);
                        intent.putExtra("str[]", grid_img);
                        intent.putExtra("type", "luntan");
                        intent.putExtra("number", finalI);
                        context.startActivity(intent);
                    }
                });


                Picasso.with(context)
                        .load(urlpath)
                        .resize(200, 200).centerInside()
                        .placeholder(R.mipmap.aio_image_default_round)
                        .error(R.mipmap.aio_image_default_round)
                        .into(imageView);
                holder.linearLayout.addView(imageView);
            }
        }


        return convertView;
    }

    public float getRawSize(int unit, float value) {
        Resources res = context.getResources();
        return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
    }


    private class ViewHolder {

        private TextView tv_location;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_pinglun;
        // private TextView tv_zan;
        private LinearLayout linearLayout;
        private LinearLayout ll;

    }


}
