package main.story;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.common.DateUtils;
import main.story.po.Body;
import main.story.po.OneDayNews;
import main.story.po.Page;
import main.story.po.Story;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年12月5日
 **/

public class StoryService {
	private final static String URL_LATEST = "http://news-at.zhihu.com/api/4/news/latest";
	private final static String URL_BEFORE = "http://news.at.zhihu.com/api/4/news/before/";
	private final static String URL_NEWS = "http://news-at.zhihu.com/api/4/news/";
	private final static String URL_THEMES = "http://news-at.zhihu.com/api/4/themes";
	private final static String URL_THEME = "http://news-at.zhihu.com/api/4/theme/";
	private final Date birthday = DateUtils.getDate(2013, 4, 19);// 知乎日报的生日20130519
	private final int eachPage = 3;

	public Body renderBody(int currentPage) throws ParseException, IOException {
		Date today = DateUtils.parse(DateUtils.format(new Date()));
		int pageNum = (int) ((today.getTime() - birthday.getTime()) / (24 * 60 * 60 * 1000) / eachPage + 1);
		currentPage = Math.min(currentPage, pageNum);
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		if (currentPage == 1) {
			newsList.addAll(getTodayNews(today));
		}

		for (Date d = DateUtils.addDay(today, -(currentPage - 1) * eachPage + 1); d.after(birthday); d = DateUtils.addDay(d, -1)) {
			if (d.after(today)) continue;
			if (newsList.size() > eachPage - 1) break;
			newsList.addAll(getBeforeNews(d));
		}

		Body body = new Body();
		body.setNewsList(newsList);
		body.setPages(getPages(currentPage, pageNum));
		return body;
	}

	private List<Page> getPages(int currentPage, int pageNum) {
		List<Page> pages = new ArrayList<Page>();
		for (int i = 1; i <= pageNum; i++) {
			Page page = new Page();
			page.setPage(i);
			if (i == currentPage) page.setCurrent(true);
			if (Math.abs(currentPage - i) > 5) page.setHide(true);
			pages.add(page);
		}
		return pages;
	}

	private List<OneDayNews> getTodayNews(Date today) throws IOException, JSONException, ParseException {
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		OneDayNews oneDay = new OneDayNews();
		String jsonToday = getJson(URL_LATEST);
		List<Story> storiesToday = getStories(jsonToday);
		if (storiesToday.size() > 0 && !DateUtils.format(today).equals(storiesToday.get(0).getDate())) return newsList;
		oneDay.setDate(DateUtils.format(today, DateUtils.FORMAT_YYYY_MM_DD_EEEE));
		oneDay.setStories(storiesToday);
		newsList.add(oneDay);
		return newsList;
	}

	private List<OneDayNews> getBeforeNews(Date d) throws IOException, JSONException, ParseException {
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		OneDayNews oneDay = new OneDayNews();
		String jsonYesterday = getJson(URL_BEFORE + DateUtils.format(d));
		List<Story> storiesYesterday = getStories(jsonYesterday);
		oneDay.setDate(DateUtils.format(DateUtils.addDay(d, -1), DateUtils.FORMAT_YYYY_MM_DD_EEEE));
		oneDay.setStories(storiesYesterday);
		newsList.add(oneDay);
		return newsList;
	}

	private List<Story> getStories(String jsonStr) throws IOException, JSONException, ParseException {
		List<Story> stories = new ArrayList<Story>();
		JSONObject jsonobj = new JSONObject(jsonStr);
		if (jsonobj.length() == 0) return stories;
		Date date = DateUtils.parse(jsonobj.getString("date"));
		JSONArray storyArr = jsonobj.getJSONArray("stories");
		Story story = null;
		for (int i = 0; i < storyArr.length(); i++) {
			jsonobj = storyArr.getJSONObject(i);
			story = new Story();
			story.setId(jsonobj.getInt("id"));
			story.setType(jsonobj.getInt("type"));
			story.setTitle(jsonobj.getString("title"));
			story.setGa_prefix(jsonobj.getString("ga_prefix"));
			story.setTitle(jsonobj.getString("title"));
			story.setMultipic(jsonobj.isNull("multipic") ? false : jsonobj.getBoolean("multipic"));
			if (story.getType() != 0) story.setImage(getImgLocalUrl(date, jsonobj.getJSONArray("images").get(0).toString()));
			story.setDate(DateUtils.format(date));
			stories.add(story);
		}
		return stories;
	}

	public Body getOneDayNews(String sDate) throws ParseException, JSONException, IOException {
		Date d = DateUtils.parse(sDate);
		List<OneDayNews> newsList = getBeforeNews(DateUtils.addDay(d, 1));
		Body body = new Body();
		body.setNewsList(newsList);
		body.setPages(new ArrayList<Page>());
		body.setQueryDate(DateUtils.format(d, DateUtils.FORMAT_YYYY_MM_DD));
		return body;
	}

	public String getImgUrl(Date date, int storyId, int type, String imgSrc) throws MalformedURLException, IOException {
		String imgUrl;
		ImgUrlModel img = ImgUrlModel.dao.findById(storyId);
		if (img != null) {
			imgUrl = img.getStr("url");
		} else {
			imgUrl = saveImg(date, storyId, type, imgSrc);
		}
		return imgUrl;
	}

	private String saveImg(Date date, int storyId, int type, String imgSrc) throws IOException, MalformedURLException {
		if (type == 0) imgSrc = new JSONObject(getJson(URL_NEWS + storyId)).getString("image");
		String imgLocalUrl = getImgLocalUrl(date, imgSrc);
		new ImgUrlModel().set("id", storyId).set("type", type).set("url", imgLocalUrl).set("img_date", new java.sql.Date(date.getTime())).save();
		return imgLocalUrl;
	}

	private String getImgLocalUrl(Date date, String imgageUrl) throws MalformedURLException, IOException {
		File filePath = new File("Web/static/img/pic/" + DateUtils.format(date));
		if (!filePath.exists()) filePath.mkdirs();
		String file = filePath.getPath() + "/" + (imgageUrl.replaceAll(":", "-").replaceAll("/", "_"));
		if (new File(file).exists()) return file.replaceFirst("Web", "");
		downloadImg(imgageUrl, file);
		return file.replaceFirst("Web", "");
	}

	private void downloadImg(String imgageUrl, String file) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(imgageUrl).openConnection();
		DataInputStream in = new DataInputStream(connection.getInputStream());
		FileOutputStream fout = new FileOutputStream(file);
		DataOutputStream out = new DataOutputStream(fout);
		byte[] buffer = new byte[4096];
		int count = 0;
		while ((count = in.read(buffer)) > 0) {
			out.write(buffer, 0, count);
		}
		fout.close();
		out.close();
		in.close();
	}

	private String getJson(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		InputStream inputStream = connection.getInputStream();
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		BufferedReader bufferedReader = new BufferedReader(reader);
		String str = null;
		StringBuffer sb = new StringBuffer();
		while ((str = bufferedReader.readLine()) != null) {
			sb.append(str);
		}
		reader.close();
		connection.disconnect();
		return sb.toString();
	}
}
