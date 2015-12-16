package main.story.po;

import java.util.List;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年12月14日
 **/

public class Body {
	private List<OneDayNews> newsList;
	private List<Page> pages;
	private String queryDate;

	public List<OneDayNews> getNewsList() {
		return newsList;
	}

	public void setNewsList(List<OneDayNews> newsList) {
		this.newsList = newsList;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public String getQueryDate() {
		return queryDate;
	}

	public void setQueryDate(String queryDate) {
		this.queryDate = queryDate;
	}
}
