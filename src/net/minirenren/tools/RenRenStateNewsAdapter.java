package net.minirenren.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.minirenren.fragment.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;   
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater; 


/**
 * 功能描述：状态新鲜事列表数据适配器
 * @author nthunt
 */
public class RenRenStateNewsAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Map<String, Object>> state_data;
	
    public RenRenStateNewsAdapter(LayoutInflater inflater,String Json){
        this.inflater = inflater;
        state_data = new ArrayList<Map<String, Object>>();
        analysizJson(Json);
    }
    @Override  
    public int getCount() {  
        return this.state_data.size();  
    } 
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.state_data.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {    	 
    	ViewHolder holder = null;
        if (convertView == null) {  
            convertView = inflater.inflate(R.layout.renren_state_list_item, null);  
            holder = new ViewHolder();  
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.iv_user_image_statenews);  
            holder.text1 = (TextView) convertView.findViewById(R.id.tv_nickname_statenews);  
  
            holder.text2 = (TextView) convertView.findViewById(R.id.tv_content_statenews);  
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.iv_content_picture);  
  
            holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.ll_comments_content_statenews);  
            holder.linearLayout2 = (LinearLayout) convertView.findViewById(R.id.ll_update_status);  
  
            holder.text3 = (TextView) convertView.findViewById(R.id.tv_published_statenews);  
            holder.text4 = (TextView) convertView.findViewById(R.id.tv_source_statenews);  
  
            holder.text5 = (TextView) convertView.findViewById(R.id.tv_status_name);  
            holder.text6 = (TextView) convertView.findViewById(R.id.tv_status_content);  
  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        final Map<String, Object> freshNews = this.state_data.get(position);
        // 姓名  
        holder.text1.setText((String)freshNews.get("user_name"));  
  
        // 加载头像
        String photourl = (String)freshNews.get("photo");
        Log.i("TAG", "headurl = " + photourl);
        ImageLoader mImageLoader=new ImageLoader();
        mImageLoader.LoadingImage(holder.imageView1, photourl);
        
        //加载状态内容
        holder.text2.setText((String)freshNews.get("content"));
        
        //加载时间
        String updateTime = (String)freshNews.get("time");  
        if (!TextUtils.isEmpty(updateTime)) {  
            updateTime = updateTime.substring(updateTime.indexOf("-") + 1, updateTime.length());  
            updateTime = updateTime.replace("-", "月");  
            updateTime = updateTime.replace(" ", "日 ");  
            int index = updateTime.indexOf("0");  
            if (index == 0) {  
                updateTime = updateTime.substring(index + 1);  
            }  
  
            holder.text3.setText(updateTime);  
        } 
        
        // 来自那种客户端  
        String source = (String)freshNews.get("source");
        if (source != null) {  
            holder.text4.setText("来自:" + source);  
        }
        
        //加载评论信息
        @SuppressWarnings("unchecked")
		List<Map<String,Object>> comments=(List<Map<String,Object>>)freshNews.get("comments");
        if(comments.size()>0){
            if (holder.linearLayout1.getChildCount() > 0) {  
                holder.linearLayout1.removeAllViews();  
            }
            TextView tvCount = new TextView(inflater.getContext());  
            tvCount.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
            tvCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);  
            tvCount.setSingleLine();  
            tvCount.setCompoundDrawablePadding(5);  
            tvCount.setPadding(0, 10, 0, 0);  
            tvCount.setText("只显示最新一条和最后一条评论");  
            tvCount.setTextColor(Color.parseColor("#ff005092")); 
            holder.linearLayout1.addView(tvCount);  
            for(int i=0;i<comments.size();i++){
            	Map<String,Object> comment=comments.get(i);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
                
                TextView tvContent = new TextView(inflater.getContext());  
                tvContent.setLayoutParams(layoutParams);  
                tvContent.setTextColor(Color.BLACK);  
                tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);  
                tvContent.setSingleLine();  
                tvContent.setPadding(0, 10, 0, 0);
                tvContent.setSingleLine(false);//设置自动换行
                tvContent.setText((String)comment.get("name") + "：" + (String)comment.get("content"));  
                holder.linearLayout1.addView(tvContent);  

                TextView tvTime = new TextView(inflater.getContext());  
                tvTime.setTextColor(Color.GRAY);  
                tvTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);  
                tvTime.setLayoutParams(layoutParams);  
                tvContent.setPadding(0, 5, 0, 10);  
                tvTime.setText((String)comment.get("time"));  
                holder.linearLayout1.addView(tvTime);  
            }
        }else {  
            if (holder.linearLayout1.getChildCount() > 0) {  
                holder.linearLayout1.removeAllViews();  
            }
            TextView tvCount = new TextView(inflater.getContext());  
            tvCount.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
            tvCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);  
            tvCount.setSingleLine();  
            tvCount.setCompoundDrawablePadding(5);
            tvCount.setPadding(0, 10, 0, 0);  
            tvCount.setText("暂无评论");  
            tvCount.setTextColor(Color.parseColor("#ff005092")); 
            holder.linearLayout1.addView(tvCount);
        } 
  
        return convertView; 
    }
    private void analysizJson(String Json)
    {

		//在此定义状态新鲜事需要的信息
		String user_name="";
		String photo="";
		String content="";
		String time="";
		String source="";//新鲜事来源的平台
		List<Map<String,Object>> comments=new ArrayList<Map<String,Object>>();//每天新鲜事评论列表，只返回第一条及最后一条
		///////////////////
		try {			
			JSONObject json=new JSONObject(Json.toString());
			JSONArray jsonResponse = json.getJSONArray("response");
			for (int i = 0; i < jsonResponse.length(); i++) 
			{
				user_name="";
				photo="";
				content="";
				time="";
				source="";//新鲜事来源的平台
				JSONObject statenews_object=jsonResponse.getJSONObject(i);
				if(statenews_object!=null){
					time=statenews_object.getString("time");
					JSONObject resource_object=statenews_object.getJSONObject("resource");
					if(resource_object!=null){
						content=resource_object.getString("content");
					}
					if(!statenews_object.isNull("source"))
					{

						JSONObject source_object=statenews_object.getJSONObject("source");
						source=source_object.getString("text");
					}
					else
					{
						source="网页人人";
					}
					JSONObject sourceUser_object=statenews_object.getJSONObject("sourceUser");
					if(sourceUser_object!=null){
						user_name=sourceUser_object.getString("name");
						JSONArray photos = sourceUser_object.getJSONArray("avatar");
						if(photos!=null){
							photo=photos.getJSONObject(0).getString("url");
						}
					}
					if(!statenews_object.isNull("comments"))
					{
						String comment_username="";
						String comment_content="";
						String comment_time="";
						comments=new ArrayList<Map<String,Object>>();
						JSONArray comments_object=statenews_object.getJSONArray("comments");
						for(int j=0;j<comments_object.length();j++)
						{
							JSONObject comment_object=comments_object.getJSONObject(j);
							comment_content=comment_object.getString("content");
							comment_time=comment_object.getString("time");
							JSONObject author_object=comment_object.getJSONObject("author");
							comment_username=author_object.getString("name");
							Map<String,Object> comment=new HashMap<String,Object>();
							comment.put("content", comment_content);
							comment.put("time", comment_time);
							comment.put("name", comment_username);
							comments.add(comment);
						}
					}
					//List<Map<String, Object>> comment_data = new ArrayList<Map<String, Object>>();
				}
				
				Map<String, Object> renren_state_item = new HashMap<String, Object>();
				renren_state_item.put("user_name", user_name);
				renren_state_item.put("photo", photo);
				renren_state_item.put("content", content);
				renren_state_item.put("time", time);
				renren_state_item.put("source", source);
				renren_state_item.put("comments", comments);
				state_data.add(renren_state_item);
			}		
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    static class ViewHolder {  
  
        public LinearLayout linearLayout1;  
  
        public LinearLayout linearLayout2;  
  
        public ImageView imageView1;  
  
        public ImageView imageView2;  
  
        public TextView text1;  
  
        public TextView text2;  
  
        public TextView text3;  
  
        public TextView text4;  
  
        public TextView text5;  
  
        public TextView text6;
    }
}
