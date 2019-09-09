package com.mytestbuddy.citytourguide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Notification extends Fragment {

	View flMain;
	View wvNotification;
	View pbNotification;

	String UserId;
	String Message;

	String WebsiteURL = null;

	CookieManager cm = null;
	FragmentManager fm = null;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.notification, container,
				false);

		WebsiteURL = getResources().getString(R.string.website_url);

		Bundle bundle = this.getArguments();
		Message = bundle.getString("Message");

		fm = getActivity().getSupportFragmentManager();

		flMain = rootView.findViewById(R.id.frame);
		wvNotification = rootView.findViewById(R.id.wvNotification);
		pbNotification = rootView.findViewById(R.id.pbNotification);

		String cookies;
		CookieSyncManager.createInstance(getActivity());
		cm = CookieManager.getInstance();
		cm.setAcceptCookie(true);
		CookieSyncManager.getInstance().sync();

		if (cm.getCookie("" + WebsiteURL + "") != null) {
			cookies = cm.getCookie("" + WebsiteURL + "").toString();
			int index = cookies.indexOf("MTBCSATUserId=");
			int Start = index + ("MTBCSATUserId=".length());
			int endIndex = cookies.indexOf(";", index);

			if (endIndex != -1) {
				UserId = cookies.substring(Start, endIndex);
			} else {
				UserId = cookies.substring(Start);
			}
		}

		if (CheckInternet.isConnectingToInternet(getActivity())) {
			((WebView) wvNotification).setWebViewClient(new myWebClient());
			((WebView) wvNotification).getSettings().setJavaScriptEnabled(true);
			((WebView) wvNotification).loadUrl(Message);
		} else {

			pbNotification.setVisibility(View.GONE);

			Toast.makeText(
					getActivity(),
					"Network Unavailable!\nPlease Check Your Internet Connection.",
					Toast.LENGTH_LONG).show();
		}

		wvNotification.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					if (CheckInterger.isInteger(UserId)) {
						for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
							fm.popBackStack();
						}

						((FrameLayout) flMain).removeAllViews();

						Home home = new Home();
						Bundle bundle = new Bundle();
						bundle.putString("Back", "Home");
						home.setArguments(bundle);

						fm.beginTransaction().add(R.id.frame, home).commit();
					} else {
						cm.removeAllCookie();
						CookieSyncManager.getInstance().sync();

						for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
							fm.popBackStack();
						}

						((FrameLayout) flMain).removeAllViews();

						getActivity().finish();

						Intent openStartingPoint = new Intent(getActivity(),
								Login.class);
						startActivity(openStartingPoint);
					}

					return true;
				}

				return false;
			}
		});

		return rootView;
	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			pbNotification.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			pbNotification.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {

		super.onResume();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					if (CheckInterger.isInteger(UserId)) {
						for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
							fm.popBackStack();
						}

						((FrameLayout) flMain).removeAllViews();

						Home home = new Home();
						Bundle bundle = new Bundle();
						bundle.putString("Back", "Home");
						home.setArguments(bundle);

						fm.beginTransaction().add(R.id.frame, home).commit();
					} else {
						cm.removeAllCookie();
						CookieSyncManager.getInstance().sync();

						getActivity().finish();

						Intent openStartingPoint = new Intent(getActivity(),
								Login.class);
						startActivity(openStartingPoint);
					}

					return true;
				}

				return false;
			}
		});
	}
}
