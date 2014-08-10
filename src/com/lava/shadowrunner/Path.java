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
	
	//Empty Constructor
	public Path () {
	} 
	
	//Method to add a location
	public void addLocation(Location location){
		locationArray.add(location);
	}
	
	//Method to calculate distance of all the past Locations
	public double distance(){
		double distance = 0;
		int  size = locationArray.size()-1;

		for(int j=0;j<size;j++){
			distance = distance + locationArray.get(j).distanceTo(locationArray.get(j+1));
		}
		distanceArray.add(distance);
		return distance;
		
	}
	//Method that retrieves the distanceArray for a path
	public List<Double> readDistance(){
		return distanceArray;
	}
	
}
