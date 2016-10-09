package com.jzh.news.xmpp;

import java.util.List;

import com.jzh.news.dao.UserDaoImpl;
import com.jzh.news.entity.User;

public class text {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// SmackXMPPTest.login("15699929627", "123456");
		UserDaoImpl udi = new UserDaoImpl();
		final List<User> list = udi.Search();
		for (int i = 0; i < list.size(); i++) {
			XmppTool.create(list.get(i));
//			if(list.get(i).getLocation()==null||list.get(i).getLocation().equals("")){
//				udi.update_qianming(list.get(i).getUser());
//			}
			
		}
		
		

	}

}
