package com.example.matting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DrawImageView extends ImageView{

	public DrawImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	Paint paint = new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);
		paint.setAlpha(100);
	};
	
	
	protected void myDraw(Canvas canvas) {
		
		canvas.drawRect(new Rect(100, 200, 400, 500), paint);
	}
}
