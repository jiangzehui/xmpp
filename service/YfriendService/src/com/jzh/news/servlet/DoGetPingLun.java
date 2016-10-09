package com.jzh.news.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jzh.news.dao.News_contentDaoImpl;
import com.jzh.news.dao.News_pinglunDaoImpl;
import com.jzh.news.dao.UserDaoImpl;
import com.jzh.news.entity.News_content;
import com.jzh.news.entity.News_pinglun;
import com.jzh.news.entity.User;

public class DoGetPingLun extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");

		String action = request.getParameter("action");
		PrintWriter out = response.getWriter();
		JSONObject array = new JSONObject();
		News_pinglunDaoImpl ndi = new News_pinglunDaoImpl();

		if (action.equals("search")) {
			String pcid = request.getParameter("pcid");
			String user = request.getParameter("user");
			String limit = request.getParameter("limit");
			List<News_pinglun> list = new ArrayList<News_pinglun>();
			list = ndi.search(pcid, limit);
			int size = ndi.search_total(pcid);
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
					JSONObject object = new JSONObject();
					String pid = list.get(i).getPid() + "";
					String iszan = ndi.search_iszan(pid, user);
					object.put("size", size);
					object.put("ispzan", iszan);// 0是没赞 1是赞
					// System.out.println(iszan);
					if (list.get(i).getUser().contains("http")) {
						String[] str = list.get(i).getUser().split(";");
						object.put("nickname", str[0]);
						object.put("sex", "");
						object.put("icon", str[1]);
						object.put("pdata", list.get(i));

					} else {
						list_user = udi.Search(list.get(i).getUser());

						object.put("nickname", list_user.get(i).getNickname());
						object.put("sex", list_user.get(i).getSex());
						object.put("icon", list_user.get(i).getIcon());
						object.put("pdata", list.get(i));

					}

					arrays.add(object);

				}
				array.put("data", arrays.toString());

			}
		} else if (action.equals("save")) {
			String pcid = request.getParameter("pcid");
			int id = Integer.parseInt(pcid);
			String user = request.getParameter("user");
			String plocation = request.getParameter("plocation");
			String ptime = request.getParameter("ptime");
			String pcontent = request.getParameter("pcontent");
			String zan = "0";
			News_pinglun news = new News_pinglun(id, user, plocation, ptime,
					pcontent, zan);
			if (ndi.save(news)) {
				array.put("code", "success");
				array.put("msg", "评论成功");

			} else {
				array.put("code", "failure");
				array.put("msg", "评论失败，请稍后重试");

			}
		} else if (action.equals("update")) {
			String pid = request.getParameter("pid");
			int ppid = Integer.parseInt(pid);
			String user = request.getParameter("user");
			ndi.save(ppid, user);
			System.out.println(pid + "\n" + user);
			String zans = ndi.search_zan(pid).get(0).getPzan();
			int num = Integer.parseInt(zans) + 1;
			if (ndi.update(pid, num + "")) {
				array.put("code", "success");

			} else {
				array.put("code", "failure");
			}

		}

		out.print(array);
		out.flush();
		out.close();
	}

}
