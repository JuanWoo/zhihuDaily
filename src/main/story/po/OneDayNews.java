package main.story.po;

import java.util.List;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

public class OneDayNews {
	private String head;
	private List<Story> stories;

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public List<Story> getStories() {
		return stories;
	}

	public void setStories(List<Story> stories) {
		this.stories = stories;
	}
}
