package com.jzh.news.control;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.jzh.news.dao.News_pinglunDaoImpl;
import com.jzh.news.entity.News_pinglun;

public class addPingLun {

	public static void main(String[] args) {
		File in = new File("index.html");
		News_pinglunDaoImpl ndi=new News_pinglunDaoImpl();
		try {

			Document doc = Jsoup.parse(in, "UTF-8", "");

			Elements e1 = doc.getElementsByClass("comment_item");

			for (int i = e1.size()-1; i>=0; i--) {
				String ptime=e1.get(i).getElementsByClass("ptime").text();
				ptime=ptime.replaceAll("·¢±í", "");
				System.out.println(ptime
						+ "\t"
						+e1.get(i).getElementsByClass("username")
						.text()
						+ "\t"
						+ (e1.get(i).getElementsByTag("img").attr("src"))
						+ "\t"
						+ e1.get(i).getElementsByClass("comment_body").text());
				
				int id = 30;//ÎÄÕÂid
				String user = e1.get(i).getElementsByClass("username").text()+ ";"
				+ (e1.get(i).getElementsByTag("img").attr("src"));
				String plocation = "";
				String pcontent = e1.get(i).getElementsByClass("comment_body").text();
				String zan = "0";
				News_pinglun news = new News_pinglun(id, user, plocation, ptime,
						pcontent, zan);
				if (ndi.save(news)) {
					

				}

			}

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

}
