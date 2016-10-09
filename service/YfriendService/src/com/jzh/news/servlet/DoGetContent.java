package com.jzh.news.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.jzh.news.dao.News_contentDaoImpl;
import com.jzh.news.dao.News_pinglunDaoImpl;
import com.jzh.news.entity.News_content;
import com.jzh.news.entity.News_pinglun;
import com.jzh.news.entity.User;

public class DoGetContent extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
		String action = request.getParameter("action");
		if (action.equals("search_content")) {
			String cid = request.getParameter("cid");
			PrintWriter out = response.getWriter();
			News_contentDaoImpl ndi = new News_contentDaoImpl();
			String content = ndi.getcontent(cid);
			JSONObject array = new JSONObject();
			if (content.equals("")) {
				array.put("code", "failure");
				array.put("msg", "暂无数据");
				array.put("data", "");
			} else {
				array.put("code", "success");
				array.put("msg", "数据请求成功");
				array.put("data", content);
			}
			//System.out.println(array.toString());
			out.print(array);
			out.flush();
			out.close();
		} else if(action.equals("search_title")){
			String type = request.getParameter("type");
			String limit = request.getParameter("limit");
			// System.out.println(type+limit);

			PrintWriter out = response.getWriter();
			News_contentDaoImpl ndi = new News_contentDaoImpl();

			List<News_content> list = new ArrayList<News_content>();

			list = ndi.getAllNews_content(type, limit);
			JSONObject array = new JSONObject();
			if (list.size() < 1) {

				array.put("code", "failure");
				array.put("msg", "暂无数据");
				array.put("data", "");
			} else {
				array.put("code", "success");
				array.put("msg", "数据请求成功");
				News_pinglunDaoImpl npi = new News_pinglunDaoImpl();
				int pinglun_size;
				int wenzhang_size;
				JSONArray arrays = new JSONArray();
				wenzhang_size = ndi.search_total(type);
				for (int i = 0; i < list.size(); i++) {

					pinglun_size = npi.search_total(list.get(i).getCid() + "");

					JSONObject object = new JSONObject();
					object.put("pinglun", pinglun_size + "");
					object.put("cdata", list.get(i));
					object.put("wenzhang", wenzhang_size);
					arrays.add(object);

				}

				array.put("data", arrays.toString());
				// System.out.println(array.toString());
			}

			out.print(array);

			out.flush();
			out.close();
		}

	}

}
