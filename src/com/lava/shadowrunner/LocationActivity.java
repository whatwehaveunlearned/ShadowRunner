package com.lava.shadowrunner;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity implements LocationListener {
	private boolean mAttachedToWindow;
	LocationManager mLocationManager;
	Location mLocation;
	TextView mTvLocation;

	//wifi_moore_test.txt text for GPS test confused with the name (walking)
	//gps_moore_test.txt text for GPS test (running) to make comparisons
	private final static String STORETEXT="test.txt";

	File file = new File(STORETEXT);
	private Path path = new Path();
	//Initialize count to see when we calculate the distance in onLocationChanged
	int count = 0;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		mTvLocation = (TextView) findViewById(R.id.tvLocation);
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		//criteria.setAccuracy(Criteria.ACCURACY_FINE);
		//criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		//System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true));
		System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true));
		String allString="";
		
		List<String> providers = mLocationManager.getProviders(criteria, true);
		for (String p : providers) {
			allString += p+":";
			if (mLocationManager.isProviderEnabled(p)){
				allString += "Y;";
				mLocationManager.requestLocationUpdates(p,10000,0,this);
				Location location = mLocationManager.getLastKnownLocation(p);
				if(location==null)
					System.out.println("getLastKnownLocation for provider " + p + " returns null");
				else{
					System.out.println("getLastKnownLocation for provider " + p + "returns NOT null");
					mTvLocation.setText(location.getLatitude() + "," + location.getLongitude());
				}
			}
			else allString += "N;";
			//on Glass, allString is : remote_gps:Y;remote_network:Y;network:Y;passive:Y
			mTvLocation.setText(allString);
		}
	}
	
	
	
	
/*	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mAttachedToWindow = true;
		openOptionsMenu();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mAttachedToWindow = false;
	}	

	@Override
	public void openOptionsMenu() {
		if (mAttachedToWindow) {
			super.openOptionsMenu();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		Intent intent = new Intent(this, LocationActivity.class);
		Bundle b;
		switch (item.getItemId()) {
		case R.id.stop:
			stopService(new Intent(this, AppService.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	@Override
	protected void onDestroy() {
		endGPS();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		double distance;
		mLocation = location;
		mTvLocation.setText(mLocation.getLatitude() + " ," + mLocation.getLongitude());
		System.out.println("onLocationChanged " + count);
		if (count>1){
			path.addLocation(location);
			if (count>3){
				distance = path.distance();
				System.out.println("onLocationChanged " + distance);
				count = 0;
				saveClicked(distance);
				Toast.makeText(this,"distance: " + distance, Toast.LENGTH_LONG)
				.show();
			}
		}
			count ++;
		
		
		//System.out.println("onLocationChanged " + path.distance());
		//getIntent().putExtra("distance", path.distance());
		//i.putExtra("distance", path.distance());
		//System.out.println("onLocationChanged " + getIntent().getBundleExtra("distance"));
		//System.out.println("onLocationChanged " + b);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	public void endGPS()
	{

		try
		{           
			mLocationManager.removeUpdates(this);
			mLocationManager=null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Method to save the coordinates values on file
	private void saveClicked(double value) {
		String string;
	    try{
	        OutputStreamWriter out=
	                new OutputStreamWriter(openFileOutput(STORETEXT,  MODE_APPEND));
	        string = String.valueOf(value);
	        out.write(string + ",");
	        out.close();

	    }
	    catch(Throwable t){
	        Toast.makeText(this, "Exception: "+ t.toString(), Toast.LENGTH_LONG)
	        .show();

	    }
	}
	
	//Method used to read the values from file
		public StringBuffer load(String file){
			FileInputStream fis;
			final StringBuffer storedString = new StringBuffer();

			try {
			    fis = openFileInput(file);
			    DataInputStream dataIO = new DataInputStream(fis);
			    String strLine = null;

			    if ((strLine = dataIO.readLine()) != null) {
			        storedString.append(strLine);
			    }

			    dataIO.close();
			    fis.close();
			    Toast.makeText(this, storedString, Toast.LENGTH_LONG)
		        .show();
			    return storedString;
			}
			catch  (Exception e) {  
				return null;
			}
			}
	

}
