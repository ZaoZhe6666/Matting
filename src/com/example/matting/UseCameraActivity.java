package com.example.matting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.strictmode.InstanceCountViolation;
import android.provider.MediaStore;
import android.provider.Telephony.Mms.Rate;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class UseCameraActivity extends Activity{ 
	// �ο�����http://blog.sina.com.cn/s/blog_4e6922ab01010gfz.html
	// �ο�����https://blog.csdn.net/yanzi1225627/article/details/8577756
	
	// ҳ�水ť
	private Button addRectBtn, changeBtn, takeBtn;
	private String TestTag = "TestLog";
	
	// ���㾵ͷ�������
	private SurfaceHolder mySurfaceHolder = null;
	private SurfaceView mySurfaceView = null;
	private SVDraw myDraw = null;
    private Camera myCamera = null;
    private boolean hasCreateCamera = false;
    
    private boolean startLine = false;
    private boolean Retake = false;
    
    private byte[] tmpPic;
    private static int finalWidth = 480;
    private static int finaleHeight = 640;
    private int optionWidth = 0;
    private int optionHeight = 0;
	
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TestTag, "in Use Camera Activity");
        // ȡ������
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.camera);
        
        // ��ʼ������
        myDraw = (SVDraw) findViewById(R.id.drawIV);
        myDraw.setVisibility(View.INVISIBLE);

        // ��ʼ��SurfaceView
        mySurfaceView = (SurfaceView) findViewById(R.id.cameraSV);
        mySurfaceHolder = mySurfaceView.getHolder();
        mySurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mySurfaceHolder.addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d(TestTag, "surface destroy");
				if(null != myCamera) {
					myCamera.setPreviewCallback(null);
					myCamera.stopPreview();
					hasCreateCamera = false;
					myCamera.release();
					myCamera = null;
				}
			}
			
			@SuppressLint("NewApi")
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TestTag, "surface Creat");

				if(Build.VERSION.SDK_INT <= 10)
				     holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
		                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
		            }
		        }
				if(!hasCreateCamera) {
					myCamera = Camera.open();
				}
				try {
					myCamera.setPreviewDisplay(mySurfaceHolder);
					Log.d(TestTag, "camera create!");
				}catch(Exception e) {
					if(null != myCamera) {
						myCamera.release();
						myCamera = null;
					}
					Log.d(TestTag, "catch in surface created" + e.getMessage());
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				initCamera();
				Log.d(TestTag, "surface changed");
			}
		});
//        mySurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        // �����㰴ť
        takeBtn = (Button) findViewById(R.id.btnPhoto);

//      takeBtn.setOnClickListener(new PhotoOnclickListener());
        takeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Retake) { // ���ռ�
					if(hasCreateCamera && myCamera != null) {
						myCamera.takePicture(null, null, myJpegCallback);
						takeBtn.setText("����");
						addRectBtn.setText("����");
						Retake = true;
					}
				}
				else { // �����
					saveJpeg();
					finish();
				}
				
			}
		});
        
        // ��Ԥ����ť
        addRectBtn = (Button) findViewById(R.id.btnAddLine);
        addRectBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Retake) { // ����
					Retake = false;
					if(startLine) {
						addRectBtn.setText("ȡ��������");
					}
					else {
						addRectBtn.setText("������");
					}
					takeBtn.setText("����");
					myCamera.startPreview();
				}
				else if(!startLine) { // ��Ӹ�����
					initCamera();
					myDraw.setVisibility(View.VISIBLE);
					myDraw.drawRect();
					addRectBtn.setText("ȡ��������");
					startLine = true;
				}
				else { // �رո�����
					myDraw.setVisibility(View.INVISIBLE);
					addRectBtn.setText("������");
					startLine = false;
				}
			}
		});
        
    }

	// ��ʼ�����
	// �ο�����https://blog.csdn.net/u012539700/article/details/79889348
	public void initCamera() {
		if(null != myCamera && !hasCreateCamera) {
			Camera.Parameters parameters = myCamera.getParameters();
			
			parameters.setPictureFormat(PixelFormat.JPEG);
			
			List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
			Camera.Size optionSize = getPreviewSize(sizeList, mySurfaceView.getHeight(), mySurfaceView.getWidth());
//			parameters.setPictureSize(480, 640);
			parameters.setPreviewSize(optionSize.width, optionSize.height);
			optionWidth = optionSize.width;
			optionHeight = optionSize.height;
			
			myCamera.setDisplayOrientation(90);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			myCamera.setParameters(parameters);
			Log.d(TestTag, "param set ok");
			myCamera.startPreview();
			Log.d(TestTag, "preview start");
		}
		hasCreateCamera = true;
	}
	
	// ����������ݱ�������
	private Camera.Size getPreviewSize(List<Camera.Size> sizes, int width, int height){
		final double TOLERANCE = 0.1;
		double targetRatio = (double) width / height;
		Camera.Size res = null;
		double minDiff = Double.MAX_VALUE;
		
		for(Camera.Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if(Math.abs(ratio - targetRatio) > TOLERANCE) continue;
			if(Math.abs(ratio - targetRatio) < minDiff) {
				minDiff = Math.abs(ratio - targetRatio);
				res = size;
			}
		}
		
		if(res == null) {
			for(Camera.Size size : sizes) {
				if(Math.abs(size.height - height) < minDiff) {
					res = size;
					minDiff = Math.abs(size.height - height);
				}
			}
		}
		return res;
	}
	
	// ��ʱ�洢������
	PictureCallback myJpegCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TestTag, "Temperary Save the Pic byte[]");
			tmpPic = data;
			myCamera.stopPreview();
		}
	};
	
	// ���ش洢��������������ǰ�
	public void saveJpeg() {
		try {
			Log.d(TestTag, "Save in ./Matting");
			// ��λȷ�������ļ���
			File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
		    if(dir.exists() && dir.isFile()) {
		    	dir.delete();
		    }
		    if(!dir.exists()) {
		    	dir.mkdir();
		    }
	    
			// ����ʱ�����Ϊ�ļ���
			Date date = new Date(System.currentTimeMillis());
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
		    String filePath = dir.getAbsolutePath() + "/Take_" + dateFormat.format(date) + ".jpg";
		    File file = new File(filePath);
		    file.createNewFile();
		    Log.d(TestTag, "FilePath:" + filePath);
		    
		    // byte����תbitmap�洢
		    Bitmap bitmap = BitmapFactory.decodeByteArray(tmpPic, 0, tmpPic.length);
		    BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(file));
		    Log.d(TestTag, "First Bitmap is set :" + bitmap.getWidth() + ":" + bitmap.getHeight());
		    
		    // bitmap ��ת
		    Matrix matrix = new Matrix();
		    matrix.postRotate(90);
		    Bitmap bitmapRoute = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		    bitmap.recycle();
		    Log.d(TestTag, "Route Bitmap is set :" + bitmapRoute.getWidth() + ":" + bitmapRoute.getHeight());

		    // bitmap�и�Ŀ������
		    int tmpRow = 0, tmpCol = 0;
		    int widthBit = bitmapRoute.getWidth();
		    int heightBit = bitmapRoute.getHeight();
		    double ratio = (double) widthBit / SVDraw.getWindowWidth();
		    Log.d(TestTag, "Option Size ���� " + optionWidth + ":" + optionHeight);
		    Log.d(TestTag, "SVD Size ���� " + SVDraw.getWindowWidth() + ":" + SVDraw.getWindowHeight());
		    Log.d(TestTag, "Bitmap Size ���� " + widthBit + ":" + heightBit);
		    Log.d(TestTag, "Cut ratio:" + ratio);

		    // ��ͼƬ���п�ѡ���� 720:960
		    if(widthBit >= (int)(720 * ratio)) {
		    	tmpRow = widthBit / 2 - (int)(360 * ratio);
		    	widthBit = (int)(finalWidth * ratio * 1.5);
		    }
		    if(heightBit >= (int)(960 * ratio + 500)) {
		    	tmpCol = heightBit / 2 - (int)(480 * ratio + 250);
		    	heightBit = (int)(finaleHeight * ratio * 1.5);
		    }
		    
		    
		    // �и�Ŀ��
		    Bitmap bitmapCut = Bitmap.createBitmap(bitmapRoute, tmpRow, tmpCol, widthBit, heightBit);
		    bitmapRoute.recycle();
		    Log.d(TestTag, "Final Bitmap is set :" + bitmapCut.getWidth() + ":" + bitmapCut.getHeight());
		   
		    // bitmap����
		    Bitmap bitmapScale = Bitmap.createScaledBitmap(bitmapCut, 480 , 640, true);
		    bitmapCut.recycle();
		    Log.d(TestTag, "Scale Bitmap is set :" + bitmapScale.getWidth() + ":" + bitmapScale.getHeight());
		    
		    
		    bitmapScale.compress(Bitmap.CompressFormat.JPEG, 100, buffStream);
		    buffStream.flush();
		    buffStream.close();
		    
		    // Զ��ˢ��
		    MainActivity.setPhoto(file);
		    Log.d(TestTag, "Saved Success");
		} catch (Exception e) {
			Log.d(TestTag, "catch in saveJpeg:" + e.getMessage());
		}
	}
}
 