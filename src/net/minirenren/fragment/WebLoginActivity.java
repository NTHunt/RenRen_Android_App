package net.minirenren.fragment;


import net.minirenren.tools.UrlTools;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebLoginActivity extends Activity {
	private static final String apiKey="5c828b20360543df986bfac03f4fbd0e";//"a3c29006929243e19547fba648581427";
	private static final String apiSecret ="1b0f546469a949999d50d8b8e85e0cdc";//"7eb0e66e829b4c66975ee5c0141a87fc";
	private static final String loginUrl="https://graph.renren.com/oauth/authorize";// Oauth2认证地址 
	private static final String successLogin="http://graph.renren.com/oauth/login_success.html";
	
	private static final String TAG="OAuthActivity";
	private static final String REDIRECT_URL="http://graph.renren.com/oauth/login_success.html";// 重定向地址 
	private ProgressDialog progressDialog; 
    private AlertDialog alertDialog; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_login);
		WebView web_login=(WebView)findViewById(R.id.webView1);
		WebSettings webSettings=web_login.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);/////////////////////////清空缓存模式
		web_login.requestFocus();
		
		//build url
		Bundle Login_url=new Bundle();
		Login_url.putString("client_id", apiKey);
		Login_url.putString("redirect_uri", successLogin);
		Login_url.putString("response_type", "token");//登录成功返回类型有code和token类型
		Login_url.putString("display","mobile");
		Login_url.putString("scope", "read_user_feed,publish_comment");//自定义需要获取的访问权限，以空格隔开；具体参考http://wiki.dev.renren.com/wiki/%E6%9D%83%E9%99%90%E5%88%97%E8%A1%A8
		
		/////////////////判断是非要重新登陆////////////////////
		final Intent intent=this.getIntent();
		Bundle bundle=intent.getExtras();
		if(bundle.containsKey("exit")){
			if(bundle.getBoolean("exit")){
				Login_url.putString("x_renew", "true");//设置重新登录
			}
		}		
		/////////////////////////////////////////////////////////
		
		
		String Url=loginUrl	+	"?"	+	UrlTools.encodeUrl(Login_url);
		web_login.loadUrl(Url);// 加载网页 
		
		// 网页加载进度条 
        progressDialog = ProgressDialog.show(this, null, "正在加载,请稍后..."); 
        alertDialog = new AlertDialog.Builder(this).create(); //创建AlertDialog 
		web_login.setWebViewClient(new WebViewClientRenRen());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_login, menu);
		return true;
	}
	
	//重写按钮按下响应函数，主要解决按返回键的崩溃情况
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WebView webView = (WebView) findViewById(R.id.webView1);
			if (webView.canGoBack()){
				webView.goBack();
				return true;
			}else{//无法返回时，直接选择是否退出
				exitOptionDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private class WebViewClientRenRen extends WebViewClient { 
	@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.i(TAG,"-onPageFinished-"+url);
			if(progressDialog.isShowing()){//加载完毕后，进度条不显示
				progressDialog.dismiss();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			Log.i(TAG,"-onPageFinished-"+url);
			if(!progressDialog.isShowing()){//网页开始加载时，显示进度条。
				progressDialog.show();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.i(TAG,"-onReceivedError-"+failingUrl);
			
			Toast.makeText(WebLoginActivity.this, "网页加载出错", Toast.LENGTH_LONG).show();
			alertDialog.setTitle("Error");
			alertDialog.setMessage(description);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			alertDialog.show();
		}

	/** 
    * 拦截URL地址，进行业务操作 
    */ 
	@Override 
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i(TAG,"-shouldOverrideUrlLoading--startwith-"+url);
			String callback_url=REDIRECT_URL+"#";
		
			if(url.startsWith(callback_url)){
			
				Log.i(TAG,"-shouldOverrideUrlLoading--"+url);
				String access_token=url.replace(callback_url+"access_token=", "");
				access_token=access_token.split("&")[0];
				System.out.println("access token=" + access_token); 
				final Intent result_intent=WebLoginActivity.this.getIntent();
				Bundle bundle=new Bundle();
				bundle.putString("access_token", access_token);
				result_intent.putExtras(bundle);
				WebLoginActivity.this.setResult(RESULT_OK, result_intent);//返回结果access_token
				WebLoginActivity.this.finish();
				//String urlString=String.format(, args)
				return false;
			}
			else{
				view.loadUrl(url); 
				return true; 
			}
		
		
		} 
	}

	private void exitOptionDialog()
	{
		new AlertDialog.Builder(this).setTitle("提示")
		.setMessage("是否退出迷你人人？")
		.setNegativeButton("No",  new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		})
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				final Intent result_intent=WebLoginActivity.this.getIntent();
				WebLoginActivity.this.setResult(RESULT_CANCELED, result_intent);//表示结束应用
				WebLoginActivity.this.finish();
			}
		})
		.show();
	}

}
