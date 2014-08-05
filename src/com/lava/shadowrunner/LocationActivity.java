package com.lava.shadowrunner;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.lava.shadowrunner.DrawView.Square;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
	DrawView mDrawView;
	Bundle bundle;
	String name;

	//wifi_moore_test.txt text for GPS test confused with the name (walking)
	//gps_moore_test.txt text for GPS test (running) to make comparisons
	private String STORETEXT;

	File file;
	private Path path = new Path();
	//Initialize count to see when we calculate the distance in onLocationChanged
	int count = 0;
	
	//Load the test file for prototype
	StringBuilder testrunstringbuilder;
	String [] testrunstring;
	List<Double> testrun = new ArrayList<Double>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		mTvLocation = (TextView) findViewById(R.id.tvLocation);
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		testrunstringbuilder = loadtest ("test1");
		System.out.println(testrunstringbuilder);
		testrunstring = testrunstringbuilder.toString().split(",");
		System.out.println(testrunstring[0]);
		convert(testrunstring);
		mDrawView = new DrawView(this);
		setContentView(mDrawView);
		mDrawView.requestFocus();
		//Retrieve the name of the run passed from MenuActivity
		bundle = getIntent().getExtras();
		STORETEXT = bundle.getString("path_name") + ".txt";
		System.out.println(STORETEXT);
		file = new File(STORETEXT);
		
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
				mLocationManager.requestLocationUpdates(p,100*60,0,this);
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
	

	@Override
	protected void onDestroy() {
		endGPS();
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {
		
		double distance;
		Double diference;
		
		mLocation = location;
		mTvLocation.setText(mLocation.getLatitude() + " ," + mLocation.getLongitude());
		System.out.println("count value = " +count);
		//Only one location has no distance!
		if (count>=1){
			path.addLocation(location);
			distance = path.distance();
			System.out.println("onLocationChanged " + count);
			saveClicked(distance);
			//At this point we will load other run sessions previously set
			diference = distance - testrun.get(count);
			System.out.println(testrun);
			System.out.println("onLocationChanged diference: " + diference);
			Toast.makeText(this,"distance: " + diference, Toast.LENGTH_LONG)
					.show();
					if (diference>0){
						mTvLocation.setText("You are winning by: " + diference.intValue() + " m.");
					}else{
		mTvLocation.setText("You are losing by:" + -diference.intValue() + " m.");
			}
		}
		count ++;
		draw();
		
		
		
		
		
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
	
	//Method to load test runs resources already hard-coded
		public StringBuilder loadtest (String filename){
			
			InputStream ins = getResources().openRawResource(
					getResources().getIdentifier("raw/"+ filename,
							"raw", getPackageName()));
			BufferedReader r = new BufferedReader(new InputStreamReader(ins));
			StringBuilder total = new StringBuilder();
			String line;
			
			try {
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return total;
		}
		
		//Method to convert values from String to double
		public void convert (String [] string){
			for (int i=0;i<string.length;i++){
					testrun.add(Double.parseDouble(string[i]));
			}
		}
		
		public void draw(){
			Square square = mDrawView.new Square();
			square.top = 10;
			square.left = 400;
			square.right = 630;
			square.bottom = 300;
			Paint paint = new Paint();
			paint.setARGB(255,255,255,255);
			paint.setAntiAlias(true);
			mDrawView.points.add(square);
			mDrawView.paints.add(paint);
			mDrawView.invalidate();
		}
	

}
