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
import main.story.po.OneDayNews;
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

	public List<OneDayNews> addTodayNews(Date today) throws IOException, JSONException, ParseException {
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

	public List<OneDayNews> addBeforeNews(Date d) throws IOException, JSONException, ParseException {
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
			if (!jsonobj.isNull("theme")) continue;// 忽略主题日报
			story = new Story();
			story.setId(jsonobj.getInt("id"));
			story.setType(jsonobj.getInt("type"));
			story.setTitle(jsonobj.getString("title"));
			story.setGa_prefix(jsonobj.getString("ga_prefix"));
			story.setTitle(jsonobj.getString("title"));
			story.setMultipic(jsonobj.isNull("multipic") ? false : jsonobj.getBoolean("multipic"));
			//story.setImage(getImgUrl(date, story.getId()));
			story.setDate(DateUtils.format(date));
			stories.add(story);
		}
		return stories;
	}

	public String getImgUrl(Date date, int storyId) throws MalformedURLException, IOException {
		String imgUrl;
		ImgUrlModel img = new ImgUrlModel().findById(storyId);
		if (img != null) {
			imgUrl = img.getStr("url");
		} else {
			imgUrl = saveImg(date, storyId);
		}
		return imgUrl;
	}

	private String saveImg(Date date, int storyId) throws MalformedURLException, IOException {
		JSONObject newsJson = new JSONObject(getJson(URL_NEWS + storyId));
		String imgLocalUrl = getImgLocalUrl(date, newsJson.getString("image"));
		new ImgUrlModel().set("id", storyId).set("url", imgLocalUrl).set("img_date", new java.sql.Date(date.getTime())).save();
		return imgLocalUrl;
	}

	private String getImgLocalUrl(Date date, String imgageUrl) throws MalformedURLException, IOException {
		File filePath = new File("Web/static/img/pic/" + DateUtils.format(date));
		if (!filePath.exists()) filePath.mkdirs();
		String file = filePath.getPath() + "/" + (imgageUrl.replaceAll(":", "-").replaceAll("/", "_"));
		if (new File(file).exists()) return file.replaceFirst("Web/", "");
		downloadImg(imgageUrl, file);
		return file.replaceFirst("Web/", "");
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
