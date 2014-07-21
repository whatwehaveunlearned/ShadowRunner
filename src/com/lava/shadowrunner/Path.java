//Class to add Locations and calculate Path between x Locations

package com.lava.shadowrunner;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;

public class Path {
	
	//Create a list to hold the locations
	List<Location> locationArray = new ArrayList<Location>();
	
	//Constructor
	public Path () {
	} ; 
	
	//Method to add a location
	public void addLocation(Location location){
		locationArray.add(location);
	}
	
	//Method to calculate distance
	public double distance(){
		double distance = 0;
		int j;
		int  size = locationArray.size()-1;
		System.out.println("SIZE: " + locationArray.size());
		
		System.out.println("PathDistance " + locationArray);
		for(j=0;j<size;j++){
			distance = distance + locationArray.get(j).distanceTo(locationArray.get(j+1));
			System.out.println("for " + j + " " + locationArray.get(j).distanceTo(locationArray.get(j+1)));
		}
		
		System.out.println("PathDistance " + distance);
		return distance;
		
	}
	
}
