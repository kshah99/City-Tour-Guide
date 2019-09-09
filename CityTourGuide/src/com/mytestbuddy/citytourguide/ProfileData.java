package com.mytestbuddy.citytourguide;

public class ProfileData {

	private String FirstName;
	private String LastName;
	private String Email;
	private String Photo;
	private String Address;
	private String Pincode;
	private String ContactNumber;
	private String BirthDate;
	private String Gender;
	private String Country;
	private String State;
	private String City;

	public ProfileData(String FirstName, String LastName, String Email,
			String Photo, String Address, String Pincode, String ContactNumber,
			String BirthDate, String Gender, String Country, String State,
			String City) {
		this.FirstName = FirstName;
		this.LastName = LastName;
		this.Email = Email;
		this.Photo = Photo;
		this.Address = Address;
		this.Pincode = Pincode;
		this.ContactNumber = ContactNumber;
		this.BirthDate = BirthDate;
		this.Gender = Gender;
		this.Country = Country;
		this.State = State;
		this.City = City;
	}

	// Set & Get FirstName...
	public void setFirstName(String FirstName) {
		this.FirstName = FirstName;
	}

	public String getFirstName() {
		return FirstName;
	}

	// Set & Get LastName...
	public void setLastName(String LastName) {
		this.LastName = LastName;
	}

	public String getLastName() {
		return LastName;
	}

	// Set & Get Email...
	public void setEmail(String Email) {
		this.Email = Email;
	}

	public String getEmail() {
		return Email;
	}

	// Set & Get Photo...
	public void setPhoto(String Photo) {
		this.Photo = Photo;
	}

	public String getPhoto() {
		return Photo;
	}

	// Set & Get Address...
	public void setAddress(String Address) {
		this.Address = Address;
	}

	public String getAddress() {
		return Address;
	}

	// Set & Get Pincode...
	public void setPincode(String Pincode) {
		this.Pincode = Pincode;
	}

	public String getPincode() {
		return Pincode;
	}

	// Set & Get ContactNumber...
	public void setContactNumber(String ContactNumber) {
		this.ContactNumber = ContactNumber;
	}

	public String getContactNumber() {
		return ContactNumber;
	}

	// Set & Get Gender...
	public void setGender(String Gender) {
		this.Gender = Gender;
	}

	public String getGender() {
		return Gender;
	}

	// Set & Get BirthDate...
	public void setBirthDate(String BirthDate) {
		this.BirthDate = BirthDate;
	}

	public String getBirthDate() {
		return BirthDate;
	}

	// Set & Get Country...
	public void setCountry(String Country) {
		this.Country = Country;
	}

	public String getCountry() {
		return Country;
	}

	// Set & Get State...
	public void setState(String State) {
		this.State = State;
	}

	public String getState() {
		return State;
	}

	// Set & Get City...
	public void setCity(String City) {
		this.City = City;
	}

	public String getCity() {
		return City;
	}

}
