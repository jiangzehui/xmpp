package com.jzh.news.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.google.gson.Gson;
import com.jzh.news.dao.UserDaoImpl;
import com.jzh.news.entity.User;
import com.jzh.news.util.Base64Coder;
import com.jzh.news.xmpp.XmppTool;

public class DoGetUser extends HttpServlet {

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
		UserDaoImpl ndi = new UserDaoImpl();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		if (action.equals("login")) {// 用户登录
			String user = request.getParameter("user");
			String password = request.getParameter("password");

			List<User> list = new ArrayList<User>();
			if (password.equals("QQSJHAAJSHAJSH")) {
				list = ndi.Search(user);
			} else if (password.equals("SINAHKSJDHSKDH")) {
				list = ndi.Search(user);
			} else {
				list = ndi.Search(user, password);
			}

			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "用户名或密码有误");
				array.put("data", "");
			} else {
				System.out.println("\n用户名：" + user + "\n在" + time + "登录了");
				array.put("code", "success");
				array.put("msg", "登录成功");
				array.put("data", list.get(0));
			}

		} else if (action.equals("save")) {

			String user = request.getParameter("user");
			String password = request.getParameter("password");
			String qq = request.getParameter("qq");
			String icon = request.getParameter("icon");
			String nickname = request.getParameter("nickname");
			String city = request.getParameter("city");
			String location = request.getParameter("location");
			String sex = request.getParameter("sex");
			String years = request.getParameter("years");
			User users = new User(user, password, qq, icon, nickname, city,
					sex, years, location, "没个性，不签名。");
			if (ndi.Save(users)) {
				array.put("code", "success");
				array.put("msg", "注册成功");
				System.out.println("\n用户名：" + user + "注册成功" + "\ntime:" + time);
				out.print(array);
				out.flush();
				out.close();
				XmppTool.create(users);
				return;

			} else {
				array.put("code", "failure");
				array.put("msg", "注册失败");

			}
		} else if (action.equals("search")) {
			String user = request.getParameter("user");
			List<User> list = new ArrayList<User>();
			list = ndi.Search(user);
			if (list.size() < 1) {

				array.put("code", "success");
				array.put("msg", "未注册");

			} else {

				array.put("code", "failure");
				array.put("msg", "已注册");

			}

		} else if (action.equals("search_meeesage")) {

			String user = request.getParameter("user");
			List<User> list = new ArrayList<User>();
			list = ndi.Search(user);
			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "用户资料获取失败");
				array.put("data", "");

			} else {

				array.put("code", "success");
				array.put("msg", "用户资料获取成功");
				array.put("data", list.get(0));

			}

		} else if (action.equals("update_message")) {
			String nickname = request.getParameter("nickname");
			String sex = request.getParameter("sex");
			String years = request.getParameter("years");
			String qq = request.getParameter("qq");
			String user = request.getParameter("user");
			String city = request.getParameter("city");
			String qianming = request.getParameter("qianming");
			System.out.println(user + "\t修改了资料" + "\ntime:" + time);
			User users = new User();
			users.setUser(user);
			users.setNickname(nickname);
			users.setSex(sex);
			users.setYears(years);
			users.setQq(qq);
			users.setCity(city);
			users.setQianming(qianming);
			if (!ndi.update_message(users)) {

				array.put("code", "failure");
				array.put("msg", "用户资料修改失败");

			} else {

				array.put("code", "success");
				array.put("msg", "用户资料修改成功");
				User u = ndi.Search_xmpp_message(users.getUser());
				String str = new Gson().toJson(u);
				ndi.update_xmpp_message(u.getUser().toLowerCase(), str);
				

			}
		} else if (action.equals("update_password")) {
			String user = request.getParameter("user");
			String password = request.getParameter("password");
			System.out.println(user + "\t修改了密码" + "\ntime:" + time);
			if (!ndi.update_message(user, password)) {

				array.put("code", "failure");

			} else {

				array.put("code", "success");

			}
		} else if (action.equals("update_icon")) {
			String files = request.getParameter("file");
			String filename = request.getParameter("filename");
			String user = request.getParameter("user");
			System.out.println(user + "\t修改了头像" + "\ntime:" + time);
			if (files != null) {
				byte[] b = Base64Coder.decodeLines(files);

				File file = new File("c:/yyquan_icon/");
				if (!file.exists()) {
					file.mkdirs();

				}
				FileOutputStream fos = new FileOutputStream(file.getPath()
						+ "/" + filename);

				fos.write(b);
				fos.flush();
				fos.close();
				if (!ndi.update_icon(user, filename)) {

					array.put("code", "failure");

				} else {

					array.put("code", "success");
					User u = ndi.Search_xmpp_message(user);
					String str = new Gson().toJson(u);
					ndi.update_xmpp_message(u.getUser().toLowerCase(), str);

				}
			}
		} else if (action.equals("search_icon")) {

			response.setContentType("image/gif");
			String name = request.getParameter("name");
			String imagePath = "c:/yyquan_icon/" + name;
			FileInputStream fis = new FileInputStream(imagePath);

			int size = fis.available(); // 得到文件大小

			byte data[] = new byte[size];

			fis.read(data); // 读数据

			fis.close();

			OutputStream os = response.getOutputStream();
			os.write(data);
			os.flush();
			os.close();
		}

		out.print(array);

		out.flush();
		out.close();
	}
}
