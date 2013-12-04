package net.minirenren.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import net.minirenren.fragment.R;
import net.minirenren.tools.ImageLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoDetailsActivity extends Activity {

	private String access_token;
	private String myID;
	private String photoNewsID;
	private String myName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_photonews);

		ImageView mImageView=(ImageView)findViewById(R.id.iv_photodetails_back);
		mImageView.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
		
		final Intent intent=this.getIntent();
		Bundle bundle=intent.getExtras();
		
		this.access_token=bundle.getString("access_token");
		this.photoNewsID=bundle.getString("photoNewsID");
		this.myID=bundle.getString("myID");
		this.myName=bundle.getString("myName");		

		TextView userName=(TextView)findViewById(R.id.tv_nickname_photonews_details);
		TextView content=(TextView)findViewById(R.id.tv_content_photonews_details);
		TextView time=(TextView)findViewById(R.id.tv_published_photonews_details);
		TextView source=(TextView)findViewById(R.id.tv_source_photonews_details);
		TextView album=(TextView)findViewById(R.id.tv_photo_album_details);
		Button comment=(Button)findViewById(R.id.button_photo_comment);
		
		userName.setText(bundle.getString("user_name"));
		content.setText(bundle.getString("content"));
		time.setText(bundle.getString("time"));
		source.setText(bundle.getString("source"));
		album.setText(bundle.getString("album"));

		ImageView userPhoto=(ImageView)findViewById(R.id.iv_user_image_photonews_details);
		ImageView photo=(ImageView)findViewById(R.id.iv_photo_image_photonews_details);
        ImageLoader mImageLoader=new ImageLoader();
        mImageLoader.LoadingImage(userPhoto, bundle.getString("userPhoto"));
        mImageLoader=new ImageLoader();
        mImageLoader.LoadingImage(photo, bundle.getString("photo"));
        
        comment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText comment_text=(EditText)PhotoDetailsActivity.this.findViewById(R.id.editText_photo_comment);
				if(comment_text.getTextSize()>0){

			    	new AlertDialog.Builder(PhotoDetailsActivity.this).setTitle("提示：暂时无法提交评论")
					.setMessage("抱歉：因人人API2.0官方文档高深莫测，小弟功力尚浅，见笑。\n吐槽一句：文档有错没，我完全Copy其流程，居然不对。。。")
					.setPositiveButton("同情", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Toast.makeText(PhotoDetailsActivity.this, "谢谢，嘿嘿", Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("鄙视", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Toast.makeText(PhotoDetailsActivity.this, "没关系，我叫小强！", Toast.LENGTH_SHORT).show();
						}
					})
					.show();
					/*String params[]=new String[4];
					params[0]=access_token;
					params[1]=photoNewsID;
					params[2]=myID;
					params[3]=comment_text.getText().toString();
					RenRenPhotoDoCommentAsyncTask mRenRenStateDoCommentAsyncTask=new RenRenPhotoDoCommentAsyncTask();
					mRenRenStateDoCommentAsyncTask.execute(params);*/
				}else{
					Toast.makeText(PhotoDetailsActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	
	private class RenRenPhotoDoCommentAsyncTask extends AsyncTask<String, Integer, String> {	  
		//private String accessToken="";
		//HttpClient clientToRenRen;
		DefaultHttpClient clientToRenRen;
		HttpPost postURL;
		//private static final String apiKey="5c828b20360543df986bfac03f4fbd0e";//"a3c29006929243e19547fba648581427";
		//private static final String apiSecret ="1b0f546469a949999d50d8b8e85e0cdc";//"7eb0e66e829b4c66975ee5c0141a87fc";

		private static final String renrenStateDoComment_Url="https://api.renren.com/v2/comment/put";//省略后面?access_token=%s&content=%s&targetUserId=0&commentType=STATUS&entryOwnerId=%s&entryId=%s";
		
	    @Override  
	    protected String doInBackground(String... params) {
	    	//clientToRenRen=new DefaultHttpClient();
	    	clientToRenRen = new DefaultHttpClient();  
	    	//Credentials creds = new UsernamePasswordCredentials(apiKey, apiSecret);
	    	//clientToRenRen.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
			
			StringBuffer resultBuff=new StringBuffer();
			synchronized(clientToRenRen){		
			try{
				postURL=new HttpPost(renrenStateDoComment_Url);
				  //  设置HTTP POST请求参数必须用NameValuePair对象  
	            List<NameValuePair> postParams = new ArrayList<NameValuePair>();  
	            postParams.add(new BasicNameValuePair("access_token", params[0]));  
	            postParams.add(new BasicNameValuePair("content", params[3]));
	            //postParams.add(new BasicNameValuePair("targetUserId", "0"));
	            postParams.add(new BasicNameValuePair("commentType", "PHOTO"));
	            postParams.add(new BasicNameValuePair("entryOwnerId", params[2]));
	            postParams.add(new BasicNameValuePair("entryId", params[1]));	            
	            
	            //  设置HTTP POST请求参数  	
				postURL.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
				//clientToRenRen.
				HttpResponse response=clientToRenRen.execute(postURL);
				if (response.getStatusLine().getStatusCode() == 200)  {
					HttpEntity entity=response.getEntity();
					BufferedReader buffReader=new BufferedReader(new InputStreamReader(entity.getContent()));
					String localvar;
					while((localvar=buffReader.readLine())!=null){
						resultBuff.append(localvar);
					}
				}
				}	catch (ClientProtocolException e){
					Log.e("TAG", e.getMessage());  
				}catch (IOException e){
					Log.e("TAG", e.getMessage());  
				}  catch(IllegalArgumentException e){
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
				Toast.makeText(PhotoDetailsActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
				EditText comment_text=(EditText)PhotoDetailsActivity.this.findViewById(R.id.editText_photo_comment);
				comment_text.setText("");

				LinearLayout commentLayout=(LinearLayout)PhotoDetailsActivity.this.findViewById(R.id.ll_comments_content_photonews_details);
				commentLayout.setVisibility(LinearLayout.VISIBLE);
				
	            TextView tvCount = new TextView(PhotoDetailsActivity.this);  
	            tvCount.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
	            tvCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);  
	            tvCount.setSingleLine();  
	            tvCount.setCompoundDrawablePadding(5);  
	            tvCount.setPadding(0, 10, 0, 0);  
	            tvCount.setText("最新评论");  
	            tvCount.setTextColor(Color.parseColor("#ff005092"));
	            commentLayout.addView(tvCount);
	            
				try{
					String comment="";
					String time="";
					JSONObject json=new JSONObject(resultJson);
					if(!json.isNull("response")){
						JSONObject comment_object=json.getJSONObject("response");
						if(!comment_object.isNull("content")){
							comment=comment_object.getString("content");
						}
						if(!comment_object.isNull("time")){
							time=comment_object.getString("time");
						}
						
						LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
						
		                TextView tvContent = new TextView(PhotoDetailsActivity.this);
		                tvContent.setLayoutParams(layoutParams);  
		                tvContent.setTextColor(Color.BLACK);  
		                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);  
		                tvContent.setSingleLine();  
		                tvContent.setPadding(0, 10, 0, 0);
		                tvContent.setSingleLine(false);//设置自动换行
		                tvContent.setText(myName + "：" + comment);  
		                commentLayout.addView(tvContent);  
		                
		                TextView tvTime = new TextView(PhotoDetailsActivity.this); 
		                tvTime.setLayoutParams(layoutParams);  
		                tvTime.setTextColor(Color.GRAY);  
		                tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		                tvContent.setPadding(0, 5, 0, 10);  
		                tvTime.setText(time);  
		                commentLayout.addView(tvTime);  
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
}
