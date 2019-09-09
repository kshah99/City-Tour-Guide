package com.mytestbuddy.citytourguide;

import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {

	EditText etEmail;
	EditText etPassword;
	TextView tvForgotPassword;
	Button btnLogin;
	TextView tvValidation;
	TextView tvRegister;

	String Email;
	String Password;

	String Website_URL = "";
	String Webservice_URL = "";
	String NameSpace = "";
	String Method_Name = "Login";
	String SOAP_ACTION = "";

	PropertyInfo pi_Email;
	PropertyInfo pi_Password;

	SoapObject request;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	ShowMessage[] Message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		setTitle("Login- "
				+ getApplicationContext().getString(R.string.app_name));

		Website_URL = getResources().getString(R.string.website_url);
		Webservice_URL = getResources().getString(R.string.webservice_url);
		NameSpace = getResources().getString(R.string.namespace);

		SOAP_ACTION = NameSpace + Method_Name;

		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		tvValidation = (TextView) findViewById(R.id.tvValidation);
		tvRegister = (TextView) findViewById(R.id.tvRegister);

		tvForgotPassword.setMovementMethod(LinkMovementMethod.getInstance());
		tvForgotPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();

				Intent intForgotPassword = new Intent(Login.this,
						ForgotPassword.class);
				startActivity(intForgotPassword);
			}
		});

		tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
		tvRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();

				Intent intForgotPassword = new Intent(Login.this,
						Register.class);
				startActivity(intForgotPassword);
			}
		});

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Email = etEmail.getText().toString().trim();
				Password = etPassword.getText().toString().trim();

				new LoginTask().execute();
			}
		});
	}

	private class LoginTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {

			dialog = ProgressDialog
					.show(Login.this, "", "Please Wait...", true);
		}

		@Override
		protected String doInBackground(String... params) {

			request = new SoapObject(NameSpace, Method_Name);

			pi_Email = new PropertyInfo();
			pi_Email.setName("Email");
			pi_Email.setValue(Email);
			pi_Email.setType(String.class);
			request.addProperty(pi_Email);

			pi_Password = new PropertyInfo();
			pi_Password.setName("Password");
			pi_Password.setValue(Password);
			pi_Password.setType(String.class);
			request.addProperty(pi_Password);

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
				// Response = e.getMessage().toString();
				e.printStackTrace();
			}

			return Response;
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			}

			if (result != null && !result.equals("")) {

				Message = gson.fromJson(result, ShowMessage[].class);
				List<ShowMessage> lstMessage = Arrays.asList(Message);

				if (lstMessage.size() > 0) {
					ShowMessage objMessage = lstMessage.get(0);

					if (objMessage.getMessage().equals("not_active")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation
								.setText("Your account is not Active. Please activate it before Login.");

					} else if (objMessage.getMessage().equals("invalid")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation.setText("Email or Password is Wrong.");

					} else if (objMessage.getMessage().equals("not_registered")) {

						tvValidation.setVisibility(View.VISIBLE);
						tvValidation.setText("You have not Registered yet.");

					} else if (objMessage.getMessage().equals("error")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation
								.setText("Server Error. Please try after Sometime!");

					} else {
						tvValidation.setVisibility(View.GONE);

						if (CheckInterger.isInteger(objMessage.getMessage())) {
							CookieManager cm = CookieManager.getInstance();
							cm.setAcceptCookie(true);
							cm.setCookie("" + Website_URL + "", "CTGUserId="
									+ objMessage.getMessage());
							CookieSyncManager.getInstance().sync();

							GCMRegistrar.checkDevice(getApplicationContext());
							GCMRegistrar.checkManifest(getApplicationContext());

							String regId = GCMRegistrar
									.getRegistrationId(getApplicationContext());

							if (regId.equals("")) {
								GCMRegistrar.register(getApplicationContext(),
										"531021467388");
							}

							Intent intHome = new Intent(Login.this,
									MainActivity.class);
							startActivity(intHome);
						} else {
							tvValidation.setVisibility(View.VISIBLE);
							tvValidation
									.setText("Something is Wrong. Please try after Sometime!");
						}
					}
				} else {
					tvValidation.setVisibility(View.VISIBLE);
					tvValidation
							.setText("Something is Wrong. Please try after Sometime!");
				}
			}
		}
	}
}