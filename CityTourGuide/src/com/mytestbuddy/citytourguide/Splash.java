package com.mytestbuddy.citytourguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle showSplash) {
		// TODO Auto-generated method stub
		super.onCreate(showSplash);
		setContentView(R.layout.splash);

		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					Intent intLogin = new Intent(Splash.this, Splash2.class);
					startActivity(intLogin);
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