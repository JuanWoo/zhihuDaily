package main.story.po;

import java.util.List;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

public class OneDayNews {
	private String date;
	private List<Story> stories;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<Story> getStories() {
		return stories;
	}

	public void setStories(List<Story> stories) {
		this.stories = stories;
	}
}
