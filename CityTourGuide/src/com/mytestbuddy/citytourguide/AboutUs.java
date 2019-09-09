package com.mytestbuddy.citytourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AboutUs extends Fragment {

	FrameLayout frame;
	TextView tvWhoWeWre;
	TextView tvOurObjective;
	TextView tvWhatWeOffer;

	String UserId;

	CookieManager cm = null;
	FragmentManager fm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.about_us, container, false);

		getActivity().setTitle("About Us");

		fm = getActivity().getSupportFragmentManager();

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		tvWhoWeWre = (TextView) rootView.findViewById(R.id.tvWhoWeWre);
		tvOurObjective = (TextView) rootView.findViewById(R.id.tvOurObjective);
		tvWhatWeOffer = (TextView) rootView.findViewById(R.id.tvWhatWeOffer);
		tvWhoWeWre.setText(getResources().getString(R.string.whowearecontent));
		tvOurObjective.setText(getResources().getString(
				R.string.ourobjectivecontent));
		tvWhatWeOffer.setText(getResources().getString(
				R.string.whatweoffercontent));

		if (CheckInterger.isInteger(UserId)) {

		} else {
			cm.removeAllCookie();
			CookieSyncManager.getInstance().sync();

			for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
				fm.popBackStack();
			}

			frame.removeAllViews();

			getActivity().finish();

			Intent intLogin = new Intent(getActivity(), Login.class);
			startActivity(intLogin);
		}

		return rootView;
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

					for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
						fm.popBackStack();
					}

					frame.removeAllViews();

					fm.beginTransaction().add(R.id.frame, new Home()).commit();

					return true;
				}

				return false;
			}
		});
	}
}