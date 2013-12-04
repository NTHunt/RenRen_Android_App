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

	private String access_token;//访问人人资料的access_token
	private View view;
	
	static UserFragment newInstance(String access_token) {//自定义带初始化参数的构造函数
		UserFragment f=new UserFragment();
		Bundle bundle=new Bundle();
		bundle.putString("access_token", access_token);
		f.setArguments(bundle);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.access_token=getArguments().getString("access_token");//获得访问access_token
		
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
	
	//获取人人登陆用户信息的类
	private class RenRenUserInfoAsyncTask extends AsyncTask<String,Integer,String>{
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

		//获取登录用户信息用户
		private static final String GET_USERID_URL = "https://api.renren.com/v2/user/login/get?access_token=%s";
		private static final String GET_USERINFO_URL = "https://api.renren.com/v2/user/get?access_token=%s&userId=%s";
	    /**  
	     * 这里的Integer参数对应AsyncTask中的第一个参数   
	     * 这里的String返回值对应AsyncTask的第三个参数  
	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
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
						resultBuff=new StringBuffer();//返回结果
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
	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
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
					userId.setText("人人ID: "+Long.toString(id));
					userGender.setText("性别: "+sex);
					userBirth.setText("生日: "+birthday);
					userHomeTown.setText("家乡: "+hometown);
			        ImageLoader mImageLoader=new ImageLoader();
			        mImageLoader.LoadingImage(userPhoto, photo);
					
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
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
	
    //android 4.0以上访问网络时一定要开线程
    private class ImageLoader {
    	private ImageView mImageView;
    	void LoadingImage(ImageView mImageView,String imageUrl){
    		this.mImageView=mImageView;
    		PhotoAsyncTask mPhotoAsyncTask=new PhotoAsyncTask();
    		mPhotoAsyncTask.execute(imageUrl);
    	}
        private class PhotoAsyncTask extends AsyncTask<String, Integer, byte[]> {
    		/**  
    	     * 这里的Integer参数对应AsyncTask中的第一个参数   
    	     * 这里的String返回值对应AsyncTask的第三个参数  
    	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
    	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
    	     */
    	    @Override  
    	    protected byte[] doInBackground(String... params) {
    			try {
    				URL url = new URL(params[0]);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//基于HTTP协议连接对象 IOException
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
    	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
    	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
    	     */  
    	    @Override  
    	    protected void onPostExecute(byte[] resultPhoto) {
        		Bitmap bitmap = BitmapFactory.decodeByteArray(resultPhoto, 0, resultPhoto.length);
        		mImageView.setImageBitmap(bitmap);
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

	private String access_token;//访问人人资料的access_token
	private View view;
	
	static UserFragment newInstance(String access_token) {//自定义带初始化参数的构造函数
		UserFragment f=new UserFragment();
		Bundle bundle=new Bundle();
		bundle.putString("access_token", access_token);
		f.setArguments(bundle);
		return f;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		this.access_token=getArguments().getString("access_token");//获得访问access_token
		
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
	
	//获取人人登陆用户信息的类
	private class RenRenUserInfoAsyncTask extends AsyncTask<String,Integer,String>{
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

		//获取登录用户信息用户
		private static final String GET_USERID_URL = "https://api.renren.com/v2/user/login/get?access_token=%s";
		private static final String GET_USERINFO_URL = "https://api.renren.com/v2/user/get?access_token=%s&userId=%s";
	    /**  
	     * 这里的Integer参数对应AsyncTask中的第一个参数   
	     * 这里的String返回值对应AsyncTask的第三个参数  
	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
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
						resultBuff=new StringBuffer();//返回结果
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
	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
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
					userId.setText("人人ID: "+Long.toString(id));
					userGender.setText("性别: "+sex);
					userBirth.setText("生日: "+birthday);
					userHomeTown.setText("家乡: "+hometown);
			        ImageLoader mImageLoader=new ImageLoader();
			        mImageLoader.LoadingImage(userPhoto, photo);
					
				}catch (JSONException e){
					Log.e("TAG", e.getMessage());  
				}
			}
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
	
    //android 4.0以上访问网络时一定要开线程
    private class ImageLoader {
    	private ImageView mImageView;
    	void LoadingImage(ImageView mImageView,String imageUrl){
    		this.mImageView=mImageView;
    		PhotoAsyncTask mPhotoAsyncTask=new PhotoAsyncTask();
    		mPhotoAsyncTask.execute(imageUrl);
    	}
        private class PhotoAsyncTask extends AsyncTask<String, Integer, byte[]> {
    		/**  
    	     * 这里的Integer参数对应AsyncTask中的第一个参数   
    	     * 这里的String返回值对应AsyncTask的第三个参数  
    	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
    	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
    	     */
    	    @Override  
    	    protected byte[] doInBackground(String... params) {
    			try {
    				URL url = new URL(params[0]);
    	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//基于HTTP协议连接对象 IOException
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
    	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
    	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
    	     */  
    	    @Override  
    	    protected void onPostExecute(byte[] resultPhoto) {
        		Bitmap bitmap = BitmapFactory.decodeByteArray(resultPhoto, 0, resultPhoto.length);
        		mImageView.setImageBitmap(bitmap);
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
}
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
