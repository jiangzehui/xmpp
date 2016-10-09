package com.jzh.news.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DoGetIcon extends HttpServlet {

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		response.setCharacterEncoding("utf-8");
		request.setCharacterEncoding("utf-8");
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

	

}
