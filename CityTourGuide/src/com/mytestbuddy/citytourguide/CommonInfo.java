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

import com.androidquery.AQuery;
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
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CommonInfo extends Fragment {

	FrameLayout frame;
	TextView tvName, tvCategory, tvDescription, tvAddress, tvPincode,
			tvContactNumber, tvAlternateNumber, tvEmail, tvWebsite;
	TableLayout tblContactDetails;
	TableRow trAddress, trPincode, trContactNumber, trAlternateNumber, trEmail,
			trWebsite;
	ListView lvCommonComment;
	EditText etComment;
	RatingBar rbRating;
	Button btnComment;
	ImageView imgPhoto;

	String UserId;
	String Type;
	String TypeId;
	String Comment;
	String Rating;

	String Website_URL = "";
	String Webservice_URL = "";
	String NameSpace = "";
	String Method_Name1 = "GetCommonInfo";
	String SOAP_ACTION1 = "";
	String Method_Name2 = "AddUserReview";
	String SOAP_ACTION2 = "";
	String Method_Name3 = "GetUserReview";
	String SOAP_ACTION3 = "";

	PropertyInfo pi_UserId;
	PropertyInfo pi_Type;
	PropertyInfo pi_TypeId;
	PropertyInfo pi_Comment;
	PropertyInfo pi_Rating;

	SoapObject request;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	CommonInfoData[] commonInfoData;
	ShowMessage[] Message;
	UserReviewData[] userReviewData;

	CommentLazyAdapter commentLazyAdapter;
	ArrayList<HashMap<String, String>> artlstComment = new ArrayList<HashMap<String, String>>();

	FragmentManager fm = null;
	CookieManager cm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.common_info, container, false);

		fm = getActivity().getSupportFragmentManager();

		Website_URL = getResources().getString(R.string.website_url);
		Webservice_URL = getResources().getString(R.string.webservice_url);
		NameSpace = getResources().getString(R.string.namespace);

		SOAP_ACTION1 = NameSpace + Method_Name1;
		SOAP_ACTION2 = NameSpace + Method_Name2;
		SOAP_ACTION3 = NameSpace + Method_Name3;

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		tvName = (TextView) rootView.findViewById(R.id.tvName);
		tvCategory = (TextView) rootView.findViewById(R.id.tvCategory);
		imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);
		tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
		tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
		tvPincode = (TextView) rootView.findViewById(R.id.tvPincode);
		tvContactNumber = (TextView) rootView
				.findViewById(R.id.tvContactNumber);
		tvAlternateNumber = (TextView) rootView
				.findViewById(R.id.tvAlternateNumber);
		tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
		tvWebsite = (TextView) rootView.findViewById(R.id.tvWebsite);

		tblContactDetails = (TableLayout) rootView
				.findViewById(R.id.tblContactDetails);
		trAddress = (TableRow) rootView.findViewById(R.id.trAddress);
		trPincode = (TableRow) rootView.findViewById(R.id.trPincode);
		trContactNumber = (TableRow) rootView
				.findViewById(R.id.trContactNumber);
		trAlternateNumber = (TableRow) rootView
				.findViewById(R.id.trAlternateNumber);
		trEmail = (TableRow) rootView.findViewById(R.id.trEmail);
		trWebsite = (TableRow) rootView.findViewById(R.id.trWebsite);

		lvCommonComment = (ListView) rootView
				.findViewById(R.id.lvCommonComment);
		etComment = (EditText) rootView.findViewById(R.id.etComment);
		rbRating = (RatingBar) rootView.findViewById(R.id.rbRating);
		btnComment = (Button) rootView.findViewById(R.id.btnComment);

		if (CheckInterger.isInteger(UserId)) {

			Bundle bundle = this.getArguments();
			if (bundle != null) {
				Type = bundle.getString("Type");
				TypeId = bundle.getString("TypeId");

				getActivity().setTitle(Type);
			}

			new CommonInfoTask().execute();

		} else {
			cm.removeAllCookie();
			CookieSyncManager.getInstance().sync();

			getActivity().finish();

			Intent intLogin = new Intent(getActivity(), Login.class);
			startActivity(intLogin);
		}

		etComment.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				etComment.setFocusable(true);
				etComment.setFocusableInTouchMode(true);
				return false;
			}
		});

		etComment.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					CancelDialog();

					return true;
				}

				return false;
			}
		});

		btnComment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Comment = etComment.getText().toString().trim();
				Rating = String.valueOf(rbRating.getRating());

				new AddUserReview().execute();
			}
		});

		return rootView;
	}

	private class CommonInfoTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			dialog = ProgressDialog.show(getActivity(), "", "Please Wait...",
					true, true, new OnCancelListener() {

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
			// TODO Auto-generated method stub

			if (!isCancelled() && isAdded()) {
				request = new SoapObject(NameSpace, Method_Name1);

				pi_Type = new PropertyInfo();
				pi_Type.setName("Type");
				pi_Type.setValue(Type);
				pi_Type.setType(String.class);
				request.addProperty(pi_Type);

				pi_TypeId = new PropertyInfo();
				pi_TypeId.setName("TypeId");
				pi_TypeId.setValue(TypeId);
				pi_TypeId.setType(String.class);
				request.addProperty(pi_TypeId);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						Webservice_URL);
				try {
					androidHttpTransport.call(SOAP_ACTION1, envp);
					SoapPrimitive response = (SoapPrimitive) envp.getResponse();
					Response = response.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return Response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (!isCancelled() && isAdded()) {

				if (result != null && !result.equals("")) {
					commonInfoData = gson.fromJson(result,
							CommonInfoData[].class);
					List<CommonInfoData> lstCommonInfoData = Arrays
							.asList(commonInfoData);

					if (lstCommonInfoData.size() > 0) {
						CommonInfoData objCommonInfoData = lstCommonInfoData
								.get(0);

						getActivity().setTitle(objCommonInfoData.getName());

						tvName.setText(objCommonInfoData.getName());
						tvCategory.setText(objCommonInfoData.getCategory());
						tvDescription.setText(Html.fromHtml(objCommonInfoData
								.getDescription().replace("\n", "<br/>")));

						AQuery androidAQuery = new AQuery(getActivity());
						androidAQuery
								.id(imgPhoto)
								.progress(R.id.progress)
								.image(""
										+ Website_URL
										+ objCommonInfoData.getPhoto().replace(
												" ", "%20") + "");

						if (objCommonInfoData.getAddress().equals("")
								&& objCommonInfoData.getPincode().equals("")
								&& objCommonInfoData.getContactNumber().equals(
										"0")
								&& objCommonInfoData.getAlternateNumber()
										.equals("0")
								&& objCommonInfoData.getEmail().equals("")
								&& objCommonInfoData.getWebsite().equals("")) {

							tblContactDetails.setVisibility(View.GONE);
						} else {
							tblContactDetails.setVisibility(View.VISIBLE);

							if (objCommonInfoData.getAddress().equals("")) {
								trAddress.setVisibility(View.GONE);
							} else {
								tvAddress.setText(objCommonInfoData
										.getAddress());
							}
							if (objCommonInfoData.getPincode().equals("")) {
								trPincode.setVisibility(View.GONE);
							} else {
								tvPincode.setText(objCommonInfoData
										.getPincode());
							}
							if (objCommonInfoData.getContactNumber().equals("")
									|| objCommonInfoData.getContactNumber()
											.equals("0")) {
								trContactNumber.setVisibility(View.GONE);
							} else {
								tvContactNumber.setText(objCommonInfoData
										.getContactNumber());
							}
							if (objCommonInfoData.getAlternateNumber().equals(
									"")
									|| objCommonInfoData.getAlternateNumber()
											.equals("0")) {
								trAlternateNumber.setVisibility(View.GONE);
							} else {
								tvAlternateNumber.setText(objCommonInfoData
										.getAlternateNumber());
							}
							if (objCommonInfoData.getEmail().equals("")) {
								trEmail.setVisibility(View.GONE);
							} else {
								tvEmail.setText(objCommonInfoData.getEmail());
							}
							if (objCommonInfoData.getWebsite().equals("")) {
								trWebsite.setVisibility(View.GONE);
							} else {
								tvWebsite.setText(objCommonInfoData
										.getWebsite());
							}

							new GetUserReview().execute();
						}
					}
				}
			}
		}
	}

	private class AddUserReview extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			dialog = ProgressDialog.show(getActivity(), "", "Please Wait...",
					true, true, new OnCancelListener() {

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
			// TODO Auto-generated method stub

			if (!isCancelled() && isAdded()) {
				request = new SoapObject(NameSpace, Method_Name2);

				pi_UserId = new PropertyInfo();
				pi_UserId.setName("UserId");
				pi_UserId.setValue(UserId);
				pi_UserId.setType(String.class);
				request.addProperty(pi_UserId);

				pi_Type = new PropertyInfo();
				pi_Type.setName("Type");
				pi_Type.setValue(Type);
				pi_Type.setType(String.class);
				request.addProperty(pi_Type);

				pi_TypeId = new PropertyInfo();
				pi_TypeId.setName("TypeId");
				pi_TypeId.setValue(TypeId);
				pi_TypeId.setType(String.class);
				request.addProperty(pi_TypeId);

				pi_Comment = new PropertyInfo();
				pi_Comment.setName("Comment");
				pi_Comment.setValue(Comment);
				pi_Comment.setType(String.class);
				request.addProperty(pi_Comment);

				pi_Rating = new PropertyInfo();
				pi_Rating.setName("Rating");
				pi_Rating.setValue(Rating);
				pi_Rating.setType(String.class);
				request.addProperty(pi_Rating);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						Webservice_URL);
				try {
					androidHttpTransport.call(SOAP_ACTION2, envp);
					SoapPrimitive response = (SoapPrimitive) envp.getResponse();
					Response = response.toString();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return Response;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (!isCancelled() && isAdded()) {
				if (result != null && !result.equals("")) {
				}
			}
		}
	}

	private class GetUserReview extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			dialog = ProgressDialog.show(getActivity(), "", "Please Wait...",
					true, true, new OnCancelListener() {

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
				request = new SoapObject(NameSpace, Method_Name3);

				pi_Type = new PropertyInfo();
				pi_Type.setName("Type");
				pi_Type.setValue(Type);
				pi_Type.setType(String.class);
				request.addProperty(pi_Type);

				pi_TypeId = new PropertyInfo();
				pi_TypeId.setName("TypeId");
				pi_TypeId.setValue(TypeId);
				pi_TypeId.setType(String.class);
				request.addProperty(pi_TypeId);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						Webservice_URL);
				try {
					androidHttpTransport.call(SOAP_ACTION3, envp);
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

				lvCommonComment.setVisibility(View.VISIBLE);
				// tvNoData.setVisibility(View.GONE);

				artlstComment.clear();
				commentLazyAdapter = null;

				if (result != null && !result.equals("")) {
					userReviewData = gson.fromJson(result,
							UserReviewData[].class);

					List<UserReviewData> lList = Arrays.asList(userReviewData);
					UserReviewData objComment = null;

					if (lList.size() > 0) {
						if (lList.get(0).getName().equals("Server Error")) {
							lvCommonComment.setVisibility(View.GONE);
							// tvNoData.setVisibility(View.VISIBLE);

							// tvNoData.setText("Server Error. Please try again later!");
						} else {

							HashMap<String, String> map = null;
							for (int i = 0; i < lList.size(); i++) {
								objComment = lList.get(i);

								map = new HashMap<String, String>();
								map.put("Name", objComment.getName());
								map.put("Photo",
										objComment.getPhoto().replace(" ",
												"%20"));
								map.put("Comment", objComment.getComment());
								map.put("Rating", objComment.getRating());
								map.put("DateTime", objComment.getDateTime());

								artlstComment.add(map);
							}

							commentLazyAdapter = new CommentLazyAdapter(
									getActivity(), artlstComment);
							lvCommonComment.setAdapter(commentLazyAdapter);
							ListViewSize.getListViewSize(lvCommonComment,
									getActivity());
						}
					} else {

						// No Data..
						lvCommonComment.setVisibility(View.GONE);
						// tvNoData.setVisibility(View.VISIBLE);

						// tvNoData.setText("No Data Found.");
					}
				} else {

					lvCommonComment.setVisibility(View.GONE);
					// tvNoData.setVisibility(View.VISIBLE);

					// tvNoData.setText("Something is Wrong.");
				}
			}
		}
	}

	public void CancelDialog() {

		new CommonInfoTask().cancel(true);
		new AddUserReview().cancel(true);
		new GetUserReview().cancel(true);

		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		frame.removeAllViews();

		CommonList commonList = new CommonList();
		Bundle bundle = new Bundle();
		bundle.putString("Type", Type);
		commonList.setArguments(bundle);

		fm.beginTransaction().replace(R.id.frame, commonList).commit();
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

					new CommonInfoTask().cancel(true);
					new AddUserReview().cancel(true);
					new GetUserReview().cancel(true);

					for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
						fm.popBackStack();
					}

					frame.removeAllViews();

					CommonList commonList = new CommonList();
					Bundle bundle = new Bundle();
					bundle.putString("Type", Type);
					commonList.setArguments(bundle);

					fm.beginTransaction().replace(R.id.frame, commonList)
							.commit();

					return true;
				}
				return false;
			}
		});
	}
}