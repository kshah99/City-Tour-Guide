package com.mytestbuddy.citytourguide;

public class CommonInfoData {

	private String TypeId;
	private String Name;
	private String Category;
	private String Photo;
	private String Description;
	private String Address;
	private String Pincode;
	private String ContactNumber;
	private String AlternateNumber;
	private String Email;
	private String Website;

	public CommonInfoData(String TypeId, String Name, String Category,
			String Photo, String Description, String Address, String Pincode,
			String ContactNumber, String AlternateNumber, String Email,
			String Website) {
		this.TypeId = TypeId;
		this.Name = Name;
		this.Category = Category;
		this.Photo = Photo;
		this.Description = Description;
		this.Address = Address;
		this.Pincode = Pincode;
		this.ContactNumber = ContactNumber;
		this.AlternateNumber = AlternateNumber;
		this.Email = Email;
		this.Website = Website;
	}

	// Set & Get TypeId
	public void setTypeId(String TypeId) {
		this.TypeId = TypeId;
	}

	public String getTypeId() {
		return TypeId;
	}

	// Set & Get Name...
	public void setName(String Name) {
		this.Name = Name;
	}

	public String getName() {
		return Name;
	}

	// Set & Get Category...
	public void setCategory(String Category) {
		this.Category = Category;
	}

	public String getCategory() {
		return Category;
	}

	// Set & Get Photo
	public void setPhoto(String Photo) {
		this.Photo = Photo;
	}

	public String getPhoto() {
		return Photo;
	}

	// Set & Get Description
	public void setDescription(String Description) {
		this.Description = Description;
	}

	public String getDescription() {
		return Description;
	}

	// Set & Get Address
	public void setAddress(String Address) {
		this.Address = Address;
	}

	public String getAddress() {
		return Address;
	}

	// Set & Get Pincode
	public void setPincode(String Pincode) {
		this.Pincode = Pincode;
	}

	public String getPincode() {
		return Pincode;
	}

	// Set & Get ContactNumber
	public void setContactNumber(String ContactNumber) {
		this.ContactNumber = ContactNumber;
	}

	public String getContactNumber() {
		return ContactNumber;
	}

	// Set & Get AlternateNumber...
	public void setAlternateNumber(String AlternateNumber) {
		this.AlternateNumber = AlternateNumber;
	}

	public String getAlternateNumber() {
		return AlternateNumber;
	}

	// Set & Get Email...
	public void setEmail(String Email) {
		this.Email = Email;
	}

	public String getEmail() {
		return Email;
	}

	// Set & Get Website
	public void setWebsite(String Website) {
		this.Website = Website;
	}

	public String getWebsite() {
		return Website;
	}
}