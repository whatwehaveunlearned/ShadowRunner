package com.lava.shadowrunner;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class LocationActivity extends Activity implements LocationListener {
	LocationManager mLocationManager;
	Location mLocation;
	TextView mTvLocation;
	
	

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
		System.out.println("best provider:" + mLocationManager.getBestProvider(criteria, true));
		String allString="";
		
		List<String> providers = mLocationManager.getProviders(criteria, false);
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

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		mTvLocation.setText(mLocation.getLatitude() + " ," + mLocation.getLongitude());
		
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
	
	

}
