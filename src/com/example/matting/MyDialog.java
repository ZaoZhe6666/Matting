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

// 实现自定义弹出窗口
// 参考资料https://blog.csdn.net/xiao190128/article/details/53282993
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
        // 指定布局  
        this.setContentView(R.layout.change_server);  
  
        inputServer = (EditText) findViewById(R.id.text_server);  
        inputPort = (EditText) findViewById(R.id.text_port);  
        inputColor = (EditText) findViewById(R.id.text_color);
        
//        nowServer = (TextView) findViewById(R.id.server_now);
//        nowPort = (TextView) findViewById(R.id.port_now);
//        nowColor = (TextView) findViewById(R.id.color_now);
       
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值  
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6  
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8  
        dialogWindow.setAttributes(p);  
  
        this.setCancelable(true);  
    }  
}
