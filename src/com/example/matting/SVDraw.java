package com.example.matting;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SVDraw extends SurfaceView implements Callback{
	protected SurfaceHolder sh;
	private int mWidth = 480;
	private int mHeight = 640;
	public SVDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}
	
	void clearDraw() {
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.BLUE);
		sh.unlockCanvasAndPost(canvas);
	}
	
	public void drawRect() {
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.TRANSPARENT);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);
		paint.setAlpha(100);
		
		canvas.drawRect(new Rect(100, 200, 400, 500), paint);
		sh.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
}
