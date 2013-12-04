package net.minirenren.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

//android 4.0���Ϸ�������ʱһ��Ҫ���߳�
public class ImageLoader {
	private ImageView mImageView;
	public void LoadingImage(ImageView mImageView,String imageUrl){
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
