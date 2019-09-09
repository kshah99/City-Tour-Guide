package com.mytestbuddy.citytourguide;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListViewSize {

	public static void getListViewSize(ListView myListView, Context context) {
		ListAdapter myListAdapter = myListView.getAdapter();

		if (myListAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int size = 0; size < myListAdapter.getCount(); size++) {

			View listItem = myListAdapter.getView(size, null, myListView);
			if (listItem instanceof ViewGroup)
				listItem.setLayoutParams(new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT));

			float scale = context.getResources().getDisplayMetrics().density;
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			@SuppressWarnings("deprecation")
			int screenWidth = display.getWidth();
			int listViewWidth = screenWidth - (int) (60 * scale + 0.5f);
			int widthSpec = MeasureSpec.makeMeasureSpec(listViewWidth,
					MeasureSpec.AT_MOST);
			listItem.measure(widthSpec, 0);

			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight
				+ (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
		myListView.setLayoutParams(params);
		myListView.requestLayout();
	}
}