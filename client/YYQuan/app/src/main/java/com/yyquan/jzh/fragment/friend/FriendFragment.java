package com.yyquan.jzh.fragment.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.ChatActivity;
import com.yyquan.jzh.activity.MainActivity;
import com.yyquan.jzh.activity.ShowMessageActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppFriend;
import com.yyquan.jzh.entity.XmppUser;
import com.yyquan.jzh.util.SLog;
import com.yyquan.jzh.util.ToastUtil;
import com.yyquan.jzh.view.CircleImageView;
import com.yyquan.jzh.view.DialogView;
import com.yyquan.jzh.xmpp.XmppService;
import com.yyquan.jzh.xmpp.XmppTool;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by Administrator on 2016/1/1.
 */
public class FriendFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnGroupClickListener {

    View view;
    SwipeRefreshLayout freshLayout;
    ExpandableListView expandableListView;
    MyFriendExpadableAdapter adapter;
    private List<RosterGroup> groups;
    private Map<RosterGroup, List<XmppFriend>> childs;
    Map<String, Integer> map_is_open;
    List<String> list_is_open;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            DialogView.Initial(getActivity(), "正在加载好友列表......");
            view = inflater.inflate(R.layout.fragment_friend, container, false);
            map_is_open = new HashMap<>();
            list_is_open = new ArrayList<>();
            freshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_friend_swipe);
            freshLayout.setOnRefreshListener(this);
            freshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_dark);
            expandableListView = (ExpandableListView) view.findViewById(R.id.fragment_friend_expandableListView);
            expandableListView.setOnGroupClickListener(this);
            DialogView.show();
            getData();

        }


        return view;
    }


    public void getData() {
        if (XmppTool.getInstance().isConnection() == false) {
            ToastUtil.show(getActivity(), "已断开,正在重连中....");
            return;
        }
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

                groups = XmppTool.getInstance().getGroups();
                childs = new HashMap<RosterGroup, List<XmppFriend>>();
                HashMap<String, Object> map = XmppService.map;
                for (int i = 0; i < groups.size(); i++) {
                    List<RosterEntry> child = XmppTool.getInstance().getEntrysByGroup(groups.get(i).getName());
                    List<XmppFriend> lists = new ArrayList<XmppFriend>();
                    list_is_open.add(groups.get(i).getName());
                    for (int j = 0; j < child.size(); j++) {

                        List<XmppUser> list = XmppTool.getInstance().searchUsers(child.get(j).getUser().split("@")[0]);
//                        String str[] = list.get(0).getName().split(";");
                        User users = new Gson().fromJson(list.get(0).getName(), User.class);
                        int status = 6;
                        if (map.containsKey(users.getUser())) {
                            status = (int) map.get(users.getUser());
                        }
                        XmppFriend xf = new XmppFriend(users, status);
                        lists.add(xf);


                    }

                    Collections.sort(lists, COMPARATOR);
                    childs.put(groups.get(i), lists);

                }

                handler.sendEmptyMessage(1);

            }

        }.start();

    }

    @Override
    public void onRefresh() {
        getData();
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message m) {

            if (m.what == 1) {

                if (groups.size() == 0) {
                    freshLayout.setVisibility(View.GONE);
                } else {
                    freshLayout.setVisibility(View.VISIBLE);
                    setAdapter();
                }
                DialogView.dismiss();
            }
        }
    };

    protected void setAdapter() {
        adapter = new MyFriendExpadableAdapter();
        expandableListView.setAdapter(adapter);
        int groupCount = expandableListView.getCount();
        if (groupCount > 0) {
            for (int i = 0; i < groupCount; i++) {

                if (map_is_open.containsKey(list_is_open.get(i))) {
                    if (map_is_open.get(list_is_open.get(i)) == 1) {
                        if (expandableListView != null) {
                            expandableListView.expandGroup(i);
                        }

                    }
                }

            }
        }

        adapter.notifyDataSetChanged();
        freshLayout.setRefreshing(false);
    }


    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (map_is_open.containsKey(list_is_open.get(groupPosition))) {

            if (expandableListView.isGroupExpanded(groupPosition)) {
                map_is_open.put(list_is_open.get(groupPosition), 0);
            } else {
                map_is_open.put(list_is_open.get(groupPosition), 1);
            }

        } else {
            map_is_open.put(list_is_open.get(groupPosition), 1);
        }
        return false;
    }


    class MyFriendExpadableAdapter extends BaseExpandableListAdapter {


        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childs.get(groups.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childs.get(groups.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.friend_group_layout, parent, false);
                holder.tv = (TextView) convertView.findViewById(R.id.textView);
                holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);

            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            if (isExpanded) {
                holder.iv.setImageResource(R.mipmap.friend_group_point_xia);
            } else {
                holder.iv.setImageResource(R.mipmap.friend_group_point);
            }
            RosterGroup group = (RosterGroup) getGroup(groupPosition);
            holder.tv.setText(group.getName() + "[" + childs.get(groups.get(groupPosition)).size() + "]");


            return convertView;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;

            final XmppFriend xf = (XmppFriend) getChild(groupPosition, childPosition);
            // RosterEntry childEntry = (RosterEntry) getChild(groupPosition, childPosition);
            if (null == convertView) {
                holder = new ChildHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.friend_child_layout, null);
                holder.cv_icon = (CircleImageView) convertView.findViewById(R.id.friend_child_imageview_icon);
                holder.iv_statu = (ImageView) convertView.findViewById(R.id.friend_child_imageview_status);
                holder.tv_name = (TextView) convertView.findViewById(R.id.friend_child_textView_name);
                holder.tv_statu = (TextView) convertView.findViewById(R.id.friend_child_textView_status);
//                convertView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                        dialog.setTitle("删除好友");
//                        dialog.setMessage("是否删除好友？");
//                        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (XmppTool.getInstance().removeUser(xf.getUser().getUser())) {
//                                    ToastUtil.show(getActivity(), "删除成功");
//                                    getData();
//                                } else {
//                                    ToastUtil.show(getActivity(), "删除失败，请重试.");
//                                }
//                            }
//                        });
//                        dialog.setNegativeButton("否", null);
//                        dialog.show();
//                        return true;
//                    }
//                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (XmppTool.getInstance().isConnection() == false) {
                            ToastUtil.show(getActivity(), "已断开,正在重连中....");
                            return;
                        }
                        Intent intent = new Intent(getActivity(), ShowMessageActivity.class);
                        intent.putExtra("user", xf.getUser().getUser());
                        startActivity(intent);
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }


            holder.tv_name.setText(xf.getUser().getNickname());
            switch (xf.getStatus()) {
                //0.在线 1.Q我吧 2.忙碌 3.勿扰 4.离开 5.隐身 6.离线
                case 5:
                case 6:
                    holder.iv_statu.setVisibility(View.INVISIBLE);
                    holder.tv_statu.setTextColor(getResources().getColor(R.color.tab_text_bg));
                    holder.tv_statu.setText("离线");
                    break;
                case 0:
                    holder.iv_statu.setVisibility(View.VISIBLE);
                    holder.iv_statu.setImageResource(R.mipmap.status_online);
                    holder.tv_statu.setTextColor(Color.GREEN);
                    holder.tv_statu.setText("在线");
                    break;
                case 2:
                    holder.iv_statu.setVisibility(View.VISIBLE);
                    holder.iv_statu.setImageResource(R.mipmap.status_busy);
                    holder.tv_statu.setTextColor(Color.RED);
                    holder.tv_statu.setText("忙碌");
                    break;
                case 3:
                    holder.iv_statu.setVisibility(View.VISIBLE);
                    holder.iv_statu.setImageResource(R.mipmap.status_shield);
                    holder.tv_statu.setTextColor(Color.RED);
                    holder.tv_statu.setText("勿扰");
                    break;
                case 4:
                    holder.iv_statu.setVisibility(View.VISIBLE);
                    holder.iv_statu.setImageResource(R.mipmap.status_leave);
                    holder.tv_statu.setTextColor(getResources().getColor(R.color.tab_text_bg));
                    holder.tv_statu.setText("离开");
                    break;
                case 1:
                    holder.iv_statu.setVisibility(View.VISIBLE);
                    holder.iv_statu.setImageResource(R.mipmap.status_qme);
                    holder.tv_statu.setTextColor(Color.GREEN);
                    holder.tv_statu.setText("Q我吧");
                    break;
            }
            if (xf.getUser().getIcon().equals("")) {
                if (xf.getUser().getSex().equals("男")) {
                    holder.cv_icon.setImageResource(R.mipmap.me_icon_man);
                } else {
                    holder.cv_icon.setImageResource(R.mipmap.me_icon_woman);
                }
            } else {
                if (xf.getUser().getIcon().substring(0, 4).equals("http")) {
                    Picasso.with(getActivity()).load(xf.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(holder.cv_icon);
                } else {
                    Picasso.with(getActivity()).load(Ip.ip_icon + xf.getUser().getIcon()).resize(200, 200).placeholder(R.mipmap.qq_addfriend_search_friend).error(R.mipmap.qq_addfriend_search_friend).centerInside().into(holder.cv_icon);
                }
            }


            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    class GroupHolder {
        TextView tv;
        ImageView iv;
    }


    class ChildHolder {
        CircleImageView cv_icon;
        ImageView iv_statu;
        TextView tv_name;
        TextView tv_statu;
    }

    private Comparator<XmppFriend> COMPARATOR = new Comparator<XmppFriend>() {
        public int compare(XmppFriend o1, XmppFriend o2) {
            return o1.compareTo(o2);// 运用User类的compareTo方法比较两个对象
        }
    };

}



