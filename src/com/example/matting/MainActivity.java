package com.example.matting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.icu.util.Measure;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements Callback {

	private static final String TAG = "MainActvity";
	private static final String TestTAG = "TestLog";
	private File photo;
	public static final String LocalHost = "219.224.168.78";
	public static int port = 5000;
//	public static final String LocalHost = "192.168.1.106";
//	public static int port = 8000;
	
	private ImageView ivImage;
	private SurfaceView sView;
	private SVDraw svDraw;
	private android.hardware.Camera mCamera;
	protected SurfaceHolder sh;
	private boolean cameraHasCreate = false;
	
	private PictureCallback myPictureCallback = new PictureCallback(){
		@Override
		public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
			// TODO Auto-generated method stub
			Log.d(TestTAG, "in save?");
		}
	};
	
	@SuppressLint("NewApi")
	private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int write = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 300);
            } else {
                String name = "Matting";
                File file1 = new File(Environment.getExternalStorageDirectory(), name);
                if (file1.mkdirs()) {
                    Log.d("6.0:", "permission -------------> " + file1.getAbsolutePath());
                } else {
                    Log.d("6.0", "deny -------------> fail to make file ");
                }
            }
        }

		if(Build.VERSION.SDK_INT <= 10)
		     sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
	
	
	
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_front);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		sView = (SurfaceView) findViewById(R.id.previewRec);
		svDraw = (SVDraw) findViewById(R.id.drawRec);
		
		ivImage.setVisibility(View.INVISIBLE);
		svDraw.setVisibility(View.INVISIBLE);
		sView.setVisibility(View.INVISIBLE);
		
		sh = sView.getHolder();
		sh.addCallback(this);
		
		// 检查读写权限
		checkPermission();
		//选择拍摄模式按钮
		Button btn_call_camera = (Button)findViewById(R.id.btn_call_camera);
		
		btn_call_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Log.d(TestTAG, "click camera");
				// 打开矩形画布
				initCamera();
				svDraw.setVisibility(View.VISIBLE);
				svDraw.drawRect();
				Log.d(TestTAG, "open rect");
				
				
				/*
				
				//切换至Camera事件
			    Intent intent = new Intent();
			    intent.setAction("android.media.action.IMAGE_CAPTURE");
			    File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
			    if(dir.exists() && dir.isFile()) {
			    	dir.delete();
			    }
			    if(!dir.exists()) {
			    	dir.mkdir();
			    }
			    File file_list[] = dir.listFiles();
			    String fileName = file_list.length + ".jpg";
			    photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Matting" , fileName);
			    
			    Uri uri = Uri.fromFile(photo);
			    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			    startActivityForResult(intent, 100);
			    */
			}
		});
		
		//选择相册选择按钮
		Button btn_photo = (Button)findViewById(R.id.btn_photo);
		
		btn_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//切换至Choose Photo事件
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
			    if(dir.exists() && dir.isFile()) {
			    	dir.delete();
			    }
			    if(!dir.exists()) {
			    	dir.mkdir();
			    }
			    
			    File file_list[] = dir.listFiles();
			    String fileName = file_list.length + ".jpg";
			    photo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Matting" , fileName);
				Log.d(TestTAG, photo.getAbsolutePath());
				
				
				
				Uri uri = Uri.fromFile(photo);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent, 200);
			}
		});
		
		//查看已上传图集
		Button btn_Album = (Button)findViewById(R.id.btn_album);

		btn_Album.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//切换至查看已有相册事件
				File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
			    if(dir.exists() && dir.isFile()) {
			    	dir.delete();
			    }
			    if(!dir.exists()) {
			    	dir.mkdir();
			    }
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Matting"));
				intent.setData(uri);
				intent.setType("image/*");
				startActivityForResult(intent, 300);
				Log.d(TestTAG, "End upload and download!");
			}
		});
		
		// 查看图片完毕
		ImageView imageRecover = (ImageView)findViewById(R.id.ivImage);
		imageRecover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivImage.setVisibility(View.INVISIBLE);
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int result, Intent data) {
		Log.d(TestTAG, "requeseCode = " + requestCode);
		if(result == -1 && (requestCode == 100 || requestCode == 200)) {//拍照/图库选择
			Log.d(TestTAG, "deal the camera/select");
			Uri uri = Uri.fromFile(photo);
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(uri);
			this.sendBroadcast(intent);
			Log.d(TestTAG, "deal ok");
			return;
		}
		if(result == -1 && requestCode == 300) { 
			
			// 查看已有相册图片 -> 建立连接发送图片 -> 接收图片
			final Uri uri = data.getData();
			
			String sendPath = UriDeal.Uri2Path(MainActivity.this, uri);
			Log.d(TestTAG, "img path " + sendPath);
			File uploadFile = new File(sendPath);
			new Thread(new SocketSendGetThread(uploadFile)).start();
		}
		if(result == -1 && requestCode == 400) {
			// 显示图片
			Uri uri = data.getData();
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "image/*");
			startActivity(intent);
		}
	}
	
	// 初始化相机
	// 参考资料https://blog.csdn.net/yanzi1225627/article/details/7926994
	@SuppressWarnings("deprecation")
	public void initCamera() {
		if(mCamera == null && !cameraHasCreate) {
			mCamera = android.hardware.Camera.open();
			Log.d(TestTAG, "camera open");
		}
		if(mCamera != null && !cameraHasCreate) {
			try {
				android.hardware.Camera.Parameters param;
				param = mCamera.getParameters();
				param.setPictureFormat(ImageFormat.JPEG);
				param.setPreviewSize(1280, 720);
				param.set("rotation", 90);
				mCamera.setDisplayOrientation(90);
				mCamera.setParameters(param);
				mCamera.setPreviewDisplay(sh);
				mCamera.startPreview();
			}catch(Exception e) {
				Log.d(TestTAG, "catch in initCamera:" + e.getMessage());
			}
			cameraHasCreate = true;
		}
	}
	
	
	// 图片收发后与原页面做互动
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 1) {
				Log.d(TestTAG, "in handle Message!");
				Bitmap bitmap = (Bitmap) msg.obj;
				ivImage.setImageBitmap(bitmap);
				ivImage.setVisibility(View.VISIBLE);
			}
		}
	};
	
	// 通过Socket进行图片收发
	public class SocketSendGetThread implements Runnable{
		private File file;
		public SocketSendGetThread(File file) {
			this.file = file;
		}
		@Override
		public void run() {
			Log.d(TestTAG, "SocketSendImg");
			Socket socket;
			try {
				// 创建Socket 指定服务器IP和端口号
				socket = new Socket(MainActivity.LocalHost, port);
				
				// 创建InputStream用于读取文件
				InputStream inputFile = new FileInputStream(file);
				
				// 创建Socket的OutputStream用于发送数据
				OutputStream outputConnect = socket.getOutputStream();
				
				// 发送文件大小
				long fileSize = inputFile.available();
				String fileSizeStr = fileSize + "";
				outputConnect.write(fileSizeStr.getBytes());
				outputConnect.flush();
				
				//将本地文件转为byte数组
				byte buffer[] = new byte[4 * 1024];
				int tmp = 0;
				// 循环读取文件
				while((tmp = inputFile.read(buffer)) != -1) {
					outputConnect.write(buffer, 0, tmp);
				}
				
				// 发送读取数据到服务端
				outputConnect.flush();
				
				// 关闭输入流
				inputFile.close();
		
		// 通过socket与RequestURL建立连接，并接受一张图片存到本地
				Log.d(TestTAG, "SocketGetImg");
				
				// 创建Socket的InputStream用来接收数据
				InputStream inputConnect = socket.getInputStream();
				Log.d(TestTAG, "break1");
				// 定位输出路径
				File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
			    if(dir.exists() && dir.isFile()) {
			    	dir.delete();
			    }
			    if(!dir.exists()) {
			    	dir.mkdir();
			    }
			    
			    // 使用时间作为输出
			    Date date = new Date(System.currentTimeMillis());
			    Log.d(TestTAG, "break in date");
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
			    Log.d(TestTAG, "break in sdf");
			    String filePath = dir.getAbsolutePath() + "/Receive_" + dateFormat.format(date) + ".jpg";
			    Log.d(TestTAG, "break2");
			    FileOutputStream outputStream = new FileOutputStream(filePath);
			    Log.d(TestTAG, "break3");
			    
			    // 读取接收文件大小
			    byte piclenBuff[] = new byte[200];
			    int picLen = inputConnect.read(piclenBuff);
			    String picLenStr = new String(piclenBuff, 0, picLen);
			    picLen = Integer.valueOf(picLenStr);
			    Log.d(TestTAG, "fileSize is:" + picLen);
			    
			    // 发送确认信息
			    outputConnect.write("receive".getBytes());
			    outputConnect.flush();
			    
			    // 读取接收文件
			    byte buffer2[] = new byte[picLen];
				int offset = 0;
				while(offset < picLen) {
					int len = inputConnect.read(buffer2, offset, picLen - offset);
					Log.d(TestTAG, "" + len);
					outputStream.write(buffer2, offset, len);
					offset += len;
				}
				Log.d(TestTAG, "yeah");
				inputConnect.close();
				outputStream.close();
				
				// 关闭连接
				socket.close();
				Log.d(TestTAG, "Get Img success.The result is " + filePath);
				if(filePath.equals("")) return;
				Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, offset);
				Log.d(TestTAG, "bitmap is ok");
				android.os.Message message = Message.obtain();
				message.obj = bitmap;
				message.what = 1;
				Log.d(TestTAG, "message is ok");
				handler.sendMessage(message);
				Log.d(TestTAG, "handler is ok");
				
			}catch(Exception e) {
				Log.d(TestTAG, "catch error:" + e.getMessage());
			}
			
		}
		
	}
	
	
	
	public void callCamera(View view) {
		Log.d(TAG, "call the camera ...");
	}
	
	public void viewPhoto(View view) {
		Log.d(TAG, "view the photo ...");
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
