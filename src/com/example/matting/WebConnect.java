package com.example.matting;

import android.text.TextDirectionHeuristic;
import android.util.Log;

public class WebConnect extends Thread{
	// 单例模式的网络连接中间键(Android4.4后规定主线程不可发送网络请求)
	private static WebConnect instance = null;
	private int port;
	private WebConnect() {
	}
	
	public static WebConnect getInstance() {
		if(instance == null) {
			synchronized (WebConnect.class) {
				if(instance == null) {
					instance = new WebConnect();
				}
			}
		}
		return instance;
	}
	
	public void getFromURL(String url) {
		port = Upload.GetFromURL(url);
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public int getPort() {
		return this.port;
	}
}
