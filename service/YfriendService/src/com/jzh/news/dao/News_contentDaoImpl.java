package com.jzh.news.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jzh.news.entity.News_content;
import com.jzh.news.entity.News_luntan;

public class News_contentDaoImpl extends BaseDaoImpl {
	List<News_content> list = new ArrayList<News_content>();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Connection conn = null;

	public List<News_content> getAllNews_content(String type, String limit) {
		conn = this.getConnection();
		try {

			pstmt = conn
					.prepareStatement("select * from news_content where ctype='"
							+ type
							+ "' order by cid desc limit "
							+ limit
							+ ",10");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				News_content content = new News_content();
				content.setCid(rs.getInt("cid"));
				content.setCtype(rs.getString("ctype"));
				content.setCtitle(rs.getString("ctitle"));
				// content.setCzhaiyao(rs.getString("czhaiyao"));
				// content.setCcontent(rs.getString("ccontent"));
				content.setCimage(rs.getString("cimage"));
				content.setCauthor(rs.getString("cauthor"));
				content.setCtime(rs.getString("ctime"));
				content.setCpinglun(rs.getString("cpinglun"));
				list.add(content);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			this.closeAll(rs, pstmt, conn);

		}
		return list;

	}

	public String getcontent(String cid) {
		conn = this.getConnection();
		String content = "";
		try {

			pstmt = conn
					.prepareStatement("select * from news_content where cid='"
							+ cid + "'");
			rs = pstmt.executeQuery();
			while (rs.next()) {

				content = rs.getString("ccontent");

			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			this.closeAll(rs, pstmt, conn);

		}
		return content;

	}

	/**
	 * 根据type查询总数
	 * 
	 * @param id
	 * @return
	 */
	public int search_total(String ctype) {
		conn = this.getConnection();
		try {

			pstmt = conn
					.prepareStatement("select * from news_content where ctype='"
							+ ctype + "'");
			rs = pstmt.executeQuery();
			rs.last();
			int rowCount = rs.getRow(); // 获得ResultSet的总行数
			return rowCount;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			this.closeAll(rs, pstmt, conn);

		}

	}

	/**
	 * 保存文章
	 * 
	 * @param news
	 */
	public boolean save(News_content news) {
		conn = this.getConnection();
		try {
			pstmt = conn
					.prepareStatement("insert into news_content(ctype,ctitle,czhaiyao,ccontent,cimage,cauthor,ctime,cpinglun)values(?,?,?,?,?,?,?,?)");

			pstmt.setString(1, news.getCtype());
			pstmt.setString(2, news.getCtitle());
			pstmt.setString(3, news.getCzhaiyao());
			pstmt.setString(4, news.getCcontent());
			pstmt.setString(5, news.getCimage());
			pstmt.setString(6, news.getCauthor());
			pstmt.setString(7, news.getCtime());
			pstmt.setString(8, news.getCpinglun());
			pstmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			this.closeAll(null, pstmt, conn);
		}
	}

}
