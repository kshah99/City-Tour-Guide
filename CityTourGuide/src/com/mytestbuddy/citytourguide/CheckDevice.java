package com.mytestbuddy.citytourguide;

import android.content.Context;
import android.content.res.Configuration;

public class CheckDevice {

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}