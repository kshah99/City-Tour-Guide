package com.mytestbuddy.citytourguide;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class UserCookie {

	static String Website_URL = "";
	static String UserId = "";
	static CookieManager cm = null;

	public static String GetCookies(Context context) {

		Website_URL = context.getResources().getString(R.string.website_url);

		String cookies;
		CookieSyncManager.createInstance(context);
		cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
		CookieSyncManager.getInstance().sync();

		if (cm.getCookie("" + Website_URL + "") != null) {
			cookies = cm.getCookie("" + Website_URL + "").toString();
			int index = cookies.indexOf("CTGUserId=");
			int Start = index + ("CTGUserId=".length());
			int endIndex = cookies.indexOf(";", index);

			if (endIndex != -1) {
				UserId = cookies.substring(Start, endIndex);
			} else {
				UserId = cookies.substring(Start);
			}
		}

		return UserId;
	}
}