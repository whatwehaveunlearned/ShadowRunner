//Class that does the canvas drawing (Paints the bitmap image in screen)
package com.lava.shadowrunner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class DrawView extends View{
	Paint userpaint;
	Paint competitorpaint;
	Paint paint;
	String start = "START";
	String goal = "GOAL";
	int userpoint=50;
	int competitorpoint=50;
	String output = "Waiting for data";
//	List<Square> points = new ArrayList<Square>();
//	List<Paint> paints = new ArrayList<Paint>();
	Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.runner);
	
	//Constructor
	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		invalidate();
		//Setting the new paint class set color and antialiasing
		paint = new Paint();
		paint.setARGB(255,255,255,255);
		paint.setAntiAlias(true);
		paint.setTextSize(20);
		userpaint = new Paint();
		userpaint.setColor(Color.GREEN);
		userpaint.setAntiAlias(true);
		competitorpaint = new Paint();
		competitorpaint.setColor(Color.RED);
		competitorpaint.setAntiAlias(true);
	}
	
//	public void onDraw(Canvas canvas) {
//		int i=0;
//		for(Square point : points){
//			Rect rect = new Rect();
//			rect.left=(int) point.left;
//			rect.top=(int) point.top;
//			rect.right=(int) point.right;
//			rect.bottom=(int) point.bottom;
//			canvas.drawBitmap(image, null, rect, paints.get(i++));
//		}	
//	}
	
	public void onDraw(Canvas canvas){
		canvas.drawLine(50, 200, 560, 200, paint);
		//the variable is going to be the x value one will be the actual runner the other who are you running against
		canvas.drawCircle(userpoint,200,10,userpaint);
		canvas.drawCircle(competitorpoint,200,10,competitorpaint);
		canvas.drawText(start, 10, 180, paint);
		canvas.drawText(goal, 530, 180, paint);
		canvas.drawText(output, 200, 250, paint);
	}
	
	class Square {
		float left, top,right,bottom;
	}

}
