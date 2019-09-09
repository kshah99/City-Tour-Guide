package com.mytestbuddy.citytourguide;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import com.actionbarsherlock.view.Menu;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends SherlockFragmentActivity {

	FrameLayout frame;
	ImageView imgRight;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList_Right;
	private ActionBarDrawerToggle mDrawerToggle;
	private ActionBar actionBar_Right;

	ArrayList<NavDrawerItem> navDrawerItems;
	private String[] mFragmentTitles;
	private TypedArray navMenuIcons;

	private NavDrawerListAdapter adapter;

	int UserCheck = 0;
	String UserId = "";

	String WebsiteURL = null;
	CookieManager cm = null;
	FragmentManager fm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		fm = getSupportFragmentManager();
		
		WebsiteURL = getResources().getString(R.string.website_url);

		mFragmentTitles = getResources().getStringArray(
				R.array.nav_drawer_items);
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		frame = (FrameLayout) findViewById(R.id.frame);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_Right = (ListView) findViewById(R.id.list_slidermenu);

		UserId = UserCookie.GetCookies(getApplicationContext());

		LayoutParams viewFlowLayout = mDrawerList_Right.getLayoutParams();
		float scale = getApplicationContext().getResources()
				.getDisplayMetrics().density;

		if (CheckDevice.isTablet(MainActivity.this)) {

			viewFlowLayout.width = (int) (310 * scale + 0.5f);
			mDrawerList_Right.setLayoutParams(viewFlowLayout);
		} else {
			viewFlowLayout.width = (int) (235 * scale + 0.5f);
			mDrawerList_Right.setLayoutParams(viewFlowLayout);
		}

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// Right Side Menus...
		// Home...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// About Us...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		// Attraction...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Hotel...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Restaurant...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		// Transportation...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[5], navMenuIcons
				.getResourceId(5, -1)));
		// My Profile...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[6], navMenuIcons
				.getResourceId(6, -1)));
		// Feedback...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[7], navMenuIcons
				.getResourceId(7, -1)));
		// Contact Us...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[8], navMenuIcons
				.getResourceId(8, -1)));
		// Rate this App...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[9], navMenuIcons
				.getResourceId(9, -1)));
		// Share this App...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[10], navMenuIcons
				.getResourceId(10, -1)));
		// CTG on Play Store...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[11], navMenuIcons
				.getResourceId(11, -1)));
		// Logout...
		navDrawerItems.add(new NavDrawerItem(mFragmentTitles[12], navMenuIcons
				.getResourceId(12, -1)));
		//

		navMenuIcons.recycle();

		actionBar_Right = getSupportActionBar();
		actionBar_Right.setCustomView(R.layout.right_menu);
		actionBar_Right.setDisplayShowTitleEnabled(true);
		actionBar_Right.setDisplayShowCustomEnabled(true);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer_left, R.string.app_name, R.string.app_name);
		mDrawerToggle.setDrawerIndicatorEnabled(false);

		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList_Right.setAdapter(adapter);

		imgRight = (ImageView) findViewById(R.id.imgRightMenu);
		imgRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (mDrawerLayout.isDrawerOpen(mDrawerList_Right)) {
					mDrawerLayout.closeDrawer(mDrawerList_Right);

					imgRight.setImageResource(R.drawable.ic_drawer_left);
				} else {
					mDrawerLayout.openDrawer(mDrawerList_Right);

					imgRight.setImageResource(R.drawable.ic_drawer_right);
				}
			}
		});

		mDrawerList_Right.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (CheckInterger.isInteger(UserId)) {
					selectItem(position);
				} else {
					cm.removeAllCookie();
					CookieSyncManager.getInstance().sync();

					finish();

					Intent openStartingPoint = new Intent(MainActivity.this,
							Login.class);
					startActivity(openStartingPoint);
				}
			}
		});

		if (CheckInterger.isInteger(UserId)) {
			if (savedInstanceState == null) {
				selectItem(0);
			}
		} else {
			cm.removeAllCookie();
			CookieSyncManager.getInstance().sync();

			finish();

			Intent openStartingPoint = new Intent(MainActivity.this,
					Login.class);
			startActivity(openStartingPoint);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void selectItem(int position) {
		Fragment newFragment = new Home();

		switch (position) {
		case 0:
			Fragment mapFragment = fm.findFragmentById(R.id.google_map);
			if (mapFragment != null) {
				fm.beginTransaction().remove(mapFragment).commit();
			}

			newFragment = new Home();
			break;
		case 1:
			newFragment = new AboutUs();
			break;
		case 2:
			newFragment = new CommonList();

			Bundle bundle1 = new Bundle();
			bundle1.putString("Type", "Attraction");
			newFragment.setArguments(bundle1);

			break;
		case 3:
			newFragment = new CommonList();

			Bundle bundle2 = new Bundle();
			bundle2.putString("Type", "Hotel");
			newFragment.setArguments(bundle2);
			break;
		case 4:
			newFragment = new CommonList();

			Bundle bundle3 = new Bundle();
			bundle3.putString("Type", "Restaurant");
			newFragment.setArguments(bundle3);
			break;
		case 5:
			newFragment = new CommonList();

			Bundle bundle4 = new Bundle();
			bundle4.putString("Type", "Transportation");
			newFragment.setArguments(bundle4);
			break;
		case 6:
			newFragment = new MyProfile();
			break;
		case 7:
			newFragment = new Feedback();
			break;
		case 8:
			newFragment = new ContactUs();
			break;
		case 9:
			RateThisApp();
			break;
		case 10:
			ShareThisApp();
			break;
		case 11:
			MytestbuddyOnPlayStore();
			break;
		case 12:
			newFragment = new Logout();
			break;
		}

		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
		frame.removeAllViews();

		fm.beginTransaction().replace(R.id.frame, newFragment).commit();

		mDrawerList_Right.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList_Right);

		imgRight.setImageResource(R.drawable.ic_drawer_left);
	}

	private void MytestbuddyOnPlayStore() {

		Uri uri = Uri
				.parse("https://play.google.com/store/apps/developer?id=Mytestbuddy");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private void RateThisApp() {

		Uri uri = Uri
				.parse("https://play.google.com/store/apps/details?id=com.mytestbuddy.citytourguide");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private void ShareThisApp() {

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_app));
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}