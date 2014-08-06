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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.lava.shadowrunner.DrawView.Square;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
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
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;

public class LocationActivity extends Activity implements LocationListener {
	private boolean mAttachedToWindow;
	LocationManager mLocationManager;
	Location mLocation;
	TextView mTvLocation;
	DrawView mDrawView;
	Bundle bundle;
	String name;
	private TextToSpeech tts;

	//wifi_moore_test.txt text for GPS test confused with the name (walking)
	//gps_moore_test.txt text for GPS test (running) to make comparisons
	private String STORETEXT;

	File file;
	private Path path = new Path();
	//Initialize count to see when we calculate the distance in onLocationChanged
	int count;
	int totaldistance;
	
	//Load the test file for prototype
	StringBuilder testrunstringbuilder;
	String [] testrunstring;
	List<Double> testrun = new ArrayList<Double>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		count = 0;
		totaldistance = 0;
		setContentView(R.layout.location);
		mTvLocation = (TextView) findViewById(R.id.tvLocation);
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		testrunstringbuilder = loadtest ("test1");
		testrunstring = testrunstringbuilder.toString().split(",");
		convert(testrunstring);
		//Calculate total distance for the competitor run for drawing based in a scale
		for(int i=0;i<testrunstring.length-1;i++){
			totaldistance+=testrun.get(i);
		}
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
		int userpos, competitorpos;
		Double distance = 0.0;
		Double diference= 0.0;
		String output = "Wating for data";
		
		mLocation = location;
		mTvLocation.setText(mLocation.getLatitude() + " ," + mLocation.getLongitude());
		System.out.println("count value = " +count);
		path.addLocation(location);
		//We race until either we finish or the competitor finishes 
		if(count<testrunstring.length-1){
			//Only one location has no distance!
			if (count>=1){
				distance = path.distance();
				System.out.println("onLocationChanged count" + count);
				saveClicked(distance);
				//At this point we will load other run sessions previously set
				diference = distance - testrun.get(count);
				System.out.println("onLocationChanged diference: " + diference);
				//Decide who is in front
				if (diference>0){
//					Toast.makeText(this, "You are winning by: " + diference.intValue() + " m.", Toast.LENGTH_LONG)
//					.show();
					output = "You are winning by: " + diference.intValue() + " m.";
				}else{
//					Toast.makeText(this, "You are losing by:" + -diference.intValue() + " m.", Toast.LENGTH_LONG)
//					.show();
					output = "You are losing by:" + -diference.intValue() + " m.";
				}
			}
			count ++;
			//Paint the image of the runner in screen
			userpos = mapCoordinate(distance.intValue(),totaldistance);
			competitorpos = mapCoordinate(testrun.get(count).intValue(),totaldistance);
			draw(userpos,competitorpos,output);
		}else{
			//RACE ENDED!!
		}
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
	//****************** Methods used for loading from file *********************
	
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
		//****************** END Methods used for loading from file *********************
		
		//****************** Methods used for drawing in the canvas *********************
		public void draw(int point1, int point2, String text){
		/*  Paint square in top left of the screen to use with the images of hoaliku*/	
		/*	Square square = mDrawView.new Square();
			square.top = 10;
			square.left = 400;
			square.right = 630;
			square.bottom = 300;
			mDrawView.points.add(square);
			mDrawView.paints.add(paint);
			mDrawView.invalidate();  */
			
			//paint line and image moving across the line
			mDrawView.userpoint = point1;
			mDrawView.competitorpoint = point2;
			mDrawView.output = text;
			mDrawView.invalidate();
		}
		
		public int mapCoordinate(int point, int length){
			//Scale f(x)=[((b-a)*(x-min))/(max-min)]+a a:50 b:560 min:0 max:560
			point = (600*point)/length;
			point = ((560-50)*point/560)+50;
			return point;
		}

}
