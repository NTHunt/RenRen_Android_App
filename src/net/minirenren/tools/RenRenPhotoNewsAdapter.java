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
 * ����������״̬�������б�����������
 * @author nthunt
 */
public class RenRenPhotoNewsAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<Map<String, Object>> photo_data;
	
    public RenRenPhotoNewsAdapter(LayoutInflater inflater,String Json){
        this.inflater = inflater;
        photo_data = new ArrayList<Map<String, Object>>();
        analysizJson(Json);
    }
    @Override  
    public int getCount() {  
        return this.photo_data.size();  
    } 
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.photo_data.get(position);
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
            convertView = inflater.inflate(R.layout.renren_photo_list_item, null);  
            holder = new ViewHolder();  
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.iv_user_image_photonews);  
            holder.text1 = (TextView) convertView.findViewById(R.id.tv_nickname_photonews);  
  
            holder.text2 = (TextView) convertView.findViewById(R.id.tv_content_photonews);  
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.iv_photo_image_photonews); 
            holder.text5=(TextView) convertView.findViewById(R.id.tv_photo_album);
  
            holder.linearLayout1 = (LinearLayout) convertView.findViewById(R.id.ll_comments_content_photonews);
            
            holder.text3 = (TextView) convertView.findViewById(R.id.tv_published_photonews);  
            holder.text4 = (TextView) convertView.findViewById(R.id.tv_source_photonews); 
  
            convertView.setTag(holder);  
        } else {  
            holder = (ViewHolder) convertView.getTag();  
        }  
        final Map<String, Object> freshNews = this.photo_data.get(position);
        // ����  
        holder.text1.setText((String)freshNews.get("user_name"));  
  
        // ����ͷ��
        String user_photourl = (String)freshNews.get("user_photo");
        Log.i("TAG", "headurl = " + user_photourl);
        ImageLoader mImageLoader_userPhoto=new ImageLoader();
        mImageLoader_userPhoto.LoadingImage(holder.imageView1, user_photourl);
        
        //����״̬����
        holder.text2.setText((String)freshNews.get("message"));        
        
        //�����ϴ���Ƭ�����ܺ������������ʾһ�ţ�
        String photourl= (String)freshNews.get("photo");
        Log.i("TAG", "headurl = " + photourl);
        ImageLoader mImageLoader_photo=new ImageLoader();
        mImageLoader_photo.LoadingImage(holder.imageView2, photourl);        
        
        //�����������
        holder.text5.setText((String)freshNews.get("album"));
        
        //����ʱ��
        String updateTime = (String)freshNews.get("time");  
        if (!TextUtils.isEmpty(updateTime)) {  
            updateTime = updateTime.substring(updateTime.indexOf("-") + 1, updateTime.length());  
            updateTime = updateTime.replace("-", "��");  
            updateTime = updateTime.replace(" ", "�� ");  
            int index = updateTime.indexOf("0");  
            if (index == 0) {  
                updateTime = updateTime.substring(index + 1);  
            }  
  
            holder.text3.setText(updateTime);  
        } 
        
        // �������ֿͻ���  
        String source = (String)freshNews.get("source");
        if (source != null) {  
            holder.text4.setText("����:" + source);  
        }
        
        //����������Ϣ
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
            tvCount.setText("ֻ��ʾ����һ�������һ������");  
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
                tvContent.setSingleLine(false);//�����Զ�����
                tvContent.setText((String)comment.get("name") + "��" + (String)comment.get("content"));  
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
            tvCount.setText("��������");  
            tvCount.setTextColor(Color.parseColor("#ff005092")); 
            holder.linearLayout1.addView(tvCount);
        } 
  
        return convertView; 
    }
    private void analysizJson(String Json)
    {

		//�ڴ˶���״̬��������Ҫ����Ϣ
		String user_name="";
		String user_photo="";
		String message="";
		String time="";
		String source="";//��������Դ��ƽ̨
		String photo="";
		String album="";//�������
		String photoNewsID="";//ͼƬ�����ID
		List<Map<String,Object>> comments=new ArrayList<Map<String,Object>>();//ÿ�������������б�ֻ���ص�һ�������һ��
		///////////////////
		try {			
			JSONObject json=new JSONObject(Json.toString());
			JSONArray jsonResponse = json.getJSONArray("response");
			for (int i = 0; i < jsonResponse.length(); i++) 
			{
				user_name="";
				user_photo="";
				message="";
				time="";
				source="";//��������Դ��ƽ̨
				photo="";
				album="";//�������
				photoNewsID="";
				JSONObject photonews_object=jsonResponse.getJSONObject(i);
				if(photonews_object!=null ){//�ϴ�������Ƭ
					if(!photonews_object.isNull("time")){
						time=photonews_object.getString("time");
					}
					if(!photonews_object.isNull("resource")){
						JSONObject resource_object=photonews_object.getJSONObject("resource");
						if(!resource_object.isNull("title")){
							album=resource_object.getString("title");//�������
						}
					}
					
					if(!photonews_object.isNull("message")){
						if(photonews_object.getString("type").contentEquals("PUBLISH_ONE_PHOTO")){
							message=String.format("�ϴ�������Ƭ��%s", photonews_object.getString("message"));
						}else if(photonews_object.getString("type").contentEquals("PUBLISH_MORE_PHOTO")){
							message=String.format("�ϴ�������Ƭ��%s", photonews_object.getString("message"));
						}
					}
					if(!photonews_object.isNull("source"))
					{

						JSONObject source_object=photonews_object.getJSONObject("source");
						source=source_object.getString("text");
					}
					else
					{
						source="��ҳ����";
					}
					if(!photonews_object.isNull("sourceUser")){
						JSONObject sourceUser_object=photonews_object.getJSONObject("sourceUser");
						if(sourceUser_object!=null){
							user_name=sourceUser_object.getString("name");
							JSONArray photos = sourceUser_object.getJSONArray("avatar");
							if(photos!=null){
								user_photo=photos.getJSONObject(0).getString("url");
							}
						}
					}
					
					if(!photonews_object.isNull("attachment"))
					{
						JSONArray photos_object=photonews_object.getJSONArray("attachment");
						JSONObject photo_object=photos_object.getJSONObject(0);
						if(!photo_object.isNull("orginalUrl")){
							photo=photo_object.getString("orginalUrl");
						}
						if(!photo_object.isNull("id"))
						{//״̬��ID
							photoNewsID=Long.toString(photo_object.getLong("id"));
						}
					}
					
					if(!photonews_object.isNull("comments"))
					{
						String comment_username="";
						String comment_content="";
						String comment_time="";
						comments=new ArrayList<Map<String,Object>>();
						JSONArray comments_object=photonews_object.getJSONArray("comments");
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
				renren_state_item.put("user_photo", user_photo);
				renren_state_item.put("message", message);
				renren_state_item.put("time", time);
				renren_state_item.put("source", source);
				renren_state_item.put("album", album);
				renren_state_item.put("photo", photo);
				renren_state_item.put("comments", comments);
				renren_state_item.put("photoNewsID", photoNewsID);
				photo_data.add(renren_state_item);
			}		
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    static class ViewHolder {  
  
        public LinearLayout linearLayout1;  //������view
  
        public ImageView imageView1;  //��ͷ��
  
        public ImageView imageView2;  //���ϴ�ͼƬ
  
        public TextView text1;  //���û���
  
        public TextView text2;  //������
  
        public TextView text3;  //�󶨷���ʱ��
  
        public TextView text4;  //�󶨷���ƽ̨
        
        public TextView text5;  //���������
    }
}
