package com.mytestbuddy.citytourguide;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentLazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public CommentLazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {

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
			vi = inflater.inflate(R.layout.comment_view, null);

		TextView tvName = (TextView) vi.findViewById(R.id.tvName);
		ImageView imgUserPhoto = (ImageView) vi.findViewById(R.id.imgUserPhoto);
		TextView tvDateTime = (TextView) vi.findViewById(R.id.tvDateTime);
		TextView tvComment = (TextView) vi.findViewById(R.id.tvComment);

		HashMap<String, String> comment = new HashMap<String, String>();
		comment = data.get(position);

		tvName.setText(comment.get("Name"));
		tvDateTime.setText(comment.get("DateTime"));
		imageLoader.DisplayImage(
				activity.getResources().getString(R.string.website_url)
						+ comment.get("Image"), imgUserPhoto);
		tvComment.setText(Html.fromHtml(comment.get("Comment")));

		return vi;
	}
}