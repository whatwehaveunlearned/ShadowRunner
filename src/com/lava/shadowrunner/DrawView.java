//Class that does the canvas drawing (Paints the bitmap image in screen)
package com.lava.shadowrunner;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class DrawView extends View{
	
	List<Square> points= new ArrayList<Square>();
	List<Paint> paints = new ArrayList<Paint>();
	Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.runner);
	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		invalidate();
	}
	
	public void onDraw(Canvas canvas) {
		int i=0;
		for(Square point : points){
			Rect rect = new Rect();
			rect.left=(int) point.left;
			rect.top=(int) point.top;
			rect.right=(int) point.right;
			rect.bottom=(int) point.bottom;
			canvas.drawBitmap(image, null, rect, paints.get(i++));
		}
		
	}
	
	class Square {
		float left, top,right,bottom;
	}

}
