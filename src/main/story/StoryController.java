package main.story;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import org.json.JSONException;
import main.common.DateUtils;
import main.story.po.Body;
import com.jfinal.core.Controller;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/


public class StoryController extends Controller {
	private StoryService service = new StoryService();

	public void index() throws JSONException, IOException, ParseException {
		String spage = getPara();
		int currentPage = (null != spage && !"".equals(spage)) ? Integer.valueOf(spage) : 1;
		Body body = service.renderBody(currentPage);
		setAttr("news", body.getNewsList());
		setAttr("pages", body.getPages());
		render("body.html");
	}

	public void image() throws MalformedURLException, IOException, ParseException {
		String imgUrl = service.getImgUrl(DateUtils.parse(getPara("date")), getParaToInt("id"), getParaToInt("type"), getPara("src"));
		setAttr("src", imgUrl);
		setAttr("id", getParaToInt("id"));
		renderJson();
	}

	public void oneDayNews() throws JSONException, IOException, ParseException {
		String sDate = getPara();
		Body body = service.getOneDayNews(sDate);
		setAttr("news", body.getNewsList());
		setAttr("pages", body.getPages());
		setAttr("queryDate", body.getQueryDate());
		render("body.html");
	}
}
