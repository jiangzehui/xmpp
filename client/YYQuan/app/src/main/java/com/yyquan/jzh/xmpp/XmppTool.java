package com.yyquan.jzh.xmpp;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.entity.XmppUser;
import com.yyquan.jzh.util.SLog;
import com.yyquan.jzh.util.SaveUserUtil;
import com.yyquan.jzh.util.SharedPreferencesUtil;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.bytestreams.ibb.provider.CloseIQProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.bytestreams.ibb.provider.OpenIQProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmppTool {
    private String tag = "XmppTool";
    private static XmppTool instance;
    public static final String HOST = "123.207.145.194";
    // public static final String HOST = "192.168.1.188";
    public static final int PORT = 5222;
    private static XMPPConnection con;
    Context context;


    public static XmppTool getInstance() {

        if (null == instance)
            instance = new XmppTool();
        return instance;
    }

    private XmppTool() {
        configure(ProviderManager.getInstance());
        ConnectionConfiguration connConfig = new ConnectionConfiguration(HOST, PORT);
        connConfig.setSASLAuthenticationEnabled(false);
        connConfig.setReconnectionAllowed(true);
        connConfig.setSendPresence(false);
        // connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
        // connConfig.setSendPresence(true);
        con = new XMPPConnection(connConfig);
        con.DEBUG_ENABLED = true;

        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        try {
            if (con.isConnected()) {// 首先判断是否还连接着服务器，需要先断开
                try {
                    con.disconnect();
                } catch (Exception e) {
                    SLog.i(tag, "conn.disconnect() failed: " + e);
                }
            }
            SmackConfiguration.setPacketReplyTimeout(30000);// 设置超时时间
            SmackConfiguration.setKeepAliveInterval(-1);
            SmackConfiguration.setDefaultPingInterval(0);
            con.connect();

            con.addConnectionListener(new ConnectionListener() {

                @Override
                public void reconnectionSuccessful() {
                    // TODO Auto-generated method stub
                    SLog.i(tag, "重连成功");
                }

                @Override
                public void reconnectionFailed(Exception arg0) {
                    // TODO Auto-generated method stub
                    SLog.i(tag, "重连失败");
//                    User user = SaveUserUtil.loadAccount(context);
//                    login(user.getUser(), user.getPassword());


                }

                @Override
                public void reconnectingIn(int arg0) {
                    // TODO Auto-generated method stub
                    SLog.i(tag, "重连中");
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    // TODO Auto-generated method stub
                    SLog.i(tag, "连接出错");
                    if (e.getMessage().contains("conflict")) {
                        SLog.i(tag, "被挤掉了");
                        disConnectServer();

                    }
//                    User user = SaveUserUtil.loadAccount(context);
//                    login(user.getUser(), user.getPassword());
                }

                @Override
                public void connectionClosed() {
                    // TODO Auto-generated method stub
                    SLog.i(tag, "连接关闭");
                }
            });

        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
    }

    public XMPPConnection getCon() {
        return con;
    }

    /**
     * 是否与服务器连接上
     *
     * @return
     */
    public boolean isConnection() {
        if (con != null) {
            return (con.isConnected() && con.isAuthenticated());
        }
        return false;
    }

    /**
     * 登录
     *
     * @param name
     * @param pwd
     * @return
     */
    public boolean login(String name, String pwd, Context context) {

        try {
            this.context = context;
            // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            con.login(name.toLowerCase(), pwd);
            // getMessage();//获取离线消息
            int status = SharedPreferencesUtil.getInt(context, "status", name + "status");
            setPresence(status);//设置状态,默认为在线状态
            return true;
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return false;
    }

    public boolean login(String name, String pwd) {

        try {

            // SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            con.login(name.toLowerCase(), pwd);
            // getMessage();//获取离线消息
            int status = SharedPreferencesUtil.getInt(context, "status", name + "status");
            setPresence(status);//设置状态,默认为在线状态
            return true;
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return false;
    }


    /**
     * 修改密码
     *
     * @param pwd
     * @return
     */
    public boolean changePassword(String pwd) {
        try {
            con.getAccountManager().changePassword(pwd);
            return true;
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return false;
    }


    /**
     * 设置状态
     *
     * @param state
     */
    public void setPresence(int state) {
        Presence presence;
        switch (state) {
            //0.在线 1.Q我吧 2.忙碌 3.勿扰 4.离开 5.隐身 6.离线
            case 0:
                presence = new Presence(Presence.Type.available);
                con.sendPacket(presence);
                SLog.e(tag, "设置在线");
                break;
            case 1:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.chat);
                con.sendPacket(presence);
                SLog.e(tag, "Q我吧");
                SLog.e(tag, presence.toXML());
                break;
            case 2:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.dnd);
                con.sendPacket(presence);
                SLog.e(tag, "忙碌");
                SLog.e(tag, presence.toXML());
                break;
            case 3:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.xa);
                con.sendPacket(presence);
                SLog.e(tag, "勿扰");
                SLog.e(tag, presence.toXML());
                break;
            case 4:
                presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.away);
                con.sendPacket(presence);
                SLog.e(tag, "离开");
                SLog.e(tag, presence.toXML());
                break;
            case 5:
                Roster roster = con.getRoster();
                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entity : entries) {
                    presence = new Presence(Presence.Type.unavailable);
                    presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                    presence.setFrom(con.getUser());
                    presence.setTo(entity.getUser());
                    con.sendPacket(presence);
                    SLog.e(tag, presence.toXML());
                }
                SLog.e(tag, "告知其他用户-隐身");

                break;
//            case 6:
//                presence = new Presence(Presence.Type.unavailable);
//                con.sendPacket(presence);
//                SLog.e(tag, "离线");
//                SLog.e(tag, presence.toXML());
//                break;
//            default:
//                break;
        }
    }

    public void setPresence(ImageView iv, ImageView iv_me, Context context, String name) {

        int status = SharedPreferencesUtil.getInt(context, "status", name + "status");
        switch (status) {
            //0.在线 1.Q我吧 2.忙碌 3.勿扰 4.离开 5.隐身 6.离线
            case 0:
                iv.setImageResource(R.mipmap.status_online);
                iv_me.setImageResource(R.mipmap.status_online);
                break;
            case 1:
                iv.setImageResource(R.mipmap.status_qme);
                iv_me.setImageResource(R.mipmap.status_qme);
                break;
            case 2:
                iv.setImageResource(R.mipmap.status_busy);
                iv_me.setImageResource(R.mipmap.status_busy);
                break;
            case 3:
                iv.setImageResource(R.mipmap.status_shield);
                iv_me.setImageResource(R.mipmap.status_shield);
                break;
            case 4:
                iv.setImageResource(R.mipmap.status_leave);
                iv_me.setImageResource(R.mipmap.status_leave);
                break;
            case 5:
                iv.setImageResource(R.mipmap.status_invisible);
                iv_me.setImageResource(R.mipmap.status_invisible);
                break;

        }
    }

    /**
     * 获取离线消息
     */
    private void getMessage() {
        OfflineMessageManager offlineManager = new OfflineMessageManager(getCon());
        try {
            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager
                    .getMessages();
            Log.i("service", offlineManager.supportsFlexibleRetrieval() + "");
            Log.i("service", "离线消息数量: " + offlineManager.getMessageCount());
            Map<String, ArrayList<Message>> offlineMsgs = new HashMap<String, ArrayList<Message>>();
            while (it.hasNext()) {
                org.jivesoftware.smack.packet.Message message = it.next();
                Log.i("service", "收到离线消息, Received from 【" + message.getFrom()
                        + "】 message: " + message.getBody());
                String fromUser = message.getFrom().split("/")[0];
                if (offlineMsgs.containsKey(fromUser)) {
                    offlineMsgs.get(fromUser).add(message);
                } else {
                    ArrayList<Message> temp = new ArrayList<Message>();
                    temp.add(message);
                    offlineMsgs.put(fromUser, temp);
                }
            }
//在这里进行处理离线消息集合......
            Set<String> keys = offlineMsgs.keySet();
            Iterator<String> offIt = keys.iterator();
            while (offIt.hasNext()) {
                String key = offIt.next();
                ArrayList<Message> ms = offlineMsgs.get(key);

                for (int i = 0; i < ms.size(); i++) {
                    Log.i("serviceeeeeeeeeeeee", "收到离线消息, Received from 【" + ms.get(i).getFrom()
                            + "】 message: " + ms.get(i).getBody());
                }
            }
            offlineManager.deleteMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 查找用户
     *
     * @param
     * @param userName
     * @return
     */
    public List<XmppUser> searchUsers(String userName) {
        List<XmppUser> list = new ArrayList<XmppUser>();
        UserSearchManager userSearchManager = new UserSearchManager(con);
        try {
            Form searchForm = userSearchManager.getSearchForm("search."
                    + con.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("Name", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = userSearchManager.getSearchResults(answerForm,
                    "search." + con.getServiceName());
            Iterator<ReportedData.Row> rows = data.getRows();
            while (rows.hasNext()) {
                XmppUser user = new XmppUser(null, null);
                ReportedData.Row row = rows.next();
                user.setUserName(row.getValues("Username").next().toString());
                user.setName(row.getValues("Name").next().toString());
                list.add(user);
            }
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return list;
    }

    /**
     * 添加好友
     *
     * @param
     * @param userName
     * @param name
     * @param groupName 是否有分组
     * @return
     */
    public boolean addUser(String userName, String name, String groupName) {
        Roster roster = con.getRoster();
        try {
            roster.createEntry(userName, name, null == groupName ? null
                    : new String[]{groupName});
            return true;
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return false;
    }

    /**
     * 删除好友
     *
     * @param userName
     * @return
     */
    public boolean removeUser(String userName) {
        Roster roster = con.getRoster();
        try {
            RosterEntry entry = roster.getEntry(userName);
            if (null != entry) {
                roster.removeEntry(entry);
            }
            return true;
        } catch (XMPPException e) {
            SLog.e(tag, Log.getStackTraceString(e));
        }
        return false;
    }


    /**
     * 添加到分组
     *
     * @param
     * @param userName
     * @param groupName
     */
    public void addUserToGroup(String userName, String groupName) {
        Roster roster = con.getRoster();
        RosterGroup group = roster.getGroup(groupName);
        if (null == group) {
            group = roster.createGroup(groupName);
        }
        RosterEntry entry = roster.getEntry(userName);
        if (entry != null) {
            try {
                group.addEntry(entry);
            } catch (XMPPException e) {
                SLog.e(tag, Log.getStackTraceString(e));
            }
        }

    }

    /**
     * 获取所有分组
     *
     * @param
     * @return
     */
    public List<RosterGroup> getGroups() {
        Roster roster = getCon().getRoster();
        List<RosterGroup> list = new ArrayList<RosterGroup>();
        list.addAll(roster.getGroups());
        return list;
    }

    /**
     * 获取某一个分组的成员
     *
     * @param
     * @param groupName
     * @return
     */
    public List<RosterEntry> getEntrysByGroup(String groupName) {

        Roster roster = getCon().getRoster();
        List<RosterEntry> list = new ArrayList<RosterEntry>();
        RosterGroup group = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntiry = group.getEntries();
        Iterator<RosterEntry> iter = rosterEntiry.iterator();
        while (iter.hasNext()) {
            RosterEntry entry = iter.next();
            SLog.i("xmpptool", entry.getUser() + "\t" + entry.getName() + entry.getType().toString());
            if (entry.getType().toString().equals("both")) {
                list.add(entry);
            }

        }
        return list;

    }

    /**
     * 判断是否是好友
     *
     * @param
     * @param user
     * @return
     */
    public boolean isFriendly(String user) {


        Roster roster = getCon().getRoster();
        List<RosterEntry> list = new ArrayList<RosterEntry>();
        list.addAll(roster.getEntries());
        for (int i = 0; i < list.size(); i++) {
            Log.i("xmppttttttttt", list.get(i).getUser().toUpperCase() + "\t" + user);
            if (list.get(i).getUser().contains(user.toLowerCase())) {
                if (list.get(i).getType().toString().equals("both")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;

    }

//    /**
//     * 注册
//     *
//     * @return 0 服务端无响应 1成功 2已存在 3 失败
//     */
//    public int regist(User user) {
//
//        Registration reg = new Registration();
//        reg.setType(IQ.Type.SET);
//        reg.setTo(con.getServiceName());
//        reg.setUsername(user.getUser());
//        reg.setPassword(user.getPassword());
//        reg.addAttribute("Android", "createUser");
//        reg.addAttribute("name", user.getNickname());
//        PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()));
//        PacketCollector col = con.createPacketCollector(filter);
//        con.sendPacket(reg);
//        IQ result = (IQ) col.nextResult(SmackConfiguration.getPacketReplyTimeout());
//        col.cancel();
//        if (null == result) {
//            SLog.e(tag, "no response from server");
//            return 0;
//        } else if (result.getType() == IQ.Type.RESULT) {
//            SLog.e(tag, result.toString());
//
//            return 1;
//        } else if (result.getType() == IQ.Type.ERROR) {
//            SLog.e(tag, result.toString());
//            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
//                return 2;
//            } else {
//                return 3;
//            }
//        }
//        return 3;
//    }


//    /**
//     * 添加分组
//     *
//     * @param
//     * @param groupName
//     * @return
//     */
//    public boolean addGroup(String groupName) {
//        try {
//            Roster roster = getCon().getRoster();
//            roster.createGroup(groupName);
//            return true;
//        } catch (Exception e) {
//            SLog.e(tag, Log.getStackTraceString(e));
//        }
//        return false;
//    }


    /**
     * 断开连接
     */
    public static void disConnectServer() {
        if (null != con && con.isConnected()) {

            new Thread() {
                public void run() {

                    con.disconnect();
                }
            }.start();
        }

    }


    public void configure(ProviderManager pm) {

        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Private Data Storage
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());

        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());

        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates",
                new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());

        // Service Discovery # Items
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        // Service Discovery # Info
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());

        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());

        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }

        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());

        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());

        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());

        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup",
                new SharedGroupsInfo.Provider());

        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());

        // FileTransfer
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        pm.addIQProvider("open", "http://jabber.org/protocol/ibb", new OpenIQProvider());
        pm.addIQProvider("close", "http://jabber.org/protocol/ibb", new CloseIQProvider());
        pm.addExtensionProvider("data", "http://jabber.org/protocol/ibb", new DataPacketProvider());

        // Privacy
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands",
                new AdHocCommandDataProvider.SessionExpiredError());
    }

}
