package com.mytestbuddy.citytourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;

public class Logout extends Fragment {

	FrameLayout frame;

	FragmentManager fm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.logout, container, false);

		CookieSyncManager.createInstance(getActivity());
		CookieManager cm = CookieManager.getInstance();
		cm.removeAllCookie();
		CookieSyncManager.getInstance().sync();

		getActivity().finish();

		Intent openLogin = new Intent(getActivity(), Login.class);
		startActivity(openLogin);

		return rootView;
	}
}
