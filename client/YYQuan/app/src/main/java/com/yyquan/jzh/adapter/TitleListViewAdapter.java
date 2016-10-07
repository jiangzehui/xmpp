package com.yyquan.jzh.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_content;

import java.util.ArrayList;


/**
 * Created by jzh on 2015/9/27.
 */
public class TitleListViewAdapter extends BaseAdapter {

    Context context;
    ArrayList<News_content> list;

    public TitleListViewAdapter(Context context, ArrayList<News_content> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<News_content> lists){
        list=lists;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.listview_item_imageview_icon);
            holder.tv_title = (TextView) convertView.findViewById(R.id.listview_item_textView_title);
           // holder.tv_zhaiyao = (TextView) convertView.findViewById(R.id.listview_item_textView_zhaiyao);
            holder.tv_pinglun = (TextView) convertView.findViewById(R.id.listview_item_textView_pinglun);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String[] str=list.get(position).getCimage().split(";");
        holder.tv_title.setText(list.get(position).getCtitle());
      //  holder.tv_zhaiyao.setText(list.get(position).getCzhaiyao());
        holder.tv_pinglun.setText(list.get(position).getCpinglun() + "评 ");
        if(str[0].contains("http")){
            Picasso.with(context).load(str[0]).resize(200,200).centerInside().placeholder(R.mipmap.aio_image_default_round).error(R.mipmap.aio_image_default_round).into(holder.iv_icon);

        }else{
            Picasso.with(context).load(Ip.ip+str[0]).resize(200,200).centerInside().placeholder(R.mipmap.aio_image_default_round).error(R.mipmap.aio_image_default_round).into(holder.iv_icon);

        }


        return convertView;
    }







    private class ViewHolder {
        TextView tv_title;
       // TextView tv_zhaiyao;
        TextView tv_pinglun;
        ImageView iv_icon;
    }
}
