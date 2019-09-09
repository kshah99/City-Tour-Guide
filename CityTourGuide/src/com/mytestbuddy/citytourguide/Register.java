package com.mytestbuddy.citytourguide;

import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends Activity {

	EditText etFirstName;
	EditText etLastName;
	EditText etMobileNumber;
	EditText etEmail;
	EditText etPassword;
	Button btnRegister;
	TextView tvValidation;
	TextView tvLogin;

	String FirstName;
	String LastName;
	String Email;
	String Password;
	String ContactNumber;

	String Website_URL = "";
	String Webservice_URL = "";
	String NameSpace = "";
	String Method_Name = "Register";
	String SOAP_Action = "";

	PropertyInfo pi_FirstName;
	PropertyInfo pi_LastName;
	PropertyInfo pi_Email;
	PropertyInfo pi_Password;
	PropertyInfo pi_ContactNumber;

	SoapObject Request;

	ProgressDialog dialog;
	String Response;

	ShowMessage[] Message;
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		setTitle("Register- "
				+ getApplicationContext().getString(R.string.app_name));

		Website_URL = getResources().getString(R.string.website_url);
		Webservice_URL = getResources().getString(R.string.webservice_url);
		NameSpace = getResources().getString(R.string.namespace);

		SOAP_Action = NameSpace + Method_Name;

		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etLastName = (EditText) findViewById(R.id.etLastName);
		etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		tvValidation = (TextView) findViewById(R.id.tvValidation);
		tvLogin = (TextView) findViewById(R.id.tvLogin);

		tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
		tvLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();

				Intent intLogin = new Intent(Register.this, Login.class);
				startActivity(intLogin);
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FirstName = etFirstName.getText().toString().trim();
				LastName = etLastName.getText().toString().trim();
				Email = etEmail.getText().toString().trim();
				Password = etPassword.getText().toString().trim();
				ContactNumber = etMobileNumber.getText().toString().trim();

				new RegisterTask().execute();
			}
		});
	}

	private class RegisterTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(Register.this, "", "Please Wait...",
					true);
		}

		@Override
		protected String doInBackground(String... params) {

			Request = new SoapObject(NameSpace, Method_Name);

			pi_FirstName = new PropertyInfo();
			pi_FirstName.setName("FirstName");
			pi_FirstName.setValue(FirstName);
			pi_FirstName.setType(String.class);
			Request.addProperty(pi_FirstName);

			pi_LastName = new PropertyInfo();
			pi_LastName.setName("LastName");
			pi_LastName.setValue(LastName);
			pi_LastName.setType(String.class);
			Request.addProperty(pi_LastName);

			pi_Email = new PropertyInfo();
			pi_Email.setName("Email");
			pi_Email.setValue(Email);
			pi_Email.setType(String.class);
			Request.addProperty(pi_Email);

			pi_Password = new PropertyInfo();
			pi_Password.setName("Password");
			pi_Password.setValue(Password);
			pi_Password.setType(String.class);
			Request.addProperty(pi_Password);

			pi_ContactNumber = new PropertyInfo();
			pi_ContactNumber.setName("ContactNumber");
			pi_ContactNumber.setValue(ContactNumber);
			pi_ContactNumber.setType(String.class);
			Request.addProperty(pi_ContactNumber);

			SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envp.dotNet = true;
			envp.setOutputSoapObject(Request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					Webservice_URL);
			try {
				androidHttpTransport.call(SOAP_Action, envp);
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

					if (objMessage.getMessage().equals("already_registered")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation.setText("You have already Registered.");

					} else if (objMessage.getMessage().equals("success")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation
								.setText("You have successfully Registered.\nCheck your Email for Account Activation.");

					} else if (objMessage.getMessage().equals("error")) {
						tvValidation.setVisibility(View.VISIBLE);
						tvValidation
								.setText("Server Error. Please try after Sometime!");

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