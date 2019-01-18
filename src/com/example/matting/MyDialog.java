package com.example.matting;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

// ʵ���Զ��嵯������
// �ο�����https://blog.csdn.net/xiao190128/article/details/53282993
public class MyDialog extends Dialog{
	private Activity context;
	private EditText inputServer;
	private EditText inputPort;
	private EditText inputColor;
	
	private String TAG = "TestLog";
	
//	private TextView nowServer;
//	private TextView nowPort;
//	private TextView nowColor;
	
	private View.OnClickListener mClickListener;
	
	public MyDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public MyDialog(Activity context, int theme, View.OnClickListener clickListener) {
		super(context, theme);  
        this.context = context;  
        this.mClickListener = clickListener;  
	}
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        // ָ������  
        this.setContentView(R.layout.change_server);  
  
        inputServer = (EditText) findViewById(R.id.text_server);  
        inputPort = (EditText) findViewById(R.id.text_port);  
        inputColor = (EditText) findViewById(R.id.text_color);
        
//        nowServer = (TextView) findViewById(R.id.server_now);
//        nowPort = (TextView) findViewById(R.id.port_now);
//        nowColor = (TextView) findViewById(R.id.color_now);
       
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ  
        // p.height = (int) (d.getHeight() * 0.6); // �߶�����Ϊ��Ļ��0.6  
        p.width = (int) (d.getWidth() * 0.8); // �������Ϊ��Ļ��0.8  
        dialogWindow.setAttributes(p);  
  
        this.setCancelable(true);  
    }  
}
