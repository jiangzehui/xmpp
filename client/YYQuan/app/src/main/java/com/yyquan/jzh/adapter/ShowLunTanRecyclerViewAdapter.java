package com.yyquan.jzh.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.ShowImageActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.News_luntan;
import com.yyquan.jzh.entity.News_pinglun;
import com.yyquan.jzh.view.CircleImageView;

import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2015/12/27.
 */
public class ShowLunTanRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<News_pinglun> news;
    News_luntan news_luntan;
    String user;
    String nickname;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    private static final int TYPE_STATE = 0;
    private static final int TYPE_PINGLUN = 1;
    LayoutInflater minflater;


    public ShowLunTanRecyclerViewAdapter(Context context, ArrayList<News_pinglun> news, News_luntan news_luntan, String user, String nickname) {
        this.context = context;
        this.news = news;
        this.user = user;
        this.news_luntan = news_luntan;
        this.nickname = nickname;
        minflater = LayoutInflater.from(context);


    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_STATE : TYPE_PINGLUN;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_STATE) {
            State_ViewHolder holders = new State_ViewHolder(minflater.inflate(R.layout.show_luntan_recycerlistview_item_top, parent, false));
            return holders;


        } else if (viewType == TYPE_PINGLUN) {
            PingLun_ViewHolder holders = new PingLun_ViewHolder(minflater.inflate(R.layout.listview_pinglun_item, parent, false));
            return holders;
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        int type = getItemViewType(position);
        switch (type) {
            case TYPE_STATE:

                State_ViewHolder sholder = (State_ViewHolder) holder;
                final String[] grid_img = news_luntan.getImage().split(";");
                if (news_luntan.getLocation().equals("")) {
                    sholder.tv_location.setVisibility(View.GONE);
                } else {
                    sholder.tv_location.setVisibility(View.VISIBLE);

                    sholder.tv_location.setText(" " + news_luntan.getLocation());
                }
                if (news_luntan.getUser().getIcon().equals("")) {
                    if (news_luntan.getUser().getSex().equals("男")) {
                        sholder.iv_icon.setImageResource(R.mipmap.me_icon_man);
                    } else {
                        sholder.iv_icon.setImageResource(R.mipmap.me_icon_woman);
                    }
                } else {
                    if (news_luntan.getUser().getIcon().substring(0, 4).equals("http")) {
                        Picasso.with(context).load(news_luntan.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(sholder.iv_icon);
                    } else {
                        Picasso.with(context).load(url_icon + news_luntan.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(sholder.iv_icon);
                    }
                }
                sholder.tv_name.setText(news_luntan.getUser().getNickname());
                sholder.tv_time.setText(news_luntan.getTime());
                sholder.tv_content.setText(news_luntan.getContent());
                if (news_luntan.getImage().equals("")) {

                } else {

                    sholder.gv.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return grid_img.length;
                        }

                        @Override
                        public Object getItem(int position) {
                            return grid_img[position];
                        }

                        @Override
                        public long getItemId(int position) {
                            return position;
                        }

                        @Override
                        public View getView(final int position, View convertView, ViewGroup parent) {
                            ViewHolders holder;
                            if (convertView == null) {
                                holder = new ViewHolders();
                                convertView = LayoutInflater.from(context).inflate(R.layout.layout_imagview, null);
                                holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
                                holder.iv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, ShowImageActivity.class);
                                        intent.putExtra("str[]", grid_img);
                                        intent.putExtra("type", "luntan");
                                        intent.putExtra("number", position);
                                        context.startActivity(intent);
                                    }
                                });
                                convertView.setTag(holder);
                            } else {
                                holder = (ViewHolders) convertView.getTag();
                            }

                            final String urlpath = Ip.ip + "/YfriendService/DoGetLunTan?action=search_image&name=" + grid_img[position];
                            Picasso.with(context)
                                    .load(urlpath)
                                    .resize(200, 200).centerCrop()
                                    .placeholder(R.mipmap.aio_image_default_round)
                                    .error(R.mipmap.aio_image_default_round)
                                    .into(holder.iv);
                            return convertView;
                        }
                    });
                }
                break;
            case TYPE_PINGLUN:
                final PingLun_ViewHolder pholder = (PingLun_ViewHolder) holder;
                pholder.tv_lou.setText("第" + position + "楼");
                String stime = "";
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
                pholder.tv_time.setText(stime);

                pholder.tv_content.setText(news.get(position).getPcontent());
                pholder.tv_zan.setText(news.get(position).getPzan() + " ");
                if (news.get(position).getPlocation().equals("")) {
                    pholder.tv_location.setVisibility(View.GONE);
                } else {
                    pholder.tv_location.setVisibility(View.VISIBLE);
                    pholder.tv_location.setText(news.get(position).getPlocation());
                }
                if (nickname.equals(news.get(position).getUser().getNickname())) {
                    pholder.tv_name.setTextColor(context.getResources().getColor(R.color.title));
                } else {
                    pholder.tv_name.setTextColor(context.getResources().getColor(R.color.pinglun_name));
                }
                pholder.tv_name.setText(news.get(position).getUser().getNickname());
                Drawable nav_up = null;
                if (news.get(position).getIspzan().equals("1")) {
                    nav_up = context.getResources().getDrawable(R.mipmap.pic_btn_liked);
                    pholder.tv_zan.setEnabled(false);

                } else {
                    nav_up = context.getResources().getDrawable(R.mipmap.pic_btn_like);
                    pholder.tv_zan.setEnabled(true);
                }
                nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                pholder.tv_zan.setCompoundDrawables(null, null, nav_up, null);
                pholder.tv_zan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (news.get(position).getIspzan().equals("1")) {

                        } else {
                            pholder.tv_zan.setEnabled(false);
                            Drawable nav_upp = context.getResources().getDrawable(R.mipmap.pic_btn_liked);
                            nav_upp.setBounds(0, 0, nav_upp.getMinimumWidth(), nav_upp.getMinimumHeight());
                            pholder.tv_zan.setCompoundDrawables(null, null, nav_upp, null);
                            pholder.tv_zan.setText(Integer.parseInt((news.get(position).getPzan())) + 1 + " ");
                            update_zan(news.get(position).getPid(), position, pholder.tv_zan);

                        }

                    }
                });
                if (news.get(position).getUser().getIcon().equals("")) {
                    if (news.get(position).getUser().getSex().equals("男")) {
                        pholder.iv_icon.setImageResource(R.mipmap.me_icon_man);
                    } else {
                        pholder.iv_icon.setImageResource(R.mipmap.me_icon_woman);
                    }
                } else {
                    if (news.get(position).getUser().getIcon().substring(0, 4).equals("http")) {
                        Picasso.with(context).load(news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(pholder.iv_icon);
                    } else {
                        Picasso.with(context).load(url_icon + news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(pholder.iv_icon);
                    }

                    // Picasso.with(context).load(url_icon+news.get(position).getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerCrop().into(holder.iv_icon);


                }

                break;
        }


    }


    @Override
    public int getItemCount() {
        return 1 + news.size();
    }

    private void update_zan(int pid, final int position, final TextView tv) {
        RequestParams params = new RequestParams();
        params.put("action", "update_zan");
        params.put("pid", pid);
        params.put("user", user);
        //Toast.makeText(context, pid + "", Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Ip.ip + "/YfriendService/DoGetLunTan", params, new AsyncHttpResponseHandler() {
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


    /**
     * 状态图片holder类
     */
    class ViewHolders {
        ImageView iv;
    }


    /**
     * 状态holder类
     */
    public class State_ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView iv_icon;
        private GridView gv;
        private TextView tv_location;
        private TextView tv_name;
        private TextView tv_time;
        private TextView tv_content;


        public State_ViewHolder(View convertView) {
            super(convertView);
            gv = (GridView) convertView.findViewById(R.id.show_luntan_gridview);
            tv_location = (TextView) convertView.findViewById(R.id.show_luntan_textView_location);
            tv_time = (TextView) convertView.findViewById(R.id.show_luntan_textView_time);
            tv_content = (TextView) convertView.findViewById(R.id.show_luntan_textView_content);
            iv_icon = (CircleImageView) convertView.findViewById(R.id.show_luntan_imageview_icon);
            tv_name = (TextView) convertView.findViewById(R.id.show_luntan_textView_name);

        }
    }


    /**
     * 评论holder类
     */
    public class PingLun_ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_location;
        private TextView tv_time;
        private TextView tv_lou;
        private TextView tv_content;
        private TextView tv_zan;
        private ImageView iv_icon;


        public PingLun_ViewHolder(View convertView) {
            super(convertView);
            iv_icon = (ImageView) convertView.findViewById(R.id.listview_pinglun_item_imageview_icon);
            tv_name = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_name);
            tv_location = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_location);
            tv_time = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_time);
            tv_lou = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_lou);
            tv_content = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_content);
            tv_zan = (TextView) convertView.findViewById(R.id.listview_pinglun_item_textView_zan);


        }
    }


}
