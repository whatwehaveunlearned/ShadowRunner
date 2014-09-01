package com.lava.shadowrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class LocationActivity extends Activity implements LocationListener, SensorEventListener {
	
	//location declarations
	private LocationManager mLocationManager;
	//private Location mLocation;
	private DrawView mDrawView;
	Bundle bundle;
	//Test name of the run used only for testing purposes
	private String STORETEXT="default1.txt";

	//Initialize values for onLocationChanged
	private int count;
	private int totaldistance;
	//Hold the values for the user and the competitor actual distance
	private Double userdistance;
	private Double competdistance;
	//Set to true when the race is finished
	private boolean exit=false;
	
	//Load the test file for prototype
	private StringBuilder testrunstringbuilder;
	private String [] testrunstring;
	private List<Double> testdistance = new ArrayList<Double>();
	private List<Double> testspeed = new ArrayList<Double>();
	private File file;
	private Path path = new Path();
	
	//Data base manager
	private DataBaseManager manager;
	private Cursor cursor;
	
	//Sensor measurements
	private SensorManager mSensorManager;
	private Sensor mSensorAccelerometer;
	private Sensor mSensorGravity;
	private Sensor mSensorGyroscope;
	private Sensor mSensorLight;
	private Sensor mSensorLinearAcceleration;
	private Sensor mSensorMagneticField;
	private Sensor mSensorRotationVector;
	private Date mSensorDataUpdatedTime;
	float[] mAccelerometer;
	float[] mGravity;
	float[] mGyroscope;
	float[] mLight;
	float[] mLinearAcceleration;
	float[] mMagneticField;
	float[] mRotationVector;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//The next line is needed in order to keep the screen on all the time
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//Initialize variables that will be updated in onChangedLocation
		count = 0;
		totaldistance = 0;
		userdistance = 0.0;
		competdistance = 0.0;
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//Load the test file get the distances and speeds
		testrunstringbuilder = loadtest ("distance1");
		testrunstring = testrunstringbuilder.toString().split(",");
		System.out.println(testrunstring);
		convert(testrunstring, testdistance);
		testrunstringbuilder = loadtest ("speed1");
		testrunstring = testrunstringbuilder.toString().split(",");
		convert(testrunstring, testspeed);
		//Calculate total distance for the competitor run for drawing based in a scale
		totaldistance+=testdistance.get(testrunstring.length-1);
		//Retrieve the name of the run passed from MenuActivity
		bundle = getIntent().getExtras();
		//STORETEXT = bundle.getString("path_name") + ".txt";
		file = new File(STORETEXT);
		//Create the database if it does not exist if it does it just loads it
		manager = new DataBaseManager(this);
		//Insert user run to db
		manager.insertDB("user", DataBaseManager.CN_USER_NAME, "test");
		cursor = manager.LoadCursor("user", null, null, null, null, null, null);
		System.out.println("Cursor: " + cursor);
		//For painting using canvas
		mDrawView = new DrawView(this);
		setContentView(mDrawView);
		mDrawView.requestFocus();
		//Sensor assignment declarations
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		mSensorLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mSensorMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mSensorDataUpdatedTime = new Date();
		
		
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
				}
			}
			else allString += "N;";
			//on Glass, allString is : remote_gps:Y;remote_network:Y;network:Y;passive:Y
		}
		mSensorManager.registerListener(this, mSensorAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorGravity,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorLight,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorLinearAcceleration,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorMagneticField,SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mSensorRotationVector,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onDestroy() {
		mSensorManager.unregisterListener(this);
		endGPS();
		super.onDestroy();
	}

	//********************************************************* LOCATION METHODS **************************************************************************
	@Override
	public void onLocationChanged(Location location) {
		//Our zero in the line is 50
		int userpos=50;
		int competitorpos=50;
		Double diference= 0.0;
		String output = "";
		double competspeed=0.0;
		
		path.addLocation(location);
		//We set to be -1 when the race is ended save the path to the data base
		if(exit == true){
			//manager.insertDB(path, columnName, text);
			this.endGPS();
			mSensorManager.unregisterListener(this);
			this.finish();
		}
		//Only one location has no distance! so we wait to calculate when we have at least 2
		if (count>=1){
			userdistance = path.distance();
			path.MediaSpeed();
			competdistance = testdistance.get(count-1);
			competspeed = testspeed.get(count-1);
			saveClicked(userdistance);
			//At this point we will load other run sessions previously set
			diference = userdistance - competdistance;
			//Decide who is in front
			if (diference > 0){
				mDrawView.text.setColor(Color.GREEN);
			}else{
				mDrawView.text.setColor(Color.RED);
			}
			output = diference.intValue() + " m";
			//Paint the image of the runner in screen
			userpos = mapCoordinate(userdistance.intValue(),totaldistance);
			competitorpos = mapCoordinate(competdistance.intValue(),totaldistance);
			if (competdistance >= totaldistance){
				output = "YOU LOST...";
				count=-1;
				mDrawView.text.setColor(Color.RED);
				mDrawView.text.setTextSize(50);
				exit=true;
			}else if (userdistance >= totaldistance){
				output = "YOU WON! ";
				mDrawView.text.setColor(Color.GREEN);
				mDrawView.text.setTextSize(50);
				exit=true;
			}
		}
		draw(userpos,competitorpos,output,competspeed);
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
		public void convert (String [] string, List<Double> list){
			for (int i=0;i<string.length;i++){
				list.add(Double.parseDouble(string[i]));
			}
			System.out.println("allValues: " + list);
		}
		
		//****************** END Methods used for saving & loading from file *********************
		
		//****************** Methods used for drawing in the canvas *********************
		
		public void draw(int point1, int point2, String text, double speed){			
			//paint line and image moving across the line
			mDrawView.userpoint = point1;
			mDrawView.competitorpoint = point2;
			mDrawView.output = text;
			mDrawView.competitorspeed = speed;
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
		
		//********************************************************* END LOCATION METHODS **************************************************************************
		
		//********************************************************* SENSOR METHODS **************************************************************************
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
					mAccelerometer = event.values.clone();
					System.out.println("mAccelerometer: " + display(mAccelerometer));
					mSensorDataUpdatedTime = new Date();
			}
			if(event.sensor.getType()==Sensor.TYPE_GRAVITY && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
				mGravity = event.values.clone();
				System.out.println("mGravity: " + display(mGravity));
				mSensorDataUpdatedTime = new Date();
			}
			if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
				mGyroscope = event.values.clone();
				System.out.println("mGyroscope: " + display(mGyroscope));
				mSensorDataUpdatedTime = new Date();
			}
			if(event.sensor.getType()==Sensor.TYPE_LIGHT && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
				mLight = event.values.clone();
				System.out.println("mLight: " + mLight[0]);
				mSensorDataUpdatedTime = new Date();
			}
			if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
				mLinearAcceleration = event.values.clone();
				System.out.println("mLinearAcceleration: " + display(mLinearAcceleration));
				mSensorDataUpdatedTime = new Date();
			}if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR && new Date().getTime() - mSensorDataUpdatedTime.getTime() > 1000){
				mRotationVector = event.values.clone();
				System.out.println("mRotationVector: " + display(mRotationVector));
				mSensorDataUpdatedTime = new Date();
			}
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
		//This method is created because the sensor send back information typically on three axis
		String display(float[] values){
			return "\n" + values[0] + "\n" + values[1] + "\n" + values[2] + "\n"; 
		}
		//********************************************************* END SENSOR METHODS **************************************************************************

		
}
