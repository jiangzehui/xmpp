package com.yyquan.jzh.view;

import android.widget.ListView;

/**
 * ScrollView中嵌入ListView,让ListView全显示出来
 * 
 * @author reyo
 * 
 */
public class MyListView extends ListView {

	public MyListView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}
	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
