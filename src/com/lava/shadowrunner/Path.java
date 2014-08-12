//Class to add Locations and calculate Path between x Locations

package com.lava.shadowrunner;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Path {
	
	//Create a list to hold the locations
	List<Location> locationArray = new ArrayList<Location>();
	//Create a list to hold the distances
	List<Double> distanceArray = new ArrayList<Double>();
	//Create a list to hold Speeds
	List<Double> speedArray = new ArrayList<Double>();
	//Create a list to hold Time of each Location
	List<Long> timeArray = new ArrayList<Long>();
	
	//Empty Constructor
	public Path () {
	} 
	
	//Method to add a location
	public void addLocation(Location location){
		locationArray.add(location);
		timeArray.add(location.getTime());
		System.out.println("TimeArray" + timeArray);
	}
	
	//Method to calculate distance of all the past Locations
	public double distance(){
		double distance = 0;
		int  size = locationArray.size()-1;

		for(int j=0;j<size;j++){
			distance = distance + locationArray.get(j).distanceTo(locationArray.get(j+1));
			//Calculate speed
			if (distanceArray.size()>1){
				System.out.println("timeArray: "+ timeArray);
				speedArray.add(Speed(locationArray.get(j).distanceTo(locationArray.get(j+1)),timeArray.get(j),timeArray.get(j+1)));
			}
		}
		System.out.println("distance.length: "+ distanceArray.size());
		System.out.println("location.length: "+ locationArray.size());
		System.out.println("distanceArray" + distanceArray);
		System.out.println("SpeedArray: "+ speedArray);
		distanceArray.add(distance);
		return distance;
		
	}
	
	//Method to calculate speed each location
	public double Speed(double distance,double time1,double time2){
		double speed = 0;
		//speed=(pos2-pos1)/time(s)
		speed = distance / (time2-time1)*1000;
		System.out.println("Speed: "+ speed);
		return speed;
	}
	
}
