<<<<<<< HEAD
package net.minirenren.fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.minirenren.fragment.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserFragment extends Fragment {

	private String access_token;//�����������ϵ�access_token
	private View view;
	
	static UserFragment newInstance(String access_token) {//�Զ������ʼ�������Ĺ��캯��
		UserFragment f=new UserFragment();
		Bundle bundle=new Bundle();
		bundle.putString("access_token", access_token);
		f.setArguments(bundle);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.access_token=getArguments().getString("access_token");//��÷���access_token
		
		this.view = inflater.inflate(R.layout.user_homepage, null);
		ImageView left = (ImageView) view.findViewById(R.id.iv_user_left);
		left.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				((MainActivity) getActivity()).showLeft();
			}
		});
		Button exit_button=(Button)view.findViewById(R.id.exit_button);
		exit_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putBoolean("exit", true);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), WebLoginActivity.class);
				startActivityForResult(intent,0);
			}
		});
		RenRenUserInfoAsyncTask mRenRenUserInfoAsyncTask=new RenRenUserInfoAsyncTask();
		mRenRenUserInfoAsyncTask.execute(this.access_token);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}	
	
	//��ȡ���˵�½�û���Ϣ����
	private class RenRenUserInfoAsyncTask extends AsyncTask<String,Integer,String>{
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

		//��ȡ��¼�û���Ϣ�û�
		private static final String GET_USERID_URL = "https://api.renren.com/v2/user/login/get?access_token=%s";
		private static final String GET_USERINFO_URL = "https://api.renren.com/v2/user/get?access_token=%s&userId=%s";
	    /**  
	     * �����Integer������ӦAsyncTask�еĵ�һ������   
	     * �����String����ֵ��ӦAsyncTask�ĵ���������  
	     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
	     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
	     */  
	    @Override  
	    protected String doInBackground(String... params) {
	    	accessToken=params[0];
			String urlString =String.format(GET_USERID_URL, accessToken);
			clientToRenRen=new DefaultHttpClient();
			getURL=new HttpGet(urlString);
			StringBuffer resultBuff=new StringBuffer();
			synchronized(clientToRenRen){		
			try{
				HttpResponse response=clientToRenRen.execute(getURL);
				HttpEntity entity=response.getEntity();
				BufferedReader buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
				String localvar;
				while((localvar=buffReader.readLine())!=null){
					resultBuff.append(localvar);
				}
				if(resultBuff.toString().length()>0){
					Long id;
					JSONObject json=new JSONObject(resultBuff.toString());
					JSONObject userInfo_object=json.getJSONObject("response");
					if(!userInfo_object.isNull("id")){
						id=userInfo_object.getLong("id");
						urlString =String.format(GET_USERINFO_URL, accessToken,Long.toString(id));
						getURL=new HttpGet(urlString);
						resultBuff=new StringBuffer();//���ؽ��
						response=clientToRenRen.execute(getURL);
						entity=response.getEntity();
						buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
						while((localvar=buffReader.readLine())!=null){
							resultBuff.append(localvar);
						}
					}
				}
				}catch (ClientProtocolException e){
					Log.e("TAG", e.getMessage());  
				}catch (IOException e){
					Log.e("TAG", e.getMessage());  
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
			return resultBuff.toString();	
	    }	  
	    /**  
	     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
	     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
	     */  
	    @Override  
	    protected void onPostExecute(String resultJson) { 
			if(resultJson.length()>0)
			{
				try{
					Long id = null;
					String name="";
					String photo="";
					String sex="";
					String birthday="";
					String hometown="";
					JSONObject json=new JSONObject(resultJson);
					JSONObject userInfo_object=json.getJSONObject("response");
					if(!userInfo_object.isNull("id")){
						id=userInfo_object.getLong("id");
					}
					if(!userInfo_object.isNull("name")){
						name=userInfo_object.getString("name");
					}
					if(!userInfo_object.isNull("avatar")){
						JSONArray photos = userInfo_object.getJSONArray("avatar");
						photo=photos.getJSONObject(2).getString("url");
					}
					if(!userInfo_object.isNull("basicInformation")){
						JSONObject basicInfo_object=userInfo_object.getJSONObject("basicInformation");
						if(!basicInfo_object.isNull("sex")){
							sex=basicInfo_object.getString("sex");
						}
						if(!basicInfo_object.isNull("birthday")){
							birthday=basicInfo_object.getString("birthday");
						}
						if(!basicInfo_object.isNull("homeTown")){
							JSONObject hometown_object=basicInfo_object.getJSONObject("homeTown");
							if(!hometown_object.isNull("province") && !hometown_object.isNull("city")){
								hometown=hometown_object.getString("province") + hometown_object.getString("city");
							}
						}
					}

					ImageView userPhoto=(ImageView)view.findViewById(R.id.iv_icon);
					TextView userName=(TextView)view.findViewById(R.id.tv_name);
					TextView userId=(TextView)view.findViewById(R.id.tv_renren_id);
					TextView userGender=(TextView)view.findViewById(R.id.tv_gender);
					TextView userBirth=(TextView)view.findViewById(R.id.tv_birth);
					TextView userHomeTown=(TextView)view.findViewById(R.id.tv_hometown);
					
					userName.setText(name);
					userId.setText("����ID: "+Long.toString(id));
					userGender.setText("�Ա�: "+sex);
					userBirth.setText("����: "+birthday);
					userHomeTown.setText("����: "+hometown);
			        ImageLoader mImageLoader=new ImageLoader();
			        mImageLoader.LoadingImage(userPhoto, photo);
					
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
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
	
    //android 4.0���Ϸ�������ʱһ��Ҫ���߳�
    private class ImageLoader {
    	private ImageView mImageView;
    	void LoadingImage(ImageView mImageView,String imageUrl){
    		this.mImageView=mImageView;
    		PhotoAsyncTask mPhotoAsyncTask=new PhotoAsyncTask();
    		mPhotoAsyncTask.execute(imageUrl);
    	}
        private class PhotoAsyncTask extends AsyncTask<String, Integer, byte[]> {
    		/**  
    	     * �����Integer������ӦAsyncTask�еĵ�һ������   
    	     * �����String����ֵ��ӦAsyncTask�ĵ���������  
    	     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
    	     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
    	     */
    	    @Override  
    	    protected byte[] doInBackground(String... params) {
    			try {
    				URL url = new URL(params[0]);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//����HTTPЭ�����Ӷ��� IOException
    	            conn.setConnectTimeout(5000);
    	            conn.setRequestMethod("GET");//IOException
    	            if(conn.getResponseCode() == 200){
    	                InputStream inStream = conn.getInputStream();
    	                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	        		byte[] buffer = new byte[1024];
    	        		int len = 0;
    	        		while( (len = inStream.read(buffer)) != -1){
    	        			outStream.write(buffer, 0, len);
    	        		}
    	        		inStream.close();
    	        		return outStream.toByteArray();
    	            }  
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}//MalformedURLException
    			catch (ProtocolException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();				
    			}catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return null;
    	    }	  
    	    /**  
    	     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
    	     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
    	     */  
    	    @Override  
    	    protected void onPostExecute(byte[] resultPhoto) {
        		Bitmap bitmap = BitmapFactory.decodeByteArray(resultPhoto, 0, resultPhoto.length);
        		mImageView.setImageBitmap(bitmap);
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
}
=======
package net.minirenren.fragment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.minirenren.fragment.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserFragment extends Fragment {

	private String access_token;//�����������ϵ�access_token
	private View view;
	
	static UserFragment newInstance(String access_token) {//�Զ������ʼ�������Ĺ��캯��
		UserFragment f=new UserFragment();
		Bundle bundle=new Bundle();
		bundle.putString("access_token", access_token);
		f.setArguments(bundle);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.access_token=getArguments().getString("access_token");//��÷���access_token
		
		this.view = inflater.inflate(R.layout.user_homepage, null);
		ImageView left = (ImageView) view.findViewById(R.id.iv_user_left);
		left.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				((MainActivity) getActivity()).showLeft();
			}
		});
		Button exit_button=(Button)view.findViewById(R.id.exit_button);
		exit_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putBoolean("exit", true);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), WebLoginActivity.class);
				startActivityForResult(intent,0);
			}
		});
		RenRenUserInfoAsyncTask mRenRenUserInfoAsyncTask=new RenRenUserInfoAsyncTask();
		mRenRenUserInfoAsyncTask.execute(this.access_token);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}	
	
	//��ȡ���˵�½�û���Ϣ����
	private class RenRenUserInfoAsyncTask extends AsyncTask<String,Integer,String>{
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

		//��ȡ��¼�û���Ϣ�û�
		private static final String GET_USERID_URL = "https://api.renren.com/v2/user/login/get?access_token=%s";
		private static final String GET_USERINFO_URL = "https://api.renren.com/v2/user/get?access_token=%s&userId=%s";
	    /**  
	     * �����Integer������ӦAsyncTask�еĵ�һ������   
	     * �����String����ֵ��ӦAsyncTask�ĵ���������  
	     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
	     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
	     */  
	    @Override  
	    protected String doInBackground(String... params) {
	    	accessToken=params[0];
			String urlString =String.format(GET_USERID_URL, accessToken);
			clientToRenRen=new DefaultHttpClient();
			getURL=new HttpGet(urlString);
			StringBuffer resultBuff=new StringBuffer();
			synchronized(clientToRenRen){		
			try{
				HttpResponse response=clientToRenRen.execute(getURL);
				HttpEntity entity=response.getEntity();
				BufferedReader buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
				String localvar;
				while((localvar=buffReader.readLine())!=null){
					resultBuff.append(localvar);
				}
				if(resultBuff.toString().length()>0){
					Long id;
					JSONObject json=new JSONObject(resultBuff.toString());
					JSONObject userInfo_object=json.getJSONObject("response");
					if(!userInfo_object.isNull("id")){
						id=userInfo_object.getLong("id");
						urlString =String.format(GET_USERINFO_URL, accessToken,Long.toString(id));
						getURL=new HttpGet(urlString);
						resultBuff=new StringBuffer();//���ؽ��
						response=clientToRenRen.execute(getURL);
						entity=response.getEntity();
						buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
						while((localvar=buffReader.readLine())!=null){
							resultBuff.append(localvar);
						}
					}
				}
				}catch (ClientProtocolException e){
					Log.e("TAG", e.getMessage());  
				}catch (IOException e){
					Log.e("TAG", e.getMessage());  
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
			return resultBuff.toString();	
	    }	  
	    /**  
	     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
	     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
	     */  
	    @Override  
	    protected void onPostExecute(String resultJson) { 
			if(resultJson.length()>0)
			{
				try{
					Long id = null;
					String name="";
					String photo="";
					String sex="";
					String birthday="";
					String hometown="";
					JSONObject json=new JSONObject(resultJson);
					JSONObject userInfo_object=json.getJSONObject("response");
					if(!userInfo_object.isNull("id")){
						id=userInfo_object.getLong("id");
					}
					if(!userInfo_object.isNull("name")){
						name=userInfo_object.getString("name");
					}
					if(!userInfo_object.isNull("avatar")){
						JSONArray photos = userInfo_object.getJSONArray("avatar");
						photo=photos.getJSONObject(2).getString("url");
					}
					if(!userInfo_object.isNull("basicInformation")){
						JSONObject basicInfo_object=userInfo_object.getJSONObject("basicInformation");
						if(!basicInfo_object.isNull("sex")){
							sex=basicInfo_object.getString("sex");
						}
						if(!basicInfo_object.isNull("birthday")){
							birthday=basicInfo_object.getString("birthday");
						}
						if(!basicInfo_object.isNull("homeTown")){
							JSONObject hometown_object=basicInfo_object.getJSONObject("homeTown");
							if(!hometown_object.isNull("province") && !hometown_object.isNull("city")){
								hometown=hometown_object.getString("province") + hometown_object.getString("city");
							}
						}
					}

					ImageView userPhoto=(ImageView)view.findViewById(R.id.iv_icon);
					TextView userName=(TextView)view.findViewById(R.id.tv_name);
					TextView userId=(TextView)view.findViewById(R.id.tv_renren_id);
					TextView userGender=(TextView)view.findViewById(R.id.tv_gender);
					TextView userBirth=(TextView)view.findViewById(R.id.tv_birth);
					TextView userHomeTown=(TextView)view.findViewById(R.id.tv_hometown);
					
					userName.setText(name);
					userId.setText("����ID: "+Long.toString(id));
					userGender.setText("�Ա�: "+sex);
					userBirth.setText("����: "+birthday);
					userHomeTown.setText("����: "+hometown);
			        ImageLoader mImageLoader=new ImageLoader();
			        mImageLoader.LoadingImage(userPhoto, photo);
					
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
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
	
    //android 4.0���Ϸ�������ʱһ��Ҫ���߳�
    private class ImageLoader {
    	private ImageView mImageView;
    	void LoadingImage(ImageView mImageView,String imageUrl){
    		this.mImageView=mImageView;
    		PhotoAsyncTask mPhotoAsyncTask=new PhotoAsyncTask();
    		mPhotoAsyncTask.execute(imageUrl);
    	}
        private class PhotoAsyncTask extends AsyncTask<String, Integer, byte[]> {
    		/**  
    	     * �����Integer������ӦAsyncTask�еĵ�һ������   
    	     * �����String����ֵ��ӦAsyncTask�ĵ���������  
    	     * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�  
    	     * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���  
    	     */
    	    @Override  
    	    protected byte[] doInBackground(String... params) {
    			try {
    				URL url = new URL(params[0]);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//����HTTPЭ�����Ӷ��� IOException
    	            conn.setConnectTimeout(5000);
    	            conn.setRequestMethod("GET");//IOException
    	            if(conn.getResponseCode() == 200){
    	                InputStream inStream = conn.getInputStream();
    	                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	        		byte[] buffer = new byte[1024];
    	        		int len = 0;
    	        		while( (len = inStream.read(buffer)) != -1){
    	        			outStream.write(buffer, 0, len);
    	        		}
    	        		inStream.close();
    	        		return outStream.toByteArray();
    	            }  
    			} catch (MalformedURLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}//MalformedURLException
    			catch (ProtocolException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();				
    			}catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return null;
    	    }	  
    	    /**  
    	     * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��  
    	     * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������  
    	     */  
    	    @Override  
    	    protected void onPostExecute(byte[] resultPhoto) {
        		Bitmap bitmap = BitmapFactory.decodeByteArray(resultPhoto, 0, resultPhoto.length);
        		mImageView.setImageBitmap(bitmap);
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
}
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
