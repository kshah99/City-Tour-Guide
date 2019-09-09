package com.mytestbuddy.citytourguide;

import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.androidquery.AQuery;
import com.google.gson.Gson;

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
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProfile extends Fragment {

	FrameLayout frame;
	ImageView imgPhoto;
	TextView tvFirstName, tvLastName, tvEmail, tvAddress, tvPincode,
			tvContactNumber, tvBirthDate, tvGender, tvCountry, tvState, tvCity;
	EditText etshare;
	String UserId;

	String Website_URL = "";
	String Webservice_URL = "";
	String NameSpace = "";
	String Method_Name = "GetProfile";
	String SOAP_ACTION = "";

	PropertyInfo pi_UserId;
	SoapObject request;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	ProfileData[] profileData;

	CookieManager cm = null;
	FragmentManager fm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.my_profile, container, false);

		getActivity().setTitle("My Profile");

		fm = getActivity().getSupportFragmentManager();

		Website_URL = getResources().getString(R.string.website_url);
		Webservice_URL = getResources().getString(R.string.webservice_url);
		NameSpace = getResources().getString(R.string.namespace);

		SOAP_ACTION = NameSpace + Method_Name;

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		tvFirstName = (TextView) rootView.findViewById(R.id.tvFirstName);
		tvLastName = (TextView) rootView.findViewById(R.id.tvLastName);
		tvEmail = (TextView) rootView.findViewById(R.id.tvEmail);
		tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
		tvPincode = (TextView) rootView.findViewById(R.id.tvPincode);
		tvContactNumber = (TextView) rootView
				.findViewById(R.id.tvContactNumber);
		tvBirthDate = (TextView) rootView.findViewById(R.id.tvBirthDate);
		tvGender = (TextView) rootView.findViewById(R.id.tvGender);
		tvCountry = (TextView) rootView.findViewById(R.id.tvCountry);
		tvState = (TextView) rootView.findViewById(R.id.tvState);
		tvCity = (TextView) rootView.findViewById(R.id.tvCity);
		imgPhoto = (ImageView) rootView.findViewById(R.id.imgPhoto);

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

		new Profile().execute();

		return rootView;
	}

	private class Profile extends AsyncTask<String, String, String> {

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
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			if (!isCancelled() && isAdded()) {
				request = new SoapObject(NameSpace, Method_Name);

				pi_UserId = new PropertyInfo();
				pi_UserId.setName("UserId");
				pi_UserId.setValue(UserId);
				pi_UserId.setType(String.class);
				request.addProperty(pi_UserId);

				SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envp.dotNet = true;
				envp.setOutputSoapObject(request);
				HttpTransportSE androidHttpTransport = new HttpTransportSE(
						Webservice_URL);

				try {
					androidHttpTransport.call(SOAP_ACTION, envp);
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
					profileData = gson.fromJson(result, ProfileData[].class);
					List<ProfileData> lstProfileData = Arrays
							.asList(profileData);

					if (lstProfileData.size() > 0) {
						ProfileData objProfile = lstProfileData.get(0);

						tvFirstName.setText(objProfile.getFirstName());
						tvLastName.setText(objProfile.getLastName());
						tvEmail.setText(objProfile.getEmail());
						tvAddress.setText(objProfile.getAddress());
						tvPincode.setText(objProfile.getPincode());
						tvContactNumber.setText(objProfile.getContactNumber());
						tvGender.setText(objProfile.getGender());
						tvBirthDate.setText(objProfile.getBirthDate());
						tvCountry.setText(objProfile.getCountry());
						tvState.setText(objProfile.getState());
						tvCity.setText(objProfile.getCity());

						AQuery androidAQuery = new AQuery(getActivity());
						androidAQuery
								.id(imgPhoto)
								.progress(R.id.progress)
								.image(""
										+ Website_URL
										+ objProfile.getPhoto().replace(" ",
												"%20") + "");
					}
				}
			}
		}
	}

	public void CancelDialog() {

		new Profile().cancel(true);

		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		frame.removeAllViews();

		fm.beginTransaction().add(R.id.frame, new Home()).commit();
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