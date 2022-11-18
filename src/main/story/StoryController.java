package main.story;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONException;

import main.common.StoryException;
import main.story.po.Body;
import main.story.po.OneDayNews;
import main.story.po.Page;
import main.story.po.Theme;
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
		try {
		    Body body = service.getIndex(currentPage);
		    renderBody(body);
        } catch (StoryException e) {
            redirect(e.getMessage());
        } catch (Exception e) {
            renderEmpty();
        }
	}

	public void image() throws MalformedURLException, IOException, ParseException {
		String imgUrl = service.getImgUrl(getPara("path"), getParaToInt("id"), getParaToInt("type"), getPara("src"));
		setAttr("src", imgUrl.startsWith("/") ? imgUrl.substring(1) : imgUrl);
		setAttr("id", getParaToInt("id"));
		renderJson();
	}

	public void themes() throws IOException {
		List<Theme> themes = service.getThemes();
		setAttr("themes", themes);
		renderJson();
	}

	public void theme() throws JSONException, IOException, ParseException {
		int themeId = getParaToInt();
		Body body = service.getTheme(themeId);
		renderBody(body);
	}

	public void oneDayNews() throws JSONException, IOException, ParseException {
		String sDate = getPara();
		try {
		    Body body = service.getOneDayNews(sDate);
		    renderBody(body);
		} catch (StoryException e) {
            redirect(e.getMessage());
        }
	}

	private void renderBody(Body body) {
		setAttr("news", body.getNewsList());
		setAttr("pages", body.getPages());
		setAttr("queryDate", body.getQueryDate());
		render("body.html");
	}
	
	private void renderEmpty() {
        setAttr("news", new ArrayList<OneDayNews>());
        setAttr("pages", new ArrayList<Page>());
        setAttr("queryDate", "");
        render("body.html");
    }
}
