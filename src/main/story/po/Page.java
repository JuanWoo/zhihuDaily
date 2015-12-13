package main.story.po;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

public class Page {
	private int page;
	private boolean current = false;
	private boolean hide = false;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}
}
