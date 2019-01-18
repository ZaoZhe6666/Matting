package com.example.matting;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Templates;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.app.Notification.MessagingStyle.Message;
import android.database.CharArrayBuffer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.text.style.TtsSpan;
import android.util.Log;
import okio.ByteString;



public class Upload {
	private static final String TestTAG = "TestLog";
	private static Handler handler = new Handler();
	
	// 通过socket与RequestURL建立连接，发送文件file
	@SuppressLint("NewApi")
	public static String SocketUploadImage(File file, int port) {
		Log.d(TestTAG, "SocketSendImg");
		Socket socket;
		String res = "";
		try {
			// 创建Socket 指定服务器IP和端口号
			socket = new Socket(MainActivity.LocalHost, port);
			
			// 创建InputStream用于读取文件
			InputStream inputFile = new FileInputStream(file);
			
			// 创建Socket的OutputStream用于发送数据
			OutputStream outputConnect = socket.getOutputStream();
			
			// 发送背景色号
			outputConnect.write(MainActivity.Color.getBytes());
			outputConnect.flush();
			
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
			
			// 接收合法性信息
		    byte symCodeBuff[] = new byte[10];
		    int symCode = inputConnect.read(symCodeBuff);
		    if(symCode == 1) { // 色号错误
		    	
		    }
		    else if(symCode == 2) { // 原始图片有误
		    	
		    }
		    else if(symCode == 3) { // 未检测到人脸
		    	
		    }
			
		    if(symCode != 0) {
		    	inputConnect.close();
		    	outputConnect.close();
				socket.close();
				return "" + symCode;
		    }
			
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
			outputConnect.close();
			outputStream.close();
			
			// 关闭连接
			socket.close();
			Log.d(TestTAG, "Get Img success.The result is " + filePath);
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, offset);
			Log.d(TestTAG, "bitmap is ok");
			android.os.Message message = handler.obtainMessage(1, bitmap);
			Log.d(TestTAG, "message is ok");
			handler.sendMessage(message);
			Log.d(TestTAG, "handler is ok");
			
			res =  filePath;
		}catch(Exception e) {
			Log.d(TestTAG, "catch error:" + e.getMessage());
		}
		return "";
	}
	
	// 通过HttpPost 向RequestURL 发送文件file
	public static String HttpUploadImage(File file, String RequestURL) {
		String result = "error";
		
		try {
			// 新建POST请求
			HttpPost post = new HttpPost(RequestURL);
			Log.d(TestTAG, "post new success");

			// 图片转换为二进制流
			if(file == null) {
				Log.d(TestTAG, "file is null!");
			}
			Log.d(TestTAG, file.getAbsolutePath());
			FileInputStream fin = new FileInputStream(file);
			byte[] bytes = new byte[fin.available() + 20];
			fin.read(bytes);
			fin.close();
			Log.d(TestTAG, bytes.toString());
			Log.d(TestTAG, "byte trans success");
			String index = new String("index=".getBytes(), "iso-8859-1");
			index += new String(bytes, "iso-8859-1");
			
			// 设定POST传送参数
			post.setHeader("Content-Type", "application/octet-stream; charset=UTF-8");
			post.setEntity(new ByteArrayEntity(index.getBytes()));
			Log.d(TestTAG, "post set over");
			
			// 打开客户端
			DefaultHttpClient client = new DefaultHttpClient();
			Log.d(TestTAG, "client set");
			
			// 发送并接受反馈
			HttpResponse response = client.execute(post);
			Log.d(TestTAG, "upload success");
		}
		catch(Exception e) {
			Log.d(TestTAG, "catch: " + e.getMessage());
		}
		return result;
	}
	
	// 访问urlPath并获得返回值
	public static int GetFromURL(String urlPath) {
		try {
			Log.d(TestTAG, "Try to get from " + urlPath);
			// 设置参数
//			List<NameValuePair> list = new LinkedList<NameValuePair>();
//			BasicNameValuePair param1 = new BasicNameValuePair("name", "root");
//			list.add(param1);
			
			// 建立HttpGet连接			
			HttpGet request = new HttpGet();
			request.setURI(URI.create(urlPath));
//			Log.d(TestTAG, "HttpGet success");
//			request.setHeader("name", "zz");

			// 创建客户端
			HttpClient client = new DefaultHttpClient();
//			Log.d(TestTAG, "client success");
			
			// 连接并获得结果
			HttpResponse response = client.execute(request);
//			Log.d(TestTAG, "execute success");
			// 正确GET
			if(response.getStatusLine().getStatusCode() == 200) {
				// 获得结果
				String data = EntityUtils.toString(response.getEntity(), "gbk");
				Log.d(TestTAG, "data from the url:" + data);
				return Integer.parseInt(data);
			}
		} catch (Exception e) {
			Log.d(TestTAG, "catch the :" + e.getMessage());
		}
		return 8000;
	}
	
}
