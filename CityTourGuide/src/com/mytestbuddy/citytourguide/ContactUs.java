package com.mytestbuddy.citytourguide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ContactUs extends Fragment {

	View flMain;
	View tvContactSuport;
	View tvEmailSuport;

	FragmentManager fm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.contact_us, container, false);

		fm = getActivity().getSupportFragmentManager();

		flMain = rootView.findViewById(R.id.frame);
		tvContactSuport = rootView.findViewById(R.id.tvContactSuport);
		tvEmailSuport = rootView.findViewById(R.id.tvEmailSuport);

		tvContactSuport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent openDialler = new Intent(Intent.ACTION_DIAL);
				openDialler.setData(Uri.parse("tel:+91 0265-6599822"));
				startActivity(openDialler);
			}
		});

		tvEmailSuport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				emailIntent.setType("plain/html");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "support@mytestbuddy.com" });
				startActivity(Intent.createChooser(emailIntent, null));
			}
		});

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

					((FrameLayout) flMain).removeAllViews();

					Home home = new Home();
					Bundle bundle = new Bundle();
					bundle.putString("Back", "Home");
					home.setArguments(bundle);

					fm.beginTransaction().add(R.id.frame, home).commit();

					return true;
				}

				return false;
			}
		});
	}
}