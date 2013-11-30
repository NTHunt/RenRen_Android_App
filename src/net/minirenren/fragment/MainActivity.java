package net.minirenren.fragment;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import net.minirenren.fragment.R;
import net.minirenren.view.SlidingMenu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;

//import net.minirenren.tools.RenRenDataInteract;

public class MainActivity extends FragmentActivity {

	private SlidingMenu mSlidingMenu;// 侧边栏的view
	private LeftFragment leftFragment; // 左侧边栏的碎片化view
	private RightFragment rightFragment; // 右侧边栏的碎片化view
	private MainListFragment_State centerFragment_state;// 中间内容碎片化的显示人人状态的view
	private MainListFragment_Photo centerFragment_photo;// 中间内容碎片化的显示人人图片的view
	private boolean state_or_photo;//用于表示当前的center frame显示的是什么，true为状态，false为图片
	private FragmentTransaction ft; // 碎片化管理的事务
	
	private String access_token;//访问人人的access token

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置登录界面
		Intent login_intent=new Intent();
		Bundle bundle=new Bundle();
		bundle.putBoolean("exit", false);
		login_intent.putExtras(bundle);
		login_intent.setClass(MainActivity.this, WebLoginActivity.class);
		startActivityForResult(login_intent,0);	
		//////////
		// 去标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));
		centerFragment_state=null;//初始时设为空
		centerFragment_photo=null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode,resultCode,data);
		if(resultCode==RESULT_OK){
			Bundle bundle=data.getExtras();
			String access_token;
			if((access_token=bundle.getString("access_token"))!=null){
				this.access_token=access_token;//设置全局变量access_token的值
				ft = this.getSupportFragmentManager().beginTransaction();
				leftFragment = new LeftFragment();
				rightFragment = new RightFragment();
				ft.replace(R.id.left_frame, leftFragment);
				ft.replace(R.id.right_frame, rightFragment);

				centerFragment_state = MainListFragment_State.newInstance(access_token);//调用newInstance函数初始化
				ft.replace(R.id.center_frame, centerFragment_state);
				ft.commit();
				state_or_photo=true;//表示当前显示状态新鲜事
				RenRenNewsAsyncTask login_name=new RenRenNewsAsyncTask();
				login_name.execute(access_token);
			}
		}else if(resultCode==RESULT_CANCELED){
			MainActivity.this.finish();
		}
	}

	public void llronclick(View v) {//该方法是right_fragment.xml中的view绑定的方法
		switch (v.getId()) {
		case R.id.llr_renren_state:
			if(state_or_photo){
				showRight();//showRight()第一次调用为显示右侧边栏，在此调用为关闭显示			
			}else{
				ft=this.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.center_frame, centerFragment_state);
				state_or_photo=true;
				showRight();//showRight()第一次调用为显示右侧边栏，在此调用为关闭显示
				ft.commit();
			}
			//Intent intent = new Intent(this, DetailsActivity.class);
			//startActivity(intent);
			break;
		case R.id.llr_renren_photo:
			if(state_or_photo){
				if(centerFragment_photo==null){
					centerFragment_photo=MainListFragment_Photo.newInstance(access_token);
				}
				ft=this.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.center_frame, centerFragment_photo);
				state_or_photo=false;
				showRight();
				ft.commit();
			}else{
				showRight();
			}
		default:
			break;
		}
	}
	
	public String getToken(){
		return this.access_token;
	}

	public void showCenter(){
		if(state_or_photo){
			if(centerFragment_state==null){
				centerFragment_state=MainListFragment_State.newInstance(access_token);
			}
			ft=this.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.center_frame, centerFragment_state);
			ft.commit();
		}else{
			if(centerFragment_photo==null){
				centerFragment_photo=MainListFragment_Photo.newInstance(access_token);
			}
			ft=this.getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.center_frame, centerFragment_photo);
			ft.commit();
		}
	}
	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}

	private class RenRenNewsAsyncTask extends AsyncTask<String, Integer, String> {	  
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

		private static final String GET_USERNAME_URL = "https://api.renren.com/v2/user/login/get?access_token=%s";
	    /**  
	     * 这里的Integer参数对应AsyncTask中的第一个参数   
	     * 这里的String返回值对应AsyncTask的第三个参数  
	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
	     */  
	    @Override  
	    protected String doInBackground(String... params) {
	    	accessToken=params[0];
			String urlString =String.format(GET_USERNAME_URL, accessToken);
			clientToRenRen=new DefaultHttpClient();
			getURL=new HttpGet(urlString);
			String userName="";
			synchronized(clientToRenRen){		
			try{
				HttpResponse response=clientToRenRen.execute(getURL);
				//System.out.println(response.getEntity().toString());
				HttpEntity entity=response.getEntity();
				BufferedReader buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
				StringBuffer strBuff=new StringBuffer();
				String result=null;
				while((result=buffReader.readLine())!=null){
					strBuff.append(result);
				}
				if(strBuff.toString()!=null)
				{
					JSONObject json=new JSONObject(strBuff.toString());//.getJSONObject("response");
					JSONObject jsonResponse = json.getJSONObject("response");
					userName=jsonResponse.getString("name");
					
				}
							
				return userName;	
				}catch (ClientProtocolException e){
					Log.e("TAG", e.getMessage());  
				}catch (IOException e){
					Log.e("TAG", e.getMessage());  
				}
				catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			return userName;
			}
	    }  	  
	  
	    /**  
	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
	     */  
	    @Override  
	    protected void onPostExecute(String result) {  
	    	
	    	new AlertDialog.Builder(MainActivity.this).setTitle("登录成功")
			.setMessage("欢迎："+result+"登录")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
	    }  	  
	  
	    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
	    @Override  
	    protected void onPreExecute() {
	    	
	    }    
	  
	    /**  
	     * 这里的Intege参数对应AsyncTask中的第二个参数  
	     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
	     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
	     */  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	    	
	    }  
	}  
}
