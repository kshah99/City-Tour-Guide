package com.mytestbuddy.citytourguide;

public class UserReviewData {

	private String Photo;
	private String Name;
	private String Rating;
	private String Comment;
	private String DateTime;

	public UserReviewData(String Photo, String Name, String Rating,
			String Comment, String DateTime) {
		this.Photo = Photo;
		this.Name = Name;
		this.Rating = Rating;
		this.Comment = Comment;
		this.DateTime = DateTime;
	}

	// Set & Get Photo
	public void setPhoto(String Photo) {
		this.Photo = Photo;
	}

	public String getPhoto() {
		return Photo;
	}

	// Set & Get Name...
	public void setName(String Name) {
		this.Name = Name;
	}

	public String getName() {
		return Name;
	}

	// Set & Get Rating...
	public void setRating(String Rating) {
		this.Rating = Rating;
	}

	public String getRating() {
		return Rating;
	}

	// Set & Get Comment...
	public void setComment(String Comment) {
		this.Comment = Comment;
	}

	public String getComment() {
		return Comment;
	}

	// Set & Get DateTime...
	public void setDateTime(String DateTime) {
		this.DateTime = DateTime;
	}

	public String getDateTime() {
		return DateTime;
	}
}