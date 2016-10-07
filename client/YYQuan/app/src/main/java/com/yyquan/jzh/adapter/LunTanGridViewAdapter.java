package com.yyquan.jzh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.ShowImageActivity;
import com.yyquan.jzh.util.PhotoSelectedHelper;

import java.io.File;
import java.util.List;

/**
 * Created by jzh on 2015/10/18.
 */
public class LunTanGridViewAdapter extends BaseAdapter {
    List<String> list;
    Context context;
    Intent intent;


    public void setList(List<String> lists){
        list=lists;
        notifyDataSetChanged();

    }
    public List<String> getList(){
        return list;
    }

    public LunTanGridViewAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHoler holer;
        if(convertView==null){
            holer=new ViewHoler();
            convertView= LayoutInflater.from(context).inflate(R.layout.luntan_imageview,null);
            holer.iv_pic= (ImageView) convertView.findViewById(R.id.luntan_state_imageView_child);
            holer.iv_chacha= (ImageView) convertView.findViewById(R.id.luntan_state_imageView_chacha);
            convertView.setTag(holer);
        }else{
            holer= (ViewHoler) convertView.getTag();
        }
        Picasso.with(context).load(new File(list.get(position))).resize(200, 200).centerCrop().into(holer.iv_pic);
        holer.iv_chacha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        holer.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra("path", list.get(position));
                intent.putExtra("type", "luntan");
                context.startActivity(intent);
            }
        });



        return convertView;
    }


    class ViewHoler{
        ImageView iv_pic;
        ImageView iv_chacha;


    }
}
