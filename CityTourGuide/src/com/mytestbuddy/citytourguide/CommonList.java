package com.mytestbuddy.citytourguide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CommonList extends Fragment {

	FrameLayout frame;
	EditText etSearch;
	ImageButton imgbtnSearch;
	ListView lvCommonList;
	TextView tvNoData;
	Spinner spnCategory;

	String Type = "";
	String Category = "0";
	String Search = "";
	String UserId;
	int UserCheck = 0;

	String WebsiteURL = null;
	String WebServiceURL = null;
	String NAMESPACE = null;
	String METHOD_NAME = "GetCommonList";
	String SOAP_ACTION = null;
	String METHOD_NAME_CATEGORY = "GetCommonCategory";
	String SOAP_ACTION_CATEGORY = null;

	private SoapObject request;
	private PropertyInfo pi_Type;
	private PropertyInfo pi_Category;
	private PropertyInfo pi_Search;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	CommonListData[] commonListData;
	CommonCategoryData[] commonCategoryData;

	ArrayAdapter<String> adapterCategory;

	List<String> categoryId = new ArrayList<String>();
	List<String> categoryName = new ArrayList<String>();

	CommonListLazyAdapter commonListlazyAdapter;
	ArrayList<HashMap<String, String>> artlstCommon = new ArrayList<HashMap<String, String>>();

	List<String> lstTypeId = new ArrayList<String>();

	FragmentManager fm = null;
	CookieManager cm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.commonlist, container, false);

		fm = getActivity().getSupportFragmentManager();

		WebsiteURL = getResources().getString(R.string.website_url);
		WebServiceURL = getResources().getString(R.string.webservice_url);
		NAMESPACE = getResources().getString(R.string.namespace);

		this.SOAP_ACTION = NAMESPACE + METHOD_NAME;
		this.SOAP_ACTION_CATEGORY = NAMESPACE + METHOD_NAME_CATEGORY;

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		etSearch = (EditText) rootView.findViewById(R.id.etSearch);
		imgbtnSearch = (ImageButton) rootView.findViewById(R.id.imgbtnSearch);
		lvCommonList = (ListView) rootView.findViewById(R.id.lvCommonList);
		tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);
		spnCategory = (Spinner) rootView.findViewById(R.id.spnCategory);

		if (CheckInterger.isInteger(UserId)) {

			UserCheck = 1;

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

		Bundle bundle = this.getArguments();
		if (bundle != null) {
			Type = bundle.getString("Type");

			getActivity().setTitle(Type);
		}

		new FetchCategoryData().execute();

		if (UserCheck == 1 && !Type.equals("")) {

			etSearch.setOnTouchListener(new View.OnTouchListener() {

				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					etSearch.setFocusable(true);
					etSearch.setFocusableInTouchMode(true);
					return false;
				}
			});

			etSearch.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub

					if (event.getAction() == KeyEvent.ACTION_UP
							&& keyCode == KeyEvent.KEYCODE_BACK) {

						new FetchCommonListData().cancel(true);

						for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
							fm.popBackStack();
						}

						frame.removeAllViews();

						fm.beginTransaction().add(R.id.frame, new Home())
								.commit();

						return true;
					}

					return false;
				}
			});

			lvCommonList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (CheckInternet.isConnectingToInternet(getActivity())) {
						for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
							fm.popBackStack();
						}

						frame.removeAllViews();

						CommonInfo commonInfo = new CommonInfo();

						Bundle bundle = new Bundle();
						bundle.putString("TypeId", lstTypeId.get(position));
						bundle.putString("Type", Type);

						commonInfo.setArguments(bundle);

						fm.beginTransaction().replace(R.id.frame, commonInfo)
								.commit();
					} else {

						Toast.makeText(
								getActivity(),
								"Network Unavailable!\nPlease Check Your Internet Connection.",
								Toast.LENGTH_LONG).show();
					}
				}
			});

			spnCategory.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {

					if (position > 0) {
						Category = categoryId.get(position);

					} else {
						Category = "0";
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
					// your code here
				}
			});

			imgbtnSearch.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					Search = etSearch.getText().toString().trim();

					new FetchCommonListData().execute();
				}
			});
		}

		return rootView;
	}

	private class FetchCategoryData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getActivity(), "", "Loading...", true,
					true, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub

							CancelDialog();
						}
					});
			dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(String... params) {

			if (!isCancelled() && isAdded()) {
				request = new SoapObject(NAMESPACE, METHOD_NAME_CATEGORY);

				pi_Type = new PropertyInfo();
				pi_Type.setName("Type");
				pi_Type.setValue(Type);
				pi_Type.setType(String.class);
				request.addProperty(pi_Type);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						WebServiceURL);
				try {
					androidHttpTransport.call(SOAP_ACTION_CATEGORY, envp);
					SoapPrimitive response = (SoapPrimitive) envp.getResponse();
					Response = response.toString();

				} catch (Exception e) {
					// Response = e.getMessage().toString();
					e.printStackTrace();
				}
			}

			return Response;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (!isCancelled() && isAdded()) {

				if (result != null && !result.equals("")) {
					commonCategoryData = gson.fromJson(result,
							CommonCategoryData[].class);

					List<CommonCategoryData> lList = Arrays
							.asList(commonCategoryData);
					CommonCategoryData objCategoryList = null;

					if (lList.size() > 0) {
						if (lList.get(0).getCategory().equals("Server Error")) {
							lvCommonList.setVisibility(View.GONE);
							tvNoData.setVisibility(View.VISIBLE);

							tvNoData.setText("Server Error. Please try again later!");
						} else {

							categoryId.clear();
							categoryName.clear();

							categoryId.add("0");
							categoryName.add("Select Category");

							for (int i = 0; i < lList.size(); i++) {
								objCategoryList = lList.get(i);

								categoryName.add(objCategoryList.getCategory());
								categoryId.add(objCategoryList.getCategoryId());
							}

							adapterCategory = new ArrayAdapter<String>(
									getActivity(), R.layout.spinner_view,
									categoryName);

							spnCategory.setAdapter(adapterCategory);

							new FetchCommonListData().execute();
						}
					} else {

						// No Data..
						lvCommonList.setVisibility(View.GONE);
						tvNoData.setVisibility(View.VISIBLE);

						tvNoData.setText("No Data Found.");
					}
				} else {

					lvCommonList.setVisibility(View.GONE);
					tvNoData.setVisibility(View.VISIBLE);

					tvNoData.setText("Something is Wrong.");
				}
			}
		}
	}

	private class FetchCommonListData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getActivity(), "", "Loading...", true,
					true, new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub

							CancelDialog();
						}
					});
			dialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected String doInBackground(String... params) {

			if (!isCancelled() && isAdded()) {
				request = new SoapObject(NAMESPACE, METHOD_NAME);

				pi_Type = new PropertyInfo();
				pi_Type.setName("Type");
				pi_Type.setValue(Type);
				pi_Type.setType(String.class);
				request.addProperty(pi_Type);

				pi_Category = new PropertyInfo();
				pi_Category.setName("Category");
				pi_Category.setValue(Category);
				pi_Category.setType(String.class);
				request.addProperty(pi_Category);

				pi_Search = new PropertyInfo();
				pi_Search.setName("Search");
				pi_Search.setValue(Search);
				pi_Search.setType(String.class);
				request.addProperty(pi_Search);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						WebServiceURL);
				try {
					androidHttpTransport.call(SOAP_ACTION, envp);
					SoapPrimitive response = (SoapPrimitive) envp.getResponse();
					Response = response.toString();

				} catch (Exception e) {
					// Response = e.getMessage().toString();
					e.printStackTrace();
				}
			}

			return Response;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (!isCancelled() && isAdded()) {

				lvCommonList.setVisibility(View.VISIBLE);
				tvNoData.setVisibility(View.GONE);

				lstTypeId.clear();
				artlstCommon.clear();
				commonListlazyAdapter = null;

				if (result != null && !result.equals("")) {
					commonListData = gson.fromJson(result,
							CommonListData[].class);

					List<CommonListData> lList = Arrays.asList(commonListData);
					CommonListData objCommonList = null;

					if (lList.size() > 0) {
						if (lList.get(0).getListId().equals("Server Error")) {
							lvCommonList.setVisibility(View.GONE);
							tvNoData.setVisibility(View.VISIBLE);

							tvNoData.setText("Server Error. Please try again later!");
						} else {

							HashMap<String, String> map = null;
							for (int i = 0; i < lList.size(); i++) {
								objCommonList = lList.get(i);

								lstTypeId.add(objCommonList.getListId());

								map = new HashMap<String, String>();
								map.put("Name", objCommonList.getName());
								map.put("Description",
										objCommonList.getDescription());
								map.put("Photo", objCommonList.getPhoto()
										.replace(" ", "%20"));
								map.put("Category", objCommonList.getCategory());

								artlstCommon.add(map);
							}

							commonListlazyAdapter = new CommonListLazyAdapter(
									getActivity(), artlstCommon);
							lvCommonList.setAdapter(commonListlazyAdapter);
						}
					} else {

						// No Data..
						lvCommonList.setVisibility(View.GONE);
						tvNoData.setVisibility(View.VISIBLE);

						tvNoData.setText("No Data Found.");
					}
				} else {

					lvCommonList.setVisibility(View.GONE);
					tvNoData.setVisibility(View.VISIBLE);

					tvNoData.setText("Something is Wrong.");
				}
			}
		}
	}

	public void CancelDialog() {

		new FetchCommonListData().cancel(true);
		new FetchCategoryData().cancel(true);

		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		frame.removeAllViews();

		fm.beginTransaction().add(R.id.frame, new Home()).commit();
	}

	@Override
	public void onResume() {

		super.onResume();

		etSearch.setFocusable(false);
		etSearch.setFocusableInTouchMode(false);
		etSearch.clearFocus();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					new FetchCommonListData().cancel(true);

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