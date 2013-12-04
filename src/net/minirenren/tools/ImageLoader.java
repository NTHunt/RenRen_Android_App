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

//android 4.0以上访问网络时一定要开线程
public class ImageLoader {
	private ImageView mImageView;
	public void LoadingImage(ImageView mImageView,String imageUrl){
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
