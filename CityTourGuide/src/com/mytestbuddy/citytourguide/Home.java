package com.mytestbuddy.citytourguide;

import java.util.Arrays;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.mytestbuddy.citytourguide.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class Home extends Fragment implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private GoogleMap googleMap;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	private LocationManager locationManager = null;
	private Location mLocation = null;

	FrameLayout frame;
	ImageButton imgbtnAttraction;
	ImageButton imgbtnHotel;
	ImageButton imgbtnRestaurant;

	String UserId;
	String Type;
	static double currentLatitude;
	static double currentLongitude;

	String WebsiteURL = null;
	String WebServiceURL = null;
	String NAMESPACE = null;
	String METHOD_NAME = "GetLocationData";
	String SOAP_ACTION = null;

	private SoapObject request;
	private PropertyInfo pi_Type;

	ProgressDialog dialog;
	String Response;

	Gson gson = new Gson();
	LocationData[] locationData;

	FragmentManager fm = null;
	CookieManager cm = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.home, container, false);

		getActivity().setTitle(getActivity().getString(R.string.app_name));

		fm = getActivity().getSupportFragmentManager();

		WebsiteURL = getResources().getString(R.string.website_url);
		WebServiceURL = getResources().getString(R.string.webservice_url);
		NAMESPACE = getResources().getString(R.string.namespace);

		this.SOAP_ACTION = NAMESPACE + METHOD_NAME;

		UserId = UserCookie.GetCookies(getActivity());

		frame = (FrameLayout) rootView.findViewById(R.id.frame);
		imgbtnAttraction = (ImageButton) rootView
				.findViewById(R.id.imgbtnAttraction);
		imgbtnHotel = (ImageButton) rootView.findViewById(R.id.imgbtnHotel);
		imgbtnRestaurant = (ImageButton) rootView
				.findViewById(R.id.imgbtnRestaurant);

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

		if (!isGooglePlayServicesAvailable()) {
			getActivity().finish();
		} else {

			locationManager = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
		}

		try {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					|| locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

				rootView.findViewById(R.id.google_map).setVisibility(
						View.VISIBLE);

				// Loading map
				initilizeMap();
			} else {
				rootView.findViewById(R.id.google_map).setVisibility(View.GONE);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Enable Location Service");
				builder.setMessage("Please enable Location Service to continue.");

				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialogInterface, int i) {
								// Show location settings when the user
								// acknowledges
								// the alert dialog
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
							}
						});

				Dialog alertDialog = builder.create();
				alertDialog.setCanceledOnTouchOutside(false);
				alertDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		imgbtnAttraction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				googleMap.clear();
				Type = "Attraction";

				CenterMap();

				new GetLocationData().execute();
			}
		});

		imgbtnHotel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				googleMap.clear();
				Type = "Hotel";

				CenterMap();

				new GetLocationData().execute();
			}
		});

		imgbtnRestaurant.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				googleMap.clear();
				Type = "Restaurant";

				CenterMap();

				new GetLocationData().execute();
			}
		});

		return rootView;
	}

	private class GetLocationData extends AsyncTask<String, String, String> {

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

				if (result != null && !result.equals("")) {
					locationData = gson.fromJson(result, LocationData[].class);

					List<LocationData> lList = Arrays.asList(locationData);
					LocationData objLocation = null;

					if (lList.size() > 0) {
						if (lList.get(0).getName().equals("Server Error")) {
							Toast.makeText(getActivity(),
									"Server Error. Please try again later!",
									Toast.LENGTH_LONG).show();
						} else {

							for (int i = 0; i < lList.size(); i++) {
								objLocation = lList.get(i);

								double[] randomLocation = createRandLocation(
										Double.valueOf(objLocation
												.getLatitude()),
										Double.valueOf(objLocation
												.getLongitude()));

								// Adding a marker
								MarkerOptions marker = new MarkerOptions()
										.position(
												new LatLng(randomLocation[0],
														randomLocation[1]))
										.title(objLocation.getName() + "\n"
												+ objLocation.getAddress());

								if (Type.equals("Attraction")) {
									marker.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.icon_marker_attarction));
								} else if (Type.equals("Hotel")) {
									marker.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.icon_marker_hotel));
								} else if (Type.equals("Restaurant")) {
									marker.icon(BitmapDescriptorFactory
											.fromResource(R.drawable.icon_marker_restaurant));
								}

								googleMap.addMarker(marker);
							}
						}
					}
				}
			}
		}
	}

	private void initilizeMap() {
		if (googleMap == null) {

			googleMap = ((SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.google_map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getActivity(), "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}

			// Changing map type
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

			// Showing / hiding your current location
			googleMap.setMyLocationEnabled(true);

			// Enable / Disable zooming controls
			googleMap.getUiSettings().setZoomControlsEnabled(false);

			// Enable / Disable my location button
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// Enable / Disable Compass icon
			googleMap.getUiSettings().setCompassEnabled(true);

			// Enable / Disable Rotate gesture
			googleMap.getUiSettings().setRotateGesturesEnabled(true);

			// Enable / Disable zooming functionality
			googleMap.getUiSettings().setZoomGesturesEnabled(true);

			googleMap.setMyLocationEnabled(true);

			mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();

			Type = "Attraction";
			new GetLocationData().execute();
		}
	}

	private double[] createRandLocation(double latitude, double longitude) {

		return new double[] { latitude + ((Math.random() - 0.5) / 500),
				longitude + ((Math.random() - 0.5) / 500),
				150 + ((Math.random() - 0.5) * 10) };
	}

	private void CenterMap() {
		LatLng latLng = new LatLng(currentLatitude, currentLongitude);

		MarkerOptions options = new MarkerOptions().position(latLng).title(
				"I am here!");
		options.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_marker_main));
		googleMap.addMarker(options);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		googleMap.animateCamera(CameraUpdateFactory.zoomIn());
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Connect the client.
		if (mGoogleApiClient != null
				&& (locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER))) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onStop() {
		// Disconnecting the client invalidates it.

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle) {

		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(1000); // Update location every second

		mLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

		if (mLocation != null) {
			currentLatitude = mLocation.getLatitude();
			currentLongitude = mLocation.getLongitude();

			LatLng latLng = new LatLng(currentLatitude, currentLongitude);

			MarkerOptions options = new MarkerOptions().position(latLng).title(
					"I am here!");
			options.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.icon_marker_main));
			googleMap.addMarker(options);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
			googleMap.animateCamera(CameraUpdateFactory.zoomIn());
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
		} else {

			Toast.makeText(
					getActivity(),
					"(Couldn't get the location. Make sure location is enabled on the device.)",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.i("Google Map->", "GoogleApiClient connection has been suspend");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.i("Google Map->", "GoogleApiClient connection has failed");
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getActivity());
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0)
					.show();
			return false;
		}
	}

	public void CancelDialog() {

		new GetLocationData().cancel(true);
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

					new GetLocationData().cancel(true);

					for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
						fm.popBackStack();
					}

					frame.removeAllViews();

					getActivity().finish();

					return true;
				}

				return false;
			}
		});
	}

	@Override
	public void onDestroyView() {
		Fragment mapFragment = fm.findFragmentById(R.id.google_map);
		if (mapFragment != null) {
			fm.beginTransaction().remove(mapFragment).commit();
		}

		super.onDestroyView();
	}
}