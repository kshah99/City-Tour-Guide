package com.mytestbuddy.citytourguide;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommonListLazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public CommonListLazyAdapter(Activity a,
			ArrayList<HashMap<String, String>> d) {

		activity = a;
		data = d;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.commonlist_listview, null);

		TextView tvName = (TextView) vi.findViewById(R.id.tvName);
		ImageView imgPhoto = (ImageView) vi.findViewById(R.id.imgPhoto);
		TextView tvCategory = (TextView) vi.findViewById(R.id.tvCategory);

		HashMap<String, String> commonData = new HashMap<String, String>();
		commonData = data.get(position);

		tvName.setText(commonData.get("Name"));
		imageLoader.DisplayImage(
				activity.getResources().getString(R.string.website_url)
						+ commonData.get("Photo"), imgPhoto);
		tvCategory.setText(commonData.get("Category"));

		return vi;
	}
}