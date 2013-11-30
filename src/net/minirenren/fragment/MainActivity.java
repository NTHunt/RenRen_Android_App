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

	private SlidingMenu mSlidingMenu;// �������view
	private LeftFragment leftFragment; // ����������Ƭ��view
	private RightFragment rightFragment; // �Ҳ��������Ƭ��view
	private MainListFragment_State centerFragment_state;// �м�������Ƭ������ʾ����״̬��view
	private MainListFragment_Photo centerFragment_photo;// �м�������Ƭ������ʾ����ͼƬ��view
	private boolean state_or_photo;//���ڱ�ʾ��ǰ��center frame��ʾ����ʲô��trueΪ״̬��falseΪͼƬ
	private FragmentTransaction ft; // ��Ƭ�����������
	
	private String access_token;//�������˵�access token

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//���õ�¼����
		Intent login_intent=new Intent();
		Bundle bundle=new Bundle();
		bundle.putBoolean("exit", false);
		login_intent.putExtras(bundle);
		login_intent.setClass(MainActivity.this, WebLoginActivity.class);
		startActivityForResult(login_intent,0);	
		//////////
		// ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));
		centerFragment_state=null;//��ʼʱ��Ϊ��
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
				this.access_token=access_token;//����ȫ�ֱ���access_token��ֵ
				ft = this.getSupportFragmentManager().beginTransaction();
				leftFragment = new LeftFragment();
				rightFragment = new RightFragment();
				ft.replace(R.id.left_frame, leftFragment);
				ft.replace(R.id.right_frame, rightFragment);

				centerFragment_state = MainListFragment_State.newInstance(access_token);//����newInstance������ʼ��
				ft.replace(R.id.center_frame, centerFragment_state);
				ft.commit();
				state_or_photo=true;//��ʾ��ǰ��ʾ״̬������
				RenRenNewsAsyncTask login_name=new RenRenNewsAsyncTask();
				login_name.execute(access_token);
			}
		}else if(resultCode==RESULT_CANCELED){
			MainActivity.this.finish();
		}
	}

	public void llronclick(View v) {//�÷�����right_fragment.xml�е�view�󶨵ķ���
		switch (v.getId()) {
		case R.id.llr_renren_state:
			if(state_or_photo){
				showRight();//showRight()��һ�ε���Ϊ��ʾ�Ҳ�������ڴ˵���Ϊ�ر���ʾ			
			}else{
				ft=this.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.center_frame, centerFragment_state);
				state_or_photo=true;
				showRight();//showRight()��һ�ε���Ϊ��ʾ�Ҳ�������ڴ˵���Ϊ�ر���ʾ
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
	     * �����Integer������ӦAsyncTask�еĵ�һ������   
	     * �����String����ֵ��ӦAsyncTask�ĵ���������  
	     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
	     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
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
	     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
	     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
	     */  
	    @Override  
	    protected void onPostExecute(String result) {  
	    	
	    	new AlertDialog.Builder(MainActivity.this).setTitle("��¼�ɹ�")
			.setMessage("��ӭ��"+result+"��¼")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			})
			.show();
	    }  	  
	  
	    //�÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ��������  
	    @Override  
	    protected void onPreExecute() {
	    	
	    }    
	  
	    /**  
	     * �����Intege������ӦAsyncTask�еĵڶ�������  
	     * ��doInBackground�������У���ÿ�ε���publishProgress�������ᴥ��onProgressUpdateִ��  
	     * onProgressUpdate����UI�߳���ִ�У����п��Զ�UI�ռ���в���  
	     */  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	    	
	    }  
	}  
}
