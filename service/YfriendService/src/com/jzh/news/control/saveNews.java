package com.jzh.news.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.jzh.news.dao.News_contentDaoImpl;
import com.jzh.news.entity.News_content;

public class saveNews {

	public static void main(String[] args) {
		runTask();
	}

	static URL url;
	static BufferedReader br;
	static Document doc = null;

	public static void runTask() {
		// execute 中放置执行的方法
		// String[] geturls =
		// {"http://www.nmc.gov.cn/publish/weather/capital.html"
		// ,"http://www.nmc.gov.cn/publish/weather/range.html" };
		// String[] geturls = { "http://www.nmc.cn/publish/forecast/china.html"
		// };
		String[] geturls = { "http://blog.csdn.net/lmj623565791?viewmode=contents" };

		for (int i = 0; i < geturls.length; i++) {
			String url = geturls[i];
			getweather(url);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println("time end:" + sdf.format(new Date()));
	}

	public static void getweather(String urlstring) {
		try {
			// url = new URL(urlstring);
			// doc = Jsoup.parse(url, 5000);
			doc = Jsoup
					.connect(urlstring)
					.header(
							"User-Agent",
							"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
					.get();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Elements es_cnames2 = doc.getElementsByClass("list_item");
		List<String> list ;
		List<String> lists ;

		for (int i = es_cnames2.size() - 1; i >= 0; i--) {
			list = new ArrayList<String>();
			lists = new ArrayList<String>();
			String title = es_cnames2.get(i).getElementsByClass("link_title")
					.text();
			String date = es_cnames2.get(i).getElementsByClass("link_postdate")
					.text();

			Elements e1s = es_cnames2.get(i).getElementsByClass("link_title");

			Element e1 = e1s.get(0);
			Elements e11 = e1.getElementsByTag("a");
			String str = e11.attr("href");
			System.out
					.println("----------------------------------开始----------------------------------");
			System.out.println(title + "\t" + date + "\t" + str);

			try {
				Document docs = Jsoup
						.connect("http://blog.csdn.net" + str)
						.header(
								"User-Agent",
								"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
						.get();
				Element t1 = docs.getElementById("article_content");
				Elements t2 = t1.getElementsByTag("p");
				Elements t3 = t1.getElementsByTag("span");
				StringBuilder sb = new StringBuilder();
				for (int j = 1; j < t2.size(); j++) {
					sb.append(t2.get(j).text() + "\n\n");
					Elements e111 = t2.get(j).getElementsByTag("img");
					for (int k = 0; k < e111.size(); k++) {
						sb.append(";;" + e111.attr("src") +";;"+ "\n\n");
						list.add(sb.toString());
						lists.add(e111.attr("src"));
						sb = new StringBuilder();
					}
				}
				sb.append("附加信息：\n\n");
				sb.append(t3.text() + "\n\n");
				list.add(sb.toString());
				String st = list.toString();
				News_contentDaoImpl cdi = new News_contentDaoImpl();
				News_content news = new News_content();
				news.setCcontent(st);
				if(lists.size()>0){
					news.setCimage(lists.get(0));
				}
				news.setCtime(date);
				news.setCtype("android开发");
				news.setCtitle(title);
				news.setCauthor("hongyang");
				news.setCzhaiyao(st);
				news.setCpinglun("0");
				if (cdi.save(news)) {
					System.out.println("保存成功！");
				}
				

				System.out
						.println("----------------------------------结束----------------------------------");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
