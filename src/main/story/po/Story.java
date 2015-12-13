package main.story.po;

/**
 * @desc	
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

public class Story {
	private int id;
	private String ga_prefix;
	private String title;
	private String image = "";
	private int type;
	private boolean multipic;
	private String date;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGa_prefix() {
		return ga_prefix;
	}

	public void setGa_prefix(String ga_prefix) {
		this.ga_prefix = ga_prefix;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isMultipic() {
		return multipic;
	}

	public void setMultipic(boolean multipic) {
		this.multipic = multipic;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
