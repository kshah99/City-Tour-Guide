package com.mytestbuddy.citytourguide;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {

	String UserId;
	String Type = null;

	String WebsiteURL = null;
	String WebServiceURL = null;
	String NAMESPACE = null;
	String METHOD_NAME = "RegisterMobileDevice";
	String SOAP_ACTION = null;

	private SoapObject request;

	private PropertyInfo pi_DeviceId;
	private PropertyInfo pi_UserId;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		WebsiteURL = getResources().getString(R.string.website_url);
		WebServiceURL = getResources().getString(R.string.webservice_url);
		NAMESPACE = getResources().getString(R.string.namespace);

		this.SOAP_ACTION = NAMESPACE + METHOD_NAME;
	}

	public GCMIntentService() {
		super("531021467388");
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.e("Registration", "Got an error!");
		Log.e("Registration", arg0.toString() + arg1.toString());
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.i("Registration", "Got a message!");
		Log.i("Registration", arg0.toString() + " " + arg1.toString());
		String message = arg1.getStringExtra("message");

		generateNotification(arg0, message);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		Log.i("Registration", "Just registered!");
		Log.i("Registration", arg0.toString() + arg1.toString());

		Register();
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
	}

	public void Register() {

		UserId = UserCookie.GetCookies(getApplicationContext());

		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());

		String DeviceId = GCMRegistrar
				.getRegistrationId(getApplicationContext());

		request = new SoapObject(NAMESPACE, METHOD_NAME);

		pi_UserId = new PropertyInfo();
		pi_UserId.setName("UserId");
		pi_UserId.setValue(UserId);
		pi_UserId.setType(String.class);
		request.addProperty(pi_UserId);

		pi_DeviceId = new PropertyInfo();
		pi_DeviceId.setName("DeviceId");
		pi_DeviceId.setValue(DeviceId);
		pi_DeviceId.setType(String.class);
		request.addProperty(pi_DeviceId);

		SoapSerializationEnvelope envp = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envp.dotNet = true;
		envp.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(
				WebServiceURL);
		try {
			androidHttpTransport.call(SOAP_ACTION, envp);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "deprecation" })
	private void generateNotification(Context context, String message) {

		String[] data = message.split("__");

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;

		String title = context.getString(R.string.app_name);

		notification = new Notification(R.drawable.ic_launcher, data[0],
				System.currentTimeMillis());

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra("type", "Notification");
		notificationIntent.putExtra("Message", data[1]);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notification.setLatestEventInfo(context, title, data[0], intent);
		notificationManager.notify(0, notification);
	}
}