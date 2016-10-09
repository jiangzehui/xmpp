package com.jzh.news.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.jzh.news.dao.News_typeDaoImpl;
import com.jzh.news.entity.News_type;

public class DoGetType extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		News_typeDaoImpl ndi = new News_typeDaoImpl();
		List<News_type> list = new ArrayList<News_type>();
		list = ndi.getAllNewTypes();
		JSONObject array = new JSONObject();
		if (list.size() < 1) {

			array.put("code", "failure");
			array.put("msg", "暂无数据");
			array.put("data", "");
		} else {

			array.put("code", "success");
			array.put("msg", "数据请求成功");
			array.put("data", list);
		}

		out.print(array);

		out.flush();
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doGet(request, response);
	}

}
