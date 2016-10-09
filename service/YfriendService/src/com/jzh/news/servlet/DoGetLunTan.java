package com.jzh.news.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jzh.news.dao.News_luntanDaoImpl;
import com.jzh.news.dao.UserDaoImpl;
import com.jzh.news.entity.News_luntan;

import com.jzh.news.entity.News_pinglun;
import com.jzh.news.entity.User;
import com.jzh.news.util.Base64Coder;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;

public class DoGetLunTan extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		final News_luntanDaoImpl ndi = new News_luntanDaoImpl();
		String action = request.getParameter("action");
		final JSONObject array = new JSONObject();

		if (action.equals("search")) {
			PrintWriter out = response.getWriter();
			String limit = request.getParameter("limit");
			if (limit == null) {
				limit = "0";
			}
			List<News_luntan> list = new ArrayList<News_luntan>();
			list = ndi.search(limit);

			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "暂无数据");
				array.put("data", "");
			} else {
				array.put("code", "success");
				array.put("msg", "数据请求成功");

				UserDaoImpl udi = new UserDaoImpl();
				JSONArray arrays = new JSONArray();
				List<User> list_user;
				int state_size = ndi.search_total();
				for (int i = 0; i < list.size(); i++) {
					list_user = new ArrayList<User>();
					list_user = udi.Search(list.get(i).getUser());
					JSONObject object = new JSONObject();
					object.put("nickname", list_user.get(i).getNickname());
					object.put("sex", list_user.get(i).getSex());
					object.put("icon", list_user.get(i).getIcon());
					object.put("time", list.get(i).getTime());
					object.put("lid", list.get(i).getLid());
					object.put("user", list_user.get(i).getUser());
					object.put("content", list.get(i).getContent());
					object.put("image", list.get(i).getImage());
					object.put("location", list.get(i).getLocation());
					object.put("state_size", state_size);
					object.put("pinglun_size", ndi.search_totals(list.get(i)
							.getLid()
							+ ""));
					arrays.add(object);

				}
				array.put("data", arrays.toString());
			}
			out.print(array);
			out.flush();
			out.close();

		} else if (action.equals("search_user")) {
			PrintWriter out = response.getWriter();
			String limit = request.getParameter("limit");
			if (limit == null) {
				limit = "0";
			}
			String user = request.getParameter("user");
			List<News_luntan> list = new ArrayList<News_luntan>();
			list = ndi.search_one(user, limit);

			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "暂无数据");
				array.put("data", "");
			} else {
				array.put("code", "success");
				array.put("msg", "数据请求成功");

				UserDaoImpl udi = new UserDaoImpl();
				JSONArray arrays = new JSONArray();
				List<User> list_user;
				int state_size = ndi.search_total(user);
				for (int i = 0; i < list.size(); i++) {
					list_user = new ArrayList<User>();
					list_user = udi.Search(list.get(i).getUser());
					JSONObject object = new JSONObject();
					object.put("nickname", list_user.get(i).getNickname());
					object.put("sex", list_user.get(i).getSex());
					object.put("icon", list_user.get(i).getIcon());
					object.put("time", list.get(i).getTime());
					object.put("lid", list.get(i).getLid());
					object.put("user", list_user.get(i).getUser());
					object.put("content", list.get(i).getContent());
					object.put("image", list.get(i).getImage());
					object.put("location", list.get(i).getLocation());
					object.put("state_size", state_size);
					object.put("pinglun_size", ndi.search_totals(list.get(i)
							.getLid()
							+ ""));
					arrays.add(object);

				}
				array.put("data", arrays.toString());

			}

			out.print(array);
			out.flush();
			out.close();

		} else if (action.equals("save")) {
			PrintWriter out = response.getWriter();
			String user = request.getParameter("user");
			String time = request.getParameter("time");
			String content = request.getParameter("content");
			String location = request.getParameter("location");
			String size = request.getParameter("image_size");
			News_luntan news = new News_luntan();
			news.setUser(user);
			news.setTime(time);
			news.setContent(content);
			news.setLocation(location);
			if (!size.equals("0")) {
				StringBuilder sb = new StringBuilder();
				int sizes = Integer.parseInt(size);
				for (int j = 0; j < sizes; j++) {

					if (sizes - 1 == j) {
						sb.append(request.getParameter("filename" + j));
					} else {
						sb.append(request.getParameter("filename" + j) + ";");
					}
					byte[] b = Base64Coder.decodeLines(request
							.getParameter("file" + j));
					String filepath = "c:/yyquan_luntan/";
					File file = new File(filepath);
					if (!file.exists()) {
						file.mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file.getPath()
							+ "/" + request.getParameter("filename" + j));

					fos.write(b);
					fos.flush();
					fos.close();
				}
				news.setImage(sb.toString());
			} else {
				news.setImage("");
			}

			if (ndi.save(news)) {
				array.put("code", "success");
			} else {
				array.put("code", "failure");
			}
			out.print(array);

			out.flush();
			out.close();
		} else if (action.equals("search_image")) {
			response.setContentType("image/gif");
			String name = request.getParameter("name");
			String imagePath = "c:/yyquan_luntan/" + name;

			FileInputStream fis = new FileInputStream(imagePath);

			int size = fis.available(); // 得到文件大小

			byte data[] = new byte[size];

			fis.read(data); // 读数据

			fis.close();

			OutputStream os = response.getOutputStream();

			os.write(data);
			os.flush();
			os.close();
		} else if (action.equals("save_pinglun")) {

			PrintWriter out = response.getWriter();

			String user = request.getParameter("user");
			String time = request.getParameter("ptime");
			String content = request.getParameter("pcontent");
			String location = request.getParameter("plocation");
			// String author = request.getParameter("author");
			String id = request.getParameter("plid");
			System.out.println(user + "\n" + id);
			int plid = Integer.parseInt(id);
			News_pinglun news = new News_pinglun(plid, user, location, time,
					content, "0");
			if (ndi.save_pinglun(news)) {
				array.put("code", "success");
				array.put("msg", "评论成功");

			} else {
				array.put("code", "failure");
				array.put("msg", "评论失败，请稍后重试");

			}
			out.print(array);
			out.flush();
			out.close();

			// UserDaoImpl udi = new UserDaoImpl();
			// News_luntan nl = new News_luntan();
			// User users = new User();
			// nl = ndi.search_one(plid);
			// if (user.equals(nl.getUser())) {
			// return;
			// }
			// users = udi.Search_one(nl.getUser());
			// JSONObject object = new JSONObject();
			// Map<String, Object> custom = new HashMap<String, Object>();
			// object.put("user", nl.getUser());
			// object.put("lid", nl.getLid());
			// object.put("content", nl.getContent());
			// object.put("image", nl.getImage());
			// object.put("time", nl.getTime());
			// object.put("location", nl.getLocation());
			// object.put("nickname", users.getNickname());
			// object.put("icon", users.getIcon());
			// object.put("sex", users.getSex());
			// custom.put("data", object.toString());
			//
			// XingeApp xinge = new XingeApp(2100152165,
			// "660ceb27bb463b5e544b1f847958f5bf");
			// ClickAction click = new ClickAction();
			// click.setActionType(ClickAction.TYPE_ACTIVITY);
			// click.setActivity("com.yyquan.jzh.activity.ShowLuntanActivity");
			// Style style = new Style(0, 1, 0, 1, -1, 1, 0, 1);
			// Message message = new Message();
			// message.setStyle(style);
			// message.setAction(click);
			// message.setExpireTime(86400);
			// message.setTitle("消息");
			// message.setContent("您有新的回复");
			// message.setCustom(custom);
			// message.setType(Message.TYPE_NOTIFICATION);
			// xinge.pushSingleAccount(0, nl.getUser(), message);

		} else if (action.equals("search_pinglun")) {
			PrintWriter out = response.getWriter();
			String user = request.getParameter("user");
			String plid = request.getParameter("plid");
			String limit = request.getParameter("limit");

			List<News_pinglun> list = new ArrayList<News_pinglun>();
			list = ndi.search_pinglun(plid, limit);
			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "暂无评论");
				array.put("data", "");
			} else {

				array.put("code", "success");
				array.put("msg", "有评论");
				UserDaoImpl udi = new UserDaoImpl();
				JSONArray arrays = new JSONArray();
				List<User> list_user;
				for (int i = 0; i < list.size(); i++) {
					list_user = new ArrayList<User>();
					list_user = udi.Search(list.get(i).getUser());
					String pid = list.get(i).getPid() + "";
					String iszan = ndi.search_iszan(pid, user);
					JSONObject object = new JSONObject();
					object.put("nickname", list_user.get(i).getNickname());
					object.put("size", ndi.search_totals(plid + ""));
					object.put("sex", list_user.get(i).getSex());
					object.put("icon", list_user.get(i).getIcon());

					object.put("pdata", list.get(i));
					object.put("ispzan", iszan);// 0是没赞 1是赞
					arrays.add(object);

				}
				array.put("data", arrays.toString());

			}

			out.print(array);

			out.flush();
			out.close();

		} else if (action.equals("update_zan")) {

			String pid = request.getParameter("pid");
			int ppid = Integer.parseInt(pid);
			String user = request.getParameter("user");
			ndi.save(ppid, user);

			String zans = ndi.search_zan(pid);

			int num = Integer.parseInt(zans) + 1;
			if (ndi.update(pid, num + "")) {
				array.put("code", "success");

			} else {
				array.put("code", "failure");
			}

		}

	}
}
