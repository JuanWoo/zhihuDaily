package main.story;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import main.common.DateUtils;
import main.story.po.Body;
import main.story.po.OneDayNews;
import main.story.po.Page;
import main.story.po.Story;
import main.story.po.Theme;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.core.JFinal;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年12月5日
 **/

public class StoryService {
	private final static String URL_LATEST = "https://news-at.zhihu.com/api/4/news/latest";
	private final static String URL_BEFORE = "https://news.at.zhihu.com/api/4/news/before/";
	private final static String URL_NEWS = "https://news-at.zhihu.com/api/4/news/";
	private final static String URL_THEMES = "https://news-at.zhihu.com/api/4/themes";
	private final static String URL_THEME = "https://news-at.zhihu.com/api/4/theme/";
	private final static String IMG_URL_PREFIX = "/static/img/";
	private final static String WEBROOT = JFinal.me().getServletContext().getRealPath("/");
	private final Date birthday = DateUtils.getDate(2013, 4, 19);// 知乎日报的生日20130519
	private final int eachPage = 3;

	public Body getIndex(int currentPage) throws ParseException, IOException {
		Date today = DateUtils.parse(DateUtils.format(new Date()));
		int pageNum = (int) ((today.getTime() - birthday.getTime()) / (24 * 60 * 60 * 1000) / eachPage + 1);
		currentPage = Math.min(currentPage, pageNum);
		String todayHead = null;
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		if (currentPage == 1) {
			OneDayNews todayNews = getTodayNews(today);
			todayHead = todayNews.getHead();
			newsList.add(todayNews);
		}

		for (Date d = DateUtils.addDay(today, -(currentPage - 1) * eachPage + 1); d.after(birthday); d = DateUtils.addDay(d, -1)) {
			if (d.after(today)) continue;
			if (newsList.size() > eachPage - 1) break;
			OneDayNews oneDayNews = getBeforeNews(d);
			if (!oneDayNews.getHead().equals(todayHead)) newsList.add(oneDayNews);
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

	private OneDayNews getTodayNews(Date today) throws IOException, JSONException, ParseException {
		OneDayNews oneDay = new OneDayNews();
		String jsonToday = getJson(URL_LATEST);
		List<Story> storiesToday = getStories(jsonToday);
		oneDay.setHead(DateUtils.format(DateUtils.parse(new JSONObject(jsonToday).getString("date")), DateUtils.FORMAT_YYYY_MM_DD_EEEE));
		oneDay.setStories(storiesToday);
		return oneDay;
	}

	private OneDayNews getBeforeNews(Date d) throws IOException, JSONException, ParseException {
		OneDayNews oneDay = new OneDayNews();
		String jsonYesterday = getJson(URL_BEFORE + DateUtils.format(d));
		List<Story> storiesYesterday = getStories(jsonYesterday);
		oneDay.setHead(DateUtils.format(DateUtils.parse(new JSONObject(jsonYesterday).getString("date")), DateUtils.FORMAT_YYYY_MM_DD_EEEE));
		oneDay.setStories(storiesYesterday);
		return oneDay;
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
			story.setGa_prefix(jsonobj.getString("ga_prefix"));
			story.setMultipic(jsonobj.isNull("multipic") ? false : jsonobj.getBoolean("multipic"));
			story.setTitle(jsonobj.getString("title") + (story.isMultipic() ? "（多图）" : ""));
			story.setPath("pic/" + DateUtils.format(date));
			if (story.getType() != 0 && !jsonobj.isNull("images")) {
				story.setImage(getImgLocalUrl(story.getPath(), story.getId(), jsonobj.getJSONArray("images").get(0).toString()));
			}
			stories.add(story);
		}
		return stories;
	}

	public Body getOneDayNews(String sDate) throws ParseException, JSONException, IOException {
		Date d = DateUtils.parse(sDate);
		OneDayNews oneDay = getBeforeNews(DateUtils.addDay(d, 1));
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		newsList.add(oneDay);
		Body body = new Body();
		body.setNewsList(newsList);
		body.setQueryDate(DateUtils.format(d, DateUtils.FORMAT_YYYY_MM_DD));
		return body;
	}

	public List<Theme> getThemes() throws IOException {
		List<Theme> themes = new ArrayList<Theme>();
		String jsonThemes = getJson(URL_THEMES);
		JSONObject jsonobj = new JSONObject(jsonThemes);
		JSONArray themesArray = jsonobj.getJSONArray("others");
		Theme theme = null;
		for (int i = 0; i < themesArray.length(); i++) {
			jsonobj = themesArray.getJSONObject(i);
			theme = new Theme();
			theme.setId(jsonobj.getInt("id"));
			theme.setColor(jsonobj.getInt("color"));
			theme.setName(jsonobj.getString("name"));
			theme.setThumbnail(jsonobj.getString("thumbnail"));
			theme.setDescription(jsonobj.getString("description"));
			themes.add(theme);
		}
		return themes;
	}

	public Body getTheme(int themeId) throws IOException, JSONException, ParseException {
		JSONObject jsonobj = new JSONObject(getJson(URL_THEME + themeId));
		List<Story> themeStories = getThemeStories(jsonobj, themeId);
		OneDayNews oneDay = new OneDayNews();
		oneDay.setStories(themeStories);
		oneDay.setHead(jsonobj.getString("name"));
		List<OneDayNews> newsList = new ArrayList<OneDayNews>();
		newsList.add(oneDay);
		Body body = new Body();
		body.setNewsList(newsList);
		return body;
	}

	private List<Story> getThemeStories(JSONObject jsonobj, int themeId) throws IOException, JSONException, ParseException {
		List<Story> stories = new ArrayList<Story>();
		if (jsonobj.length() == 0) return stories;
		JSONArray storyArr = jsonobj.getJSONArray("stories");
		Story story = null;
		for (int i = 0; i < storyArr.length(); i++) {
			jsonobj = storyArr.getJSONObject(i);
			story = new Story();
			story.setId(jsonobj.getInt("id"));
			story.setType(jsonobj.getInt("type"));
			story.setTitle(jsonobj.getString("title"));
			story.setPath("theme/" + themeId);
			if (!jsonobj.isNull("images")) {
				story.setImage(getImgLocalUrl(story.getPath(), story.getId(), jsonobj.getJSONArray("images").get(0).toString()));
			}
			stories.add(story);
		}
		return stories;
	}

	public String getImgUrl(String path, int storyId, int type, String imgSrc) throws MalformedURLException, IOException {
		String file = WEBROOT + IMG_URL_PREFIX + path + "/" + storyId + ".jpg";
		if (new File(file).exists()) return file.substring(WEBROOT.length() + 1);
		String imgUrl = saveImg(path, storyId, type, imgSrc);
		return imgUrl;
	}

	private String saveImg(String path, int storyId, int type, String imgSrc) throws IOException, MalformedURLException {
		if (type == 0 && path.indexOf("theme") < 0) imgSrc = new JSONObject(getJson(URL_NEWS + storyId)).getString("image");
		if (WEBROOT.endsWith(imgSrc) || imgSrc.equals("/")) return "";
		String imgLocalUrl = getImgLocalUrl(path, storyId, imgSrc);
		return imgLocalUrl;
	}

	private String getImgLocalUrl(String path, int storyId, String imgageUrl) throws MalformedURLException, IOException {
		File filePath = new File(WEBROOT + IMG_URL_PREFIX + path);
		if (!filePath.exists()) filePath.mkdirs();
		String file = filePath.getPath() + "/" + storyId + ".jpg";
		if (new File(file).exists()) return file.substring(WEBROOT.length() + 1);
		downloadImg(imgageUrl, file);
		return file.substring(WEBROOT.length());
	}

	private void downloadImg(String imgageUrl, String file) throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(imgageUrl.replace("https:", "http:")).openConnection();
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
		CloseableHttpClient httpclient = createSSLClientDefault();
		HttpGet httpget = new HttpGet(urlStr);
		CloseableHttpResponse response = httpclient.execute(httpget);
		StringBuffer sb = new StringBuffer();
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			sb.append(EntityUtils.toString(entity));
		}
		response.close();
		httpclient.close();
		return sb.toString();
	}

	private static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
				@Override
				public boolean verify(String s, SSLSession sslSession) {
					return true;
				}
			});
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}
