package com.mytestbuddy.citytourguide;

import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class Feedback extends Fragment {

	FrameLayout frame;
	TextView tvValidation;
	EditText etMessage;
	Button btFeedback;

	String UserId;
	String Feedback;

	String WebsiteURL = null;
	String WebServiceURL = null;
	String NAMESPACE = null;
	String METHOD_NAME = "Feedback";
	String SOAP_ACTION = null;

	private SoapObject request;
	private PropertyInfo pi_UserId;
	private PropertyInfo pi_Feedback;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	ShowMessage[] Message;

	CookieManager cm = null;
	FragmentManager fm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.feedback, container, false);

		getActivity().setTitle("Feedback");

		fm = getActivity().getSupportFragmentManager();

		WebsiteURL = getResources().getString(R.string.website_url);
		WebServiceURL = getResources().getString(R.string.webservice_url);
		NAMESPACE = getResources().getString(R.string.namespace);

		this.SOAP_ACTION = NAMESPACE + METHOD_NAME;

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		tvValidation = (TextView) rootView.findViewById(R.id.tvValidation);
		etMessage = (EditText) rootView.findViewById(R.id.etMessage);
		btFeedback = (Button) rootView.findViewById(R.id.btFeedback);

		etMessage.setFocusable(false);

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

		etMessage.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub

				etMessage.setFocusable(true);
				etMessage.setFocusableInTouchMode(true);

				return false;
			}
		});

		etMessage.setOnKeyListener(new View.OnKeyListener() {

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

		btFeedback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Feedback = ((EditText) etMessage).getText().toString().trim();

				if (Feedback.equals("")) {
					tvValidation.setText("Please Enter Feedback.");
					tvValidation.setTextColor(getResources().getColor(
							R.color.red));
				} else {

					if (CheckInternet.isConnectingToInternet(getActivity())) {
						new FeedbackTask().execute();

						etMessage.setFocusable(false);
						etMessage.setFocusableInTouchMode(false);
						etMessage.clearFocus();

						getView().setFocusable(true);
						getView().setFocusableInTouchMode(true);
						getView().requestFocus();
					} else {

						tvValidation
								.setText("Network Unavailable!\nPlease Check Your Internet Connection..");
						tvValidation.setTextColor(getResources().getColor(
								R.color.red));
					}
				}
			}
		});

		return rootView;
	}

	private class FeedbackTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(getActivity(), "",
					"Submitting your Feedback...\nPlease wait...", true, true,
					new OnCancelListener() {

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

				pi_UserId = new PropertyInfo();
				pi_UserId.setName("UserId");
				pi_UserId.setValue(UserId);
				pi_UserId.setType(String.class);
				request.addProperty(pi_UserId);

				pi_Feedback = new PropertyInfo();
				pi_Feedback.setName("Feedback");
				pi_Feedback.setValue(Feedback);
				pi_Feedback.setType(String.class);
				request.addProperty(pi_Feedback);

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
					Message = gson.fromJson(result, ShowMessage[].class);

					List<ShowMessage> lList = Arrays.asList(Message);
					ShowMessage objShowMessage = lList.get(0);

					if (objShowMessage.getMessage().equals("Success")) {
						tvValidation.setText("Thank you for your Feedback.");
						tvValidation.setTextColor(getResources().getColor(
								R.color.green));

						((EditText) etMessage).setText("");
					} else if (objShowMessage.getMessage().equals(
							"Server Error")) {
						tvValidation
								.setText("Server Error. Please try again later!");
						tvValidation.setTextColor(getResources().getColor(
								R.color.red));

					} else {
						tvValidation.setText("Something is Wrong.");
						tvValidation.setTextColor(getResources().getColor(
								R.color.red));
					}
				} else {
					tvValidation.setText("Something is Wrong.");
					tvValidation.setTextColor(getResources().getColor(
							R.color.red));
				}
			}
		}
	}

	public void CancelDialog() {

		new FeedbackTask().cancel(true);

		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		frame.removeAllViews();

		fm.beginTransaction().add(R.id.frame, new Home()).commit();
	}

	@Override
	public void onResume() {

		super.onResume();

		etMessage.setFocusable(false);
		etMessage.setFocusableInTouchMode(false);
		etMessage.clearFocus();

		getView().setFocusableInTouchMode(true);
		getView().requestFocus();
		getView().setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == KeyEvent.ACTION_UP
						&& keyCode == KeyEvent.KEYCODE_BACK) {

					new FeedbackTask().cancel(true);

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
