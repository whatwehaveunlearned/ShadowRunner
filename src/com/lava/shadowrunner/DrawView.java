//Class that does the canvas drawing (Paints the bitmap image in screen)
package com.lava.shadowrunner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class DrawView extends View{
	//Paints
	Paint rectpaint;
	Paint lines;
	Paint text;
	//Used Strings
	String start = "START";
	String goal = "GOAL";
	String output = "...";
	//Initial Points
	int userpoint=0;
	int competitorpoint=0;
	//Initial Speeds
	double userspeed = 0.0;
	double competitorspeed = 0.0;
	//Rectangles to hold the runner images and the different images drawn
	RectF user;
	RectF competitor;
	//RectF speed;
	RectF flag;
	Bitmap userimage = BitmapFactory.decodeResource(getResources(), R.drawable.green);
	Bitmap competitorimage = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
	Bitmap flagimage = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
	Bitmap speedimage = BitmapFactory.decodeResource(getResources(), R.drawable.speedeffect);
	//Initial position of the rectangles
	int competitorleft = 50;
	int competitortop = 70;
	int competitorright = 150;
	int competitorbottom = 170;
	int userleft = 50;
	int usertop = 100;
	int userright = 150;
	int userbottom = 200;
	float [] speedlines;
	
	
	//Constructor
	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		invalidate();
		//Setting and configuring paints
		lines = new Paint();
		text = new Paint();
		rectpaint = new Paint();
		lines.setARGB(255,255,255,255);
		lines.setAntiAlias(true);
		lines.setStrokeWidth(8);
		text.setTextSize(100);
		text.setColor(Color.RED);
		rectpaint.setAntiAlias(true);
		speedlines = new float [] {competitorleft-20,competitortop,competitorright-20,competitorbottom};
		//Create the actual rectangles with the positions defined initially
		competitor = new RectF(competitorleft,competitortop,competitorright,competitorbottom);
		user = new RectF(userleft,usertop,userright,userbottom);
		//speed = new RectF(competitorleft+10,competitortop+15,competitorright,competitorbottom-30);
		flag = new RectF(540,50,620,130);
	}
	
	public void onDraw(Canvas canvas){
		//Create the lines representing road
		canvas.drawLine(50,150,610,150, lines);
		canvas.drawLine(50,180,610,180,lines);
		//Move the user along the road
		competitor.offsetTo(competitorpoint, competitortop);
		user.offsetTo(userpoint, usertop);
		//And the images to the user and competitor rectangles
		System.out.println("compSpeed: " + competitorspeed);
		System.out.println("userSpeed: " + userspeed);
		if (competitorspeed>1){
			//speed.offsetTo(competitorpoint-30, competitortop+15);
			canvas.drawLines(speedlines, lines);
			//canvas.drawBitmap(speedimage, null, speed, rectpaint);
		}else if(userspeed>1){
			//speed.offsetTo(userpoint-30, usertop+20);
			//canvas.drawBitmap(speedimage, null, speed, rectpaint);
			canvas.drawLines(speedlines, lines);
		}
		canvas.drawBitmap(flagimage, null, flag, rectpaint);
		canvas.drawBitmap(competitorimage, null, competitor, rectpaint);
		canvas.drawBitmap(userimage, null, user, rectpaint);
		//Adding information text
		canvas.drawText(output, 220, 320, text);
	}

}
