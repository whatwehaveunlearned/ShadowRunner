package com.lava.shadowrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends Activity implements LocationListener {
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
	int count;
	int totaldistance;
	//Hold the values for the user and the competitor actual distance
	Double userdistance;
	Double competdistance;
	
	//Load the test file for prototype
	StringBuilder testrunstringbuilder;
	String [] testrunstring;
	List<Double> testrun = new ArrayList<Double>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		count = 0;
		totaldistance = 0;
		userdistance = 0.0;
		competdistance = 0.0;
		setContentView(R.layout.location);
		mTvLocation = (TextView) findViewById(R.id.tvLocation);
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		testrunstringbuilder = loadtest ("test1");
		testrunstring = testrunstringbuilder.toString().split(",");
		convert(testrunstring);
		//Calculate total distance for the competitor run for drawing based in a scale
		totaldistance+=testrun.get(testrunstring.length-1);
		System.out.println("TOTALDISTANCE: " + totaldistance);
		//Retrieve the name of the run passed from MenuActivity
		bundle = getIntent().getExtras();
		STORETEXT = bundle.getString("path_name") + ".txt";
		file = new File(STORETEXT);
		//For painting using canvas
		mDrawView = new DrawView(this);
		setContentView(mDrawView);
		mDrawView.requestFocus();
		
		
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
				mLocationManager.requestLocationUpdates(p,100*60,0,this); //100*60
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
		//Our zero in the line is 50
		int userpos=50;
		int competitorpos=50;
		boolean exit=false;
		Double diference= 0.0;
		String output = "";
		mLocation = location;
		
		mTvLocation.setText(mLocation.getLatitude() + " ," + mLocation.getLongitude());
		System.out.println("count value = " +count);
		path.addLocation(location);
		//We set to be -1 when the race is ended
		if(exit == true){
			endGPS();
			finish();
		}
		//Only one location has no distance! so we wait to calculate when we have at least 2
		if (count>=1){
			userdistance = path.distance();
			competdistance = testrun.get(count-1);
			saveClicked(userdistance);
			//At this point we will load other run sessions previously set
			diference = userdistance - competdistance;
			//Decide who is in front
			if (diference > 0){
				output = diference.intValue() + " m.";
			}else{
				output = diference.intValue() + " m.";
			}
			//Paint the image of the runner in screen
			userpos = mapCoordinate(userdistance.intValue(),totaldistance);
			competitorpos = mapCoordinate(competdistance.intValue(),totaldistance);
			if (competdistance == totaldistance){
				output = "SORRY YOU LOST...";
				count=-1;
				exit=true;
			}else if (path.distance() == totaldistance){
				output = "CONGRATS YOU WON! ";
				exit=true;
			}
		}
		draw(userpos,competitorpos,output);
		count ++;
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
	
	//Method to stop the GPS
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
	
	//****************** Methods used for saving & loading from file *************************
	
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
			System.out.println("allValues: " + testrun);
		}
		
		//****************** END Methods used for saving & loading from file *********************
		
		//****************** Methods used for drawing in the canvas *********************
		
		public void draw(int point1, int point2, String text){			
			//paint line and image moving across the line
			mDrawView.userpoint = point1;
			System.out.println("point1: " + point1);
			System.out.println("point2: " + point2);
			mDrawView.competitorpoint = point2;
			mDrawView.output = text;
			mDrawView.invalidate();
		}
		
		//Method to scale values of the positions to the actual drawing
		public int mapCoordinate(int point, int length){
			int converted;
			//Scale f(x)=[((b-a)*(x-min))/(max-min)]+a a:50 b:560 min:0 max:length
			converted = ((560-50)*point/length)+50;
			return converted;
		}
		
		//****************** END Methods used for drawing in the canvas *********************
}
