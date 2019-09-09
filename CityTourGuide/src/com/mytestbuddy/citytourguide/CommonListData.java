package com.mytestbuddy.citytourguide;

public class CommonListData {

	private String ListId;
	private String Name;
	private String Description;
	private String Photo;
	private String Category;

	public CommonListData(String ListId, String Name, String Description,
			String Photo, String Ctegory) {
		this.ListId = ListId;
		this.Name = Name;
		this.Description = Description;
		this.Photo = Photo;
	}

	// Set & Get ListId...
	public void setListId(String ListId) {
		this.ListId = ListId;
	}

	public String getListId() {
		return ListId;
	}

	// Set & Get Name...
	public void setName(String Name) {
		this.Name = Name;
	}

	public String getName() {
		return Name;
	}

	// Set & Get Description...
	public void setDescription(String Description) {
		this.Description = Description;
	}

	public String getDescription() {
		return Description;
	}

	// Set & Get Photo...
	public void setPhoto(String Photo) {
		this.Photo = Photo;
	}

	public String getPhoto() {
		return Photo;
	}

	// Set & Get Category...
	public void setCategory(String Category) {
		this.Category = Category;
	}

	public String getCategory() {
		return Category;
	}
}