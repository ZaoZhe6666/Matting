package com.example.matting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class UseCameraActivity extends Activity{ 
	// �ο�����http://blog.sina.com.cn/s/blog_4e6922ab01010gfz.html
	// �ο�����https://blog.csdn.net/yanzi1225627/article/details/8577756
	
	// ҳ�水ť
	private Button addRectBtn, closeBtn, takeBtn;
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
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TestTag, "surface Creat");

				if(Build.VERSION.SDK_INT <= 10)
				     holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
	public void initCamera() {
		if(null != myCamera && !hasCreateCamera) {
			Camera.Parameters parameters = myCamera.getParameters();
			
			parameters.setPictureFormat(PixelFormat.JPEG);
			
//			parameters.setPictureSize(480, 640);
//			parameters.setPreviewSize(480, 640);
			
			myCamera.setDisplayOrientation(90);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			myCamera.setParameters(parameters);
			Log.d(TestTag, "param set ok");
			myCamera.startPreview();
			Log.d(TestTag, "preview start");
		}
		hasCreateCamera = true;
	}
	
	// ��ʱ�洢������
	PictureCallback myJpegCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Log.d(TestTag, "Temperary Save the Pic byte[]");
			tmpPic = data;
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
		    
		    // bitmap�и�Ϊ480 x 640
		    int widthBit = bitmap.getWidth();
		    int heightBit = bitmap.getHeight();
		    int tmpRow = 0, tmpCol = 0;
		    if(widthBit >= finalWidth) {
		    	tmpRow = widthBit / 2 - 240;
		    	widthBit = finalWidth;
		    }
		    if(heightBit >= finaleHeight) {
		    	tmpCol = heightBit / 2 - 320;
		    	heightBit = finaleHeight;
		    }
		    Bitmap bitmapCut = Bitmap.createBitmap(bitmap, tmpRow, tmpCol, widthBit, heightBit);
		    
		    bitmapCut.compress(Bitmap.CompressFormat.JPEG, 100, buffStream);
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
