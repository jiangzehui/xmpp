package com.yyquan.jzh.fragment.friend;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.ChatActivity;
import com.yyquan.jzh.activity.MainActivity;
import com.yyquan.jzh.activity.TransactionFriendActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppFriend;
import com.yyquan.jzh.entity.XmppMessage;
import com.yyquan.jzh.entity.XmppUser;
import com.yyquan.jzh.util.RecyclerViewDividerItemDecoration;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.util.TimeUtil;
import com.yyquan.jzh.util.ToastUtil;
import com.yyquan.jzh.view.BadgeView;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.xmpp.XmppContentProvider;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/1.
 */
public class MessageFragment extends Fragment {
    View view;
    RecyclerView rv;
    List<XmppMessage> list;
    List<XmppMessage> list_add;
    String url_icon = Ip.ip + "/YfriendService/DoGetIcon?name=";
    MyAdapter adapter;
    String user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {

            user = SaveUserUtil.loadAccount(getActivity()).getUser();
            Log.i("message", "user=" + user);
            view = inflater.inflate(R.layout.fragment_message, container, false);
            ButterKnife.bind(this, view);
            rv = (RecyclerView) view.findViewById(R.id.fragment_messgae_recyclerview);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));//设置排列方式
            rv.addItemDecoration(new RecyclerViewDividerItemDecoration(getActivity(), RecyclerViewDividerItemDecoration.VERTICAL_LIST));
            initialData();

        }


        return view;
    }

    public void initialData() {
        list = new ArrayList<>();
        list_add = new ArrayList<>();
        //Cursor cursor = XmppService.resolver.query(XmppContentProvider.CONTENT_MESSAGES_URI, null, "select * from message where too=?", new String[]{SaveUserUtil.loadAccount(getActivity()).getUser()}, null);
        Cursor cursor = XmppService.resolver.query(XmppContentProvider.CONTENT_MESSAGES_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String type = cursor.getString(cursor.getColumnIndex("type"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String to = cursor.getString(cursor.getColumnIndex("too"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            XmppUser user = new XmppUser(username, name);
            String time = cursor.getString(cursor.getColumnIndex("time"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            int result = cursor.getInt(cursor.getColumnIndex("result"));
            String main = cursor.getString(cursor.getColumnIndex("main"));
            XmppMessage xm = new XmppMessage(id, to, type, user, time, content, result, main);

            if (to.equals(this.user)) {
                Log.i("message》》》》》》》》》》》", xm.toString() + "\n" + this.user);
                list.add(xm);
            }

//            }

        }

        if (list.size() < 1) {
            rv.setVisibility(View.GONE);
        } else {

            rv.setVisibility(View.VISIBLE);
            adapter = new MyAdapter();
            initEvent();
            rv.setAdapter(adapter);

        }


    }

    private void initEvent() {
        adapter.setOnItemClickLitener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (list.get(position).getType().equals("chat")) {
                    if (MainActivity.main != null) {
                        if (MainActivity.main.receiver.manager != null) {
                            MainActivity.main.receiver.manager.cancel(0);
                        }
                    }
                    ContentValues values = new ContentValues();
                    values.put("result", 0);
                    XmppService.resolver.update(XmppContentProvider.CONTENT_MESSAGES_URI, values, "id=?", new String[]{list.get(position).getId() + ""});
                    adapter.setData(position);
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    User users = new Gson().fromJson(list.get(position).getUser().getName(), User.class);
                    intent.putExtra("xmpp_friend", new XmppFriend(users));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), TransactionFriendActivity.class);
                    intent.putExtra("xmpp_user", list.get(position));
                    startActivity(intent);
                }


            }


        });


        adapter.setOnLongItemClickLitener(new OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("删除信息");
                dialog.setMessage("是否删除信息？");
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XmppService.resolver.delete(XmppContentProvider.CONTENT_MESSAGES_URI, "id=?", new String[]{list.get(position).getId() + ""});
                        initialData();

                    }
                });
                dialog.setNegativeButton("否", null);
                dialog.show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private OnItemClickListener mOnItemClickLitener;
        private OnLongItemClickListener mOnLongItemClickLitener;
        LayoutInflater inflater;

        void setData(int position) {

            list.get(position).setResult(0);
            adapter.notifyDataSetChanged();
        }

        public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        public void setOnLongItemClickLitener(OnLongItemClickListener mOnLongItemClickLitener) {
            this.mOnLongItemClickLitener = mOnLongItemClickLitener;
        }

        public MyAdapter() {
            inflater = LayoutInflater.from(getActivity());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0 || viewType == 1) {
                return new FriendHolder(inflater.inflate(R.layout.fragment_message_recyclerview_item, parent, false));
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getPosition();
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                    }
                });
            }

            if (mOnLongItemClickLitener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getPosition();
                        mOnLongItemClickLitener.onLongItemClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

            if (holder instanceof FriendHolder) {
                FriendHolder friendHolder = (FriendHolder) holder;
                // String str[] = list.get(position).getUser().getName().split(";");
                User users = new Gson().fromJson(list.get(position).getUser().getName(), User.class);
                friendHolder.tv_title.setText(users.getNickname());
                friendHolder.tv_content.setText(list.get(position).getContent());
                friendHolder.tv_time.setText(list.get(position).getTime());

                switch (list.get(position).getResult()) {
                    case -1:
                    case 0:
                        friendHolder.tv_ts.setVisibility(View.GONE);
                        break;
                    default:
                        friendHolder.tv_ts.setVisibility(View.VISIBLE);
                        friendHolder.tv_ts.setText(list.get(position).getResult() + "");
                        break;
                }
                if (users.getIcon().equals("")) {
                    if (users.getSex().equals("男")) {
                        friendHolder.iv.setImageResource(R.mipmap.me_icon_man);
                    } else {
                        friendHolder.iv.setImageResource(R.mipmap.me_icon_woman);
                    }
                } else {
                    if (users.getIcon().substring(0, 4).equals("http")) {
                        Picasso.with(getActivity()).load(users.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(friendHolder.iv);
                    } else {
                        Picasso.with(getActivity()).load(url_icon + users.getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(friendHolder.iv);
                    }
                }
            }
        }


        @Override
        public int getItemViewType(int position) {
            if (list.get(position).getType().equals("add") || list.get(position).getType().equals("agreed")) {
                return 0;
            } else if (list.get(position).getType().equals("chat")) {
                return 1;
            } else {
                return 0;
            }


        }

        @Override
        public int getItemCount() {
            return list.size();
        }


    }


    class FriendHolder extends RecyclerView.ViewHolder {

        CircleImageView iv;
        TextView tv_title;
        TextView tv_content;
        TextView tv_time;
        BadgeView tv_ts;

        public FriendHolder(View itemView) {
            super(itemView);
            iv = (CircleImageView) itemView.findViewById(R.id.fragment_message_imageview_icon);
            tv_title = (TextView) itemView.findViewById(R.id.fragment_message_textView_title);
            tv_content = (TextView) itemView.findViewById(R.id.fragment_message_textView_content);
            tv_time = (TextView) itemView.findViewById(R.id.fragment_message_textView_time);
            tv_ts = (BadgeView) itemView.findViewById(R.id.tv_tishi);

        }
    }

    private interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private interface OnLongItemClickListener {
        void onLongItemClick(View view, int position);
    }


}
