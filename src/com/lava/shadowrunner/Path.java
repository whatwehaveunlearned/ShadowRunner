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
	List<Double> timeArray = new ArrayList<Double>();
	//Create a list to hold Accelerations
	List<Double> accelerationArray = new ArrayList<Double>();
	//Is Speeding variables
	boolean IsSpeeding=false;
	
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
		if (locationArray.size()>1){
			for(int j=0;j<size;j++){
				distance = distance + locationArray.get(j).distanceTo(locationArray.get(j+1));
			}
		}
		distanceArray.add(distance);
		System.out.println("distanceArray" + distanceArray);
		return distance;
		
	}
	
	//Method to calculate media velocity of all the past Locations
	public double MediaSpeed(){
		double speed = 0;
		
		int  size = locationArray.size()-1;
		if (locationArray.size()>1){
			for(int j=0;j<size;j++){
				//Calculate speed
				if (distanceArray.size()>1){
					speed=speed+CalculateSpeed(locationArray.get(j),locationArray.get(j+1));
				}
			}
		}
		speed=speed/size;
		speedArray.add(speed);
		System.out.println("SpeedArray: "+ speedArray);
		return speed;
		
	}
	
	
	
	
	//Method to calculate media acceleration of all the past Locations
	public double MediaAcc(){
		double acceleration = 0;
		int  size = speedArray.size()-1;
		System.out.println("size speed: "+ speedArray.size());
		if (speedArray.size()>1){
			for(int j=0;j<size;j++){
				System.out.println("j: "+ j);
				acceleration=acceleration+Calculateacceleration(speedArray.get(j),speedArray.get(j+1),timeArray.get(j));
			}
			acceleration=acceleration/size;
		}
		accelerationArray.add(acceleration);
		System.out.println("AccelerationArray: "+ accelerationArray);
		return acceleration;
		
	}
	
	//Method to calculate speed at each location
	private double CalculateSpeed(Location location1, Location location2){
		double speed = 0;
		double time = location2.getTime()-location1.getTime()/1000;//In seconds
		
		//speed=(pos2-pos1)/time(sec)
		speed = location1.distanceTo(location2) / (time);
		System.out.println("Speed: "+ speed);
		timeArray.add(time);
		return speed;
	}
	
	//Method to calculate acceleration at each location
	private double Calculateacceleration(double Vi, double V, double t){
		double acceleration=0;
		
		acceleration = V-Vi/Math.pow(t, 2);
		return acceleration;
	}
	
}
