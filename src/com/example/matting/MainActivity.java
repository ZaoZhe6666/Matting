package com.example.matting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Picture;
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

public class MainActivity extends Activity{

	private static final String TAG = "MainActvity";
	private static final String TestTAG = "TestLog";
	private static File photo;
	public static final String LocalHost = "219.224.168.78";
	public static int port = 5000;
//	public static final String LocalHost = "192.168.1.106";
//	public static int port = 8000;
	
	private ImageView ivImage;
	
	
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

    }
	
	
	
	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_front);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		
		ivImage.setVisibility(View.INVISIBLE);
		
//		sh = sView.getHolder();
//		sh.addCallback(this);
//		sh.setFormat(PixelFormat.TRANSLUCENT);
		
		// ����дȨ��
		checkPermission();
		//ѡ������ģʽ��ť
		Button btn_call_camera = (Button)findViewById(R.id.btn_call_camera);
		
		btn_call_camera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Log.d(TestTAG, "click camera");
				// �򿪾��λ���
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, UseCameraActivity.class);
				startActivityForResult(intent, 100);
			}
		});
		
		// ��ͼ���ϴ�
		Button btn_photo = (Button)findViewById(R.id.btn_photo);

		btn_photo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//�л����鿴��������¼�
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
			}
		});
		

		// �鿴Matting�ļ���
		Button btn_Album = (Button)findViewById(R.id.btn_album);
		
		btn_Album.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
				startActivityForResult(intent, 400);
			}
		});
		
		// �鿴ͼƬ���
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
		if(requestCode == 100 || requestCode == 200) {// ����
			Log.d(TestTAG, "deal the camera/select");
			// ����ϵͳͼ��
			try {
				MediaStore.Images.Media.insertImage(null, photo.getAbsolutePath(), photo.getName(), null);
			} catch (FileNotFoundException e) {}
			
			Uri uri = Uri.fromFile(photo);
			Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			intent.setData(uri);
			Log.d(TestTAG, "deal ok");
			sendBroadcast(intent);
		}
		if(result == -1 && requestCode == 300) { 
			
			// �鿴�������ͼƬ -> �������ӷ���ͼƬ -> ����ͼƬ
			final Uri uri = data.getData();
			
			String sendPath = UriDeal.Uri2Path(MainActivity.this, uri);
			Log.d(TestTAG, "img path " + sendPath);
			File uploadFile = new File(sendPath);
			new Thread(new SocketSendGetThread(uploadFile)).start();
		}
		if(result == -1 && requestCode == 400) {
			// ��ʾͼƬ
			Uri uri = data.getData();
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(uri, "image/*");
			startActivity(intent);
		}
	}
	
	
	// ͼƬ�շ�����ԭҳ��������
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
	
	// ͨ��Socket����ͼƬ�շ�
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
				// ����Socket ָ��������IP�Ͷ˿ں�
				socket = new Socket(MainActivity.LocalHost, port);
				
				// ����InputStream���ڶ�ȡ�ļ�
				InputStream inputFile = new FileInputStream(file);
				
				// ����Socket��OutputStream���ڷ�������
				OutputStream outputConnect = socket.getOutputStream();
				
				// �����ļ���С
				long fileSize = inputFile.available();
				String fileSizeStr = fileSize + "";
				outputConnect.write(fileSizeStr.getBytes());
				outputConnect.flush();
				
				//�������ļ�תΪbyte����
				byte buffer[] = new byte[4 * 1024];
				int tmp = 0;
				// ѭ����ȡ�ļ�
				while((tmp = inputFile.read(buffer)) != -1) {
					outputConnect.write(buffer, 0, tmp);
				}
				
				// ���Ͷ�ȡ���ݵ������
				outputConnect.flush();
				
				// �ر�������
				inputFile.close();
		
		// ͨ��socket��RequestURL�������ӣ�������һ��ͼƬ�浽����
				Log.d(TestTAG, "SocketGetImg");
				
				// ����Socket��InputStream������������
				InputStream inputConnect = socket.getInputStream();
				Log.d(TestTAG, "break1");
				// ��λ���·��
				File dir = new File(Environment.getExternalStorageDirectory(), "Matting");
			    if(dir.exists() && dir.isFile()) {
			    	dir.delete();
			    }
			    if(!dir.exists()) {
			    	dir.mkdir();
			    }
			    
			    // ʹ��ʱ����Ϊ���
			    Date date = new Date(System.currentTimeMillis());
			    Log.d(TestTAG, "break in date");
			    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
			    Log.d(TestTAG, "break in sdf");
			    String filePath = dir.getAbsolutePath() + "/Receive_" + dateFormat.format(date) + ".jpg";
			    Log.d(TestTAG, "break2");
			    FileOutputStream outputStream = new FileOutputStream(filePath);
			    Log.d(TestTAG, "break3");
			    
			    // ��ȡ�����ļ���С
			    byte piclenBuff[] = new byte[200];
			    int picLen = inputConnect.read(piclenBuff);
			    String picLenStr = new String(piclenBuff, 0, picLen);
			    picLen = Integer.valueOf(picLenStr);
			    Log.d(TestTAG, "fileSize is:" + picLen);
			    
			    // ����ȷ����Ϣ
			    outputConnect.write("receive".getBytes());
			    outputConnect.flush();
			    
			    // ��ȡ�����ļ�
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
				
				// �ر�����
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
	
	public static void setPhoto(File file) {
		photo = file;
	}
	
	
	public void callCamera(View view) {
		Log.d(TAG, "call the camera ...");
	}
	
	public void viewPhoto(View view) {
		Log.d(TAG, "view the photo ...");
	}

}
