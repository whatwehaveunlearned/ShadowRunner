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
	int userpoint=50;
	int competitorpoint=50;
	//Initial Speeds
	double userspeed = 0.0;
	double competitorspeed = 0.0;
	//Rectangles to hold the runner images and the different images drawn
	RectF user;
	RectF competitor;
	RectF speed;
	Bitmap userimage = BitmapFactory.decodeResource(getResources(), R.drawable.green);
	Bitmap competitorimage = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
	Bitmap flagimage = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
	Bitmap speedimage = BitmapFactory.decodeResource(getResources(), R.drawable.speedeffect);
	//Initial position of the rectangles
	int userleft = 50;
	int usertop = 90;
	int userright = 150;
	int userbottom = 190;
	int competitorleft = 50;
	int competitortop = 130;
	int competitorright = 150;
	int competitorbottom = 230;
	
	
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
		//Create the actual rectangles with the positions defined initially
		competitor = new RectF(userleft,usertop,userright,userbottom);
		user = new RectF(competitorleft,competitortop,competitorright,competitorbottom);
		speed = new RectF(competitorleft,competitortop+20,competitorright,competitorbottom-20);
	}
	
	public void onDraw(Canvas canvas){
		//Create the lines representing road
		canvas.drawLine(50,130,560,130, lines);
		canvas.drawLine(50,230,560,230,lines);
		//Move the user alog the road
		user.offsetTo(userpoint, usertop);
		competitor.offsetTo(competitorpoint, competitortop);
		//And the images to the user and competitor rectangles
		System.out.println("compSpeed: " + competitorspeed);
		System.out.println("userSpeed: " + userspeed);
		if (competitorspeed>1){
			speed.offsetTo(competitorpoint-50, competitortop+20);
			canvas.drawBitmap(speedimage, null, speed, rectpaint);
		}else if(userspeed>1){
			speed.offsetTo(userpoint-50, usertop+20);
			canvas.drawBitmap(speedimage, null, speed, rectpaint);
		}
		canvas.drawBitmap(userimage, null, user, rectpaint);
		canvas.drawBitmap(competitorimage, null, competitor, rectpaint);
		//Adding information text
		//canvas.drawText(start, 10, 180, paint);
		//canvas.drawText(goal, 530, 180, paint);
		canvas.drawText(output, 220, 320, text);
	}

}
