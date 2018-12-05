package com.example.matting;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;

public class SVDraw extends SurfaceView implements Callback{
	protected SurfaceHolder sh;
	private int mWidth = 0;
	private int mHeight = 0;
	public SVDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		sh = getHolder();
		sh.addCallback(this);
		sh.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
		
		// 获得屏幕大小
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mWidth = wm.getDefaultDisplay().getWidth();
		mHeight = wm.getDefaultDisplay().getHeight();
	}
	
	void clearDraw() {
		Canvas canvas = sh.lockCanvas();
		canvas.drawColor(Color.BLUE);
		sh.unlockCanvasAndPost(canvas);
	}
	
	public void drawRect() {
		Canvas canvas = sh.lockCanvas();
		canvas.translate(mWidth / 2, mHeight / 2);
		canvas.drawColor(Color.TRANSPARENT);
		
		
		
		// 红色提示矩形框
		Paint redPaint = new Paint();
		redPaint.setAntiAlias(true);
		redPaint.setColor(Color.RED);
		redPaint.setStyle(Style.STROKE);
		redPaint.setStrokeWidth(2.5f);
		redPaint.setAlpha(100);
		
		canvas.drawRect(new Rect(-240, -320, 240, 320), redPaint);
		
		// 绿色人物提示框
		Paint greenPaint = new Paint();
		greenPaint.setAntiAlias(true);
		greenPaint.setColor(0xFFA4C739);
		greenPaint.setStyle(Paint.Style.STROKE);
		
		// 头部
		RectF rectf_head = new RectF(-150, -250, 150, 10);
		canvas.drawArc(rectf_head, 190, 160, false, greenPaint);
		
		// 下颌
		RectF rectf_mouse = new RectF(-110, 80, 110, 215);
		canvas.drawArc(rectf_mouse, 20, 140, false, greenPaint);
		
		
		// 衬衣
		canvas.drawLine(-94, 210, -200, 305, greenPaint);
		canvas.drawLine(94, 210, 200, 305, greenPaint);
		
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
