package com.mytestbuddy.citytourguide;

public class CheckInterger {

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}