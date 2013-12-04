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
	private static final String loginUrl="https://graph.renren.com/oauth/authorize";// Oauth2��֤��ַ 
	private static final String successLogin="http://graph.renren.com/oauth/login_success.html";
	
	private static final String TAG="OAuthActivity";
	private static final String REDIRECT_URL="http://graph.renren.com/oauth/login_success.html";// �ض����ַ 
	private ProgressDialog progressDialog; 
    private AlertDialog alertDialog; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_login);
		WebView web_login=(WebView)findViewById(R.id.webView1);
		WebSettings webSettings=web_login.getSettings();
		webSettings.setJavaScriptEnabled(true);
		//webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);/////////////////////////��ջ���ģʽ
		web_login.requestFocus();
		
		//build url
		Bundle Login_url=new Bundle();
		Login_url.putString("client_id", apiKey);
		Login_url.putString("redirect_uri", successLogin);
		Login_url.putString("response_type", "token");//��¼�ɹ�����������code��token����
		Login_url.putString("display","mobile");
		Login_url.putString("scope", "read_user_feed,publish_comment");//�Զ�����Ҫ��ȡ�ķ���Ȩ�ޣ��Կո����������ο�http://wiki.dev.renren.com/wiki/%E6%9D%83%E9%99%90%E5%88%97%E8%A1%A8
		
		/////////////////�ж��Ƿ�Ҫ���µ�½////////////////////
		final Intent intent=this.getIntent();
		Bundle bundle=intent.getExtras();
		if(bundle.containsKey("exit")){
			if(bundle.getBoolean("exit")){
				Login_url.putString("x_renew", "true");//�������µ�¼
			}
		}		
		/////////////////////////////////////////////////////////
		
		
		String Url=loginUrl	+	"?"	+	UrlTools.encodeUrl(Login_url);
		web_login.loadUrl(Url);// ������ҳ 
		
		// ��ҳ���ؽ����� 
        progressDialog = ProgressDialog.show(this, null, "���ڼ���,���Ժ�..."); 
        alertDialog = new AlertDialog.Builder(this).create(); //����AlertDialog 
		web_login.setWebViewClient(new WebViewClientRenRen());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_login, menu);
		return true;
	}
	
	//��д��ť������Ӧ��������Ҫ��������ؼ��ı������
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WebView webView = (WebView) findViewById(R.id.webView1);
			if (webView.canGoBack()){
				webView.goBack();
				return true;
			}else{//�޷�����ʱ��ֱ��ѡ���Ƿ��˳�
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
			if(progressDialog.isShowing()){//������Ϻ󣬽���������ʾ
				progressDialog.dismiss();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
			Log.i(TAG,"-onPageFinished-"+url);
			if(!progressDialog.isShowing()){//��ҳ��ʼ����ʱ����ʾ��������
				progressDialog.show();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.i(TAG,"-onReceivedError-"+failingUrl);
			
			Toast.makeText(WebLoginActivity.this, "��ҳ���س���", Toast.LENGTH_LONG).show();
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
    * ����URL��ַ������ҵ����� 
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
				WebLoginActivity.this.setResult(RESULT_OK, result_intent);//���ؽ��access_token
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
		new AlertDialog.Builder(this).setTitle("��ʾ")
		.setMessage("�Ƿ��˳��������ˣ�")
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
				WebLoginActivity.this.setResult(RESULT_CANCELED, result_intent);//��ʾ����Ӧ��
				WebLoginActivity.this.finish();
			}
		})
		.show();
	}

}
