package main.story;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import main.common.DateUtils;
import main.story.po.OneDayNews;
import main.story.po.Page;

import com.jfinal.core.Controller;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/


public class StoryController extends Controller {

	private final int eachPage = 3;
	private StoryService service = new StoryService();

	public void index() throws JSONException, IOException, ParseException  {
		String spage = getPara();
		int currentPage = (null != spage && !"".equals(spage)) ? Integer.valueOf(spage) : 1;
		Date today = DateUtils.parse(DateUtils.format(new Date()));
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		if (currentPage == 1) {
			newsList.addAll(service.addTodayNews(today));
		}

		Date firstDay = DateUtils.parse("20130519");
		int pageNum = (int) ((today.getTime() - firstDay.getTime()) / (24 * 60 * 60 * 1000) / eachPage + 1);
		currentPage = Math.min(currentPage, pageNum);
		for (Date d = DateUtils.addDay(today, -(currentPage - 1) * eachPage + 1); d.after(firstDay); d = DateUtils.addDay(d, -1)) {
			if (d.after(today)) continue;
			if (newsList.size() > eachPage - 1) break;
			newsList.addAll(service.addBeforeNews(d));
		}

		List<Page> pages = new ArrayList<Page>();
		for (int i = 1; i <= pageNum; i++) {
			Page page = new Page();
			page.setPage(i);
			if (i == currentPage) page.setCurrent(true);
			if (Math.abs(currentPage - i) > 5) page.setHide(true);
			pages.add(page);
		}

		setAttr("news", newsList);
		setAttr("pages", pages);
		render("body.html");
	}

	public void image() throws MalformedURLException, IOException, ParseException {
		String imgUrl = service.getImgUrl(DateUtils.parse(getPara("date")), getParaToInt("id"));
		setAttr("src", imgUrl);
		setAttr("id", getParaToInt("id"));
		renderJson();
	}
}
