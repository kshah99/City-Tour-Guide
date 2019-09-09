package com.mytestbuddy.citytourguide;

public class LocationData {

	private String Name;
	private String Address;
	private String Latitude;
	private String Longitude;

	public LocationData(String Name, String Address, String Latitude,
			String Longitude) {
		this.Name = Name;
		this.Address = Address;
		this.Latitude = Latitude;
		this.Longitude = Longitude;
	}

	// get & set Name
	public void setName(String Name) {
		this.Name = Name;
	}

	public String getName() {
		return Name;
	}

	// get &set Address
	public void setAddress(String Address) {
		this.Address = Address;
	}

	public String getAddress() {
		return Address;
	}

	// get &set Latitude
	public void setLatitude(String Latitude) {
		this.Latitude = Latitude;
	}

	public String getLatitude() {
		return Latitude;
	}

	// get &set Longitude
	public void setLongitude(String Longitude) {
		this.Longitude = Longitude;
	}

	public String getLongitude() {
		return Longitude;
	}
}