package com.drocode.android.touchlocation;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.drocode.android.touchlocation.geo.GeoCodeResult;
import com.drocode.android.touchlocation.geo.GeoCoder;
import com.drocode.android.touchlocation.overlay.MapsOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TouchLocation extends MapActivity implements LocationListener,
		View.OnClickListener {

	private MapView mapView;
	private MapController mapController;

	private Location currentLocation;
	private LocationManager locationManager;

	private MyLocationOverlay me = null;

	private boolean hitZoominLimit;
	private boolean hitZoomoutLimit;

	private static final int ZOOM_LEVEL = 17;
	private static final long MIN_TIME = 1000;
	private static final float MIN_DISTANCE = 0;

	private GeoCoder geoCoder = new GeoCoder();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mapView = (MapView) findViewById(R.id.mapView);
		mapController = mapView.getController();
		mapView.setBuiltInZoomControls(false);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Register for updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DISTANCE, this);

		if (currentLocation != null) {
			showCurrentLocation();
		}

		((Button) findViewById(R.id.zoomin)).setOnClickListener(this);
		((Button) findViewById(R.id.zoomout)).setOnClickListener(this);
		((Button) findViewById(R.id.satelite)).setOnClickListener(this);
		((Button) findViewById(R.id.traffic)).setOnClickListener(this);
		((Button) findViewById(R.id.normal)).setOnClickListener(this);
		((Button) findViewById(R.id.findlocation)).setOnClickListener(this);

		me = new MyLocationOverlay(TouchLocation.this, mapView);
		mapView.getOverlays().add(me);

		initLocationMap();

		mapView.invalidate();
	}

	private void initLocationMap() {
		double lat = -6.29826;
		double lon = 106.82024;
		MapsOverlay m2 = new MapsOverlay(this.getResources().getDrawable(
				R.drawable.home), TouchLocation.this);
		List<Overlay> l2 = mapView.getOverlays();
		m2.addItem(getPoint(lat, lon), "RM Padang Sari Mande",
				"Menyediakan Aneka Spesial Makanan" + "\nLong:10682024"
						+ "\nLat:-6.29826");
		l2.add(m2);
	}

	public void shareIt(String pmessage) {
		Intent i = new Intent(Intent.ACTION_SEND);

		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_SUBJECT, "My Location");
		i.putExtra(Intent.EXTRA_TEXT, pmessage);

		startActivity(Intent.createChooser(i, "My Location"));
	}

	private void showCurrentLocation() {

		// Get a cached location, if it exists
		currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		String message = String
				.format("Longitude: %1$s\nLatitude: %2$s\nAltitude: %f\nSpeed: %f\nAccuracy: %f",
						currentLocation.getLongitude(),
						currentLocation.getLatitude(),
						currentLocation.getAltitude(),
						currentLocation.getSpeed(),
						currentLocation.getAccuracy());

		// My Location via GPS
		double latitude = currentLocation.getLatitude();
		double longitude = currentLocation.getLongitude();

		mapController.animateTo(getPoint(latitude, longitude));
		mapController.setZoom(ZOOM_LEVEL);

		Drawable pin = this.getResources().getDrawable(R.drawable.mylocation);
		pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());

		MapsOverlay markers = new MapsOverlay(pin, TouchLocation.this);
		markers.addItem(getPoint(latitude, longitude), "My Current Location",
				message);
		List<Overlay> list = mapView.getOverlays();
		list.add(markers);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.detaillocation:
			performReverseGeo();
			return true;

		case R.id.exit:
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void createGpsAlert() {
		// Ask the user to enable GPS
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TouchLocation.this);
		builder.setTitle("Location Manager");
		builder.setMessage("Your GPS is currently disabled.\n"
				+ "Would you like to change these settings now?");
		// builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent gpsOptionsIntent = new Intent(Intent.ACTION_MAIN);
				gpsOptionsIntent.setClassName("com.android.settings",
						"com.android.settings.SecuritySettings");
				startActivity(gpsOptionsIntent);
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	protected GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)));
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Check if the GPS setting is currently enabled on the device.
		final boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			createGpsAlert();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Resume location updates when we're resumed
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME, MIN_DISTANCE, this);
		me.enableCompass();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Turn off location updates if we're paused
		locationManager.removeUpdates(this);
		me.disableCompass();
	}

	@Override
	public void onLocationChanged(Location newLocation) {
		currentLocation = newLocation;
		showCurrentLocation();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getApplicationContext(), "GPS Disabled",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(getApplicationContext(), "GPS Enabled",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.zoomin:
			if (hitZoomoutLimit) {
				hitZoomoutLimit = false;
				((Button) findViewById(R.id.zoomout)).setEnabled(true);
			}

			hitZoominLimit = !mapController.zoomIn();
			if (hitZoominLimit) {
				((Button) findViewById(R.id.zoomin)).setEnabled(false);
			}
			break;

		case R.id.zoomout:
			if (hitZoominLimit) {
				hitZoominLimit = false;
				((Button) findViewById(R.id.zoomin)).setEnabled(true);
			}

			hitZoomoutLimit = !mapController.zoomOut();
			if (hitZoomoutLimit) {
				((Button) findViewById(R.id.zoomout)).setEnabled(false);
			}
			break;

		case R.id.satelite:
			if (!mapView.isSatellite()) {
				mapView.setSatellite(true);
			}
			break;

		case R.id.traffic:
			if (!mapView.isTraffic()) {
				mapView.setTraffic(true);
			}
			break;

		case R.id.normal:
			mapView.setSatellite(false);
			mapView.setTraffic(false);
			break;

		case R.id.findlocation:
			showCurrentLocation();
			break;

		}
	}

	protected void performReverseGeo() {
		new ReverseGeocodeLookupTask().execute((Void[]) null);
	}

	protected class ReverseGeocodeLookupTask extends
			AsyncTask<Void, Void, GeoCodeResult> {

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			this.progressDialog = ProgressDialog.show(TouchLocation.this, null,
					"Please wait...", true, true,
					new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							ReverseGeocodeLookupTask.this.cancel(true);
							progressDialog.cancel();
						}
					});
		}

		@Override
		protected GeoCodeResult doInBackground(Void... params) {
			return geoCoder.reverseGeoCode(currentLocation.getLatitude(),
					currentLocation.getLongitude());
		}

		@Override
		protected void onPostExecute(GeoCodeResult result) {
			this.progressDialog.cancel();

			// My current location via GPS
			String currentLoc = String.format(
					"Latitude: %.6f\nLongitude: %.6f",
					currentLocation.getLatitude(),
					currentLocation.getLongitude());

			String currentLoc2 = convertDecimalCoordinatesToMinutes(
					currentLocation.getLatitude(),
					currentLocation.getLongitude());

			// GeoCoding location
			String addressLocation = result.toString();

			AlertDialog.Builder dialog = new AlertDialog.Builder(
					TouchLocation.this);
			dialog.setTitle("Details Location");

			final TextView message = new TextView(TouchLocation.this);
			message.setTextSize(14);
			message.setPadding(10, 10, 10, 10);
			message.setText(Html.fromHtml(currentLoc2) + "\n\n" + currentLoc
					+ "\n" + addressLocation);

			dialog.setView(message);
			dialog.setPositiveButton("Share", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dlg, int which) {
					String status = message.getText().toString();
					shareIt(status);
				}
			});

			dialog.setNegativeButton("Close", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dlg, int which) {
					dlg.dismiss();
				}
			});
			dialog.show();
		}
	}

	private static String convertDecimalCoordinatesToMinutes(double latitude,
			double longitude) {
		
		StringBuffer result = new StringBuffer(100);

		double absLatitude = Math.abs(latitude);
		int latDegrees = ((Double) absLatitude).intValue();

		double minute = 60.0 * (absLatitude - latDegrees);
		double absMinute = Math.abs(minute);
		int minutes = ((Double) absMinute).intValue();

		double seconds = 60.0 * (absMinute - minutes);

		result.append(((latitude < 0.0) ? "S " : "N ")).append(latDegrees);
		result.append("&deg; ").append(minutes);
		result.append("&#39; ").append(String.format("%.3f", seconds));
		result.append("&quot;").append(" ");

		double absLongitude = Math.abs(longitude);
		int lonDegrees = ((Double) absLongitude).intValue();

		minute = 60.0 * (absLongitude - lonDegrees);
		absMinute = Math.abs(minute);
		minutes = ((Double) absMinute).intValue();

		seconds = 60.0 * (absMinute - minutes);

		result.append(((longitude < 0.0) ? "W " : "E ")).append(lonDegrees);
		result.append("&deg; ").append(minutes);
		result.append("&#39; ").append(String.format("%.3f", seconds));
		result.append("&quot;");

		return result.toString();
	}
}