package com.jzh.news.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jzh.news.entity.News_content;
import com.jzh.news.entity.News_type;

public class News_typeDaoImpl extends BaseDaoImpl {
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	List<News_type> list = new ArrayList<News_type>();

	/**
	 * 从数据库获取新闻数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<News_type> getAllNewTypes() {
		conn = this.getConnection();
		try {
			pstmt = conn.prepareStatement("select * from news_type");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				News_type news = new News_type();
				news.setId(rs.getInt("id"));
				news.setType_name(rs.getString("type_name"));
				news.setType_url(rs.getString("type_url"));

				list.add(news);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			this.closeAll(rs, pstmt, conn);
		}

		return list;
	}

}
