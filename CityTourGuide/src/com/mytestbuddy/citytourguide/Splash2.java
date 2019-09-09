package com.mytestbuddy.citytourguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class Splash2 extends Activity {

	String WebsiteURL = null;
	String cookies;
	String UserId;

	CookieManager cm;

	@Override
	protected void onCreate(Bundle showSplash) {
		// TODO Auto-generated method stub
		super.onCreate(showSplash);
		setContentView(R.layout.splash2);

		WebsiteURL = getResources().getString(R.string.website_url);

		CookieSyncManager.createInstance(this);
		cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
		CookieSyncManager.getInstance().sync();

		if (cm.getCookie("" + WebsiteURL + "") != null) {
			cookies = cm.getCookie("" + WebsiteURL + "").toString();
			int index = cookies.indexOf("CTGUserId=");
			int Start = index + ("CTGUserId=".length());
			int endIndex = cookies.indexOf(";", index);

			if (endIndex != -1) {
				UserId = cookies.substring(Start, endIndex);
			} else {
				UserId = cookies.substring(Start);
			}
		}

		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (cookies == null) {

						Intent intLogin = new Intent(Splash2.this, Login.class);
						startActivity(intLogin);
					} else {

						if (CheckInterger.isInteger(UserId)) {
							Intent intHome = new Intent(Splash2.this,
									MainActivity.class);
							startActivity(intHome);
						} else {
							cm.removeAllCookie();
							CookieSyncManager.getInstance().sync();

							finish();

							Intent intLogin = new Intent(Splash2.this,
									Login.class);
							startActivity(intLogin);
						}
					}
				}
			}
		};

		timer.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}