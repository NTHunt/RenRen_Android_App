package net.minirenren.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import net.minirenren.tools.RenRenStateNewsAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainListFragment_State extends ListFragment {//用于显示人人状态列表的listfragment
	private ImageView lv_left;
	private ImageView iv_right;
	LayoutInflater inflater;
	private View mView;
	//private static final int list_i

	private String access_token;
	
<<<<<<< HEAD
	private int pageNum_State;//指示当前人人状态新鲜事页面编号，默认每页30个新鲜事
	//private int pageNum_phone;//指示当前人人照片新鲜事页面编号，默认每页30个新鲜事
=======
	private int pageNum_State;//指示当前人人状态新鲜事页面编号，默认每页20个新鲜事
	//private int pageNum_phone;//指示当前人人照片新鲜事页面编号，默认每页20个新鲜事
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
	
    static MainListFragment_State newInstance(String access_token) {
    	MainListFragment_State f = new MainListFragment_State();
        Bundle args = new Bundle();
        args.putString("access_token", access_token);
        f.setArguments(args);
        return f;
    }
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater=inflater;
		final View mView = inflater.inflate(R.layout.list, null);
		this.mView=mView;
		TextView mTitle=(TextView)mView.findViewById(R.id.mainframe_title);
		
		
		mTitle.setText("人人状态\n（点击刷新）");
		mTitle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String params[]=new String[2];
				params[0]=access_token;
				params[1]=String.format("%d", pageNum_State);	
				
				TextView mTitle=(TextView)mView.findViewById(R.id.mainframe_title);				
				
				mTitle.setText("人人状态\n（刷新中。。。）");
				ListView listView=getListView();
				listView.setAdapter(null);//清空listview数据
				
				RenRenStateNewsAsyncTask news_State=new RenRenStateNewsAsyncTask();
				news_State.execute(params);
				
		    	ProgressBar mProgressbar=(ProgressBar)mView.findViewById(R.id.header_progressbar);
		    	if(mProgressbar.getVisibility()==ProgressBar.INVISIBLE){
		    		mProgressbar.setVisibility(ProgressBar.VISIBLE);
		    	}
			}
		});
		
		lv_left = (ImageView) mView.findViewById(R.id.iv_left);
		iv_right = (ImageView) mView.findViewById(R.id.iv_right);
		
		String params[]=new String[2];
		params[0]=this.access_token;
		params[1]=String.format("%d", this.pageNum_State);		
		RenRenStateNewsAsyncTask news_State=new RenRenStateNewsAsyncTask();
		news_State.execute(params);
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {		

		super.onCreate(savedInstanceState);
		////////////////////////////////		
		this.access_token=getArguments().getString("access_token");
		this.pageNum_State=1;
		//this.pageNum_phone=1;
		
		////////////////////////////////
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		lv_left.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((MainActivity) getActivity()).showLeft();
			}
		});

		iv_right.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((MainActivity) getActivity()).showRight();
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d("----->", position + "");
		
<<<<<<< HEAD
		@SuppressWarnings("unchecked")
		Map<String, Object> state_data=(Map<String, Object>) l.getItemAtPosition(position);
		
		Intent intent = new Intent(getActivity(), StateDetailsActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("access_token", ((MainActivity) getActivity()).getToken());
		bundle.putString("myName", ((MainActivity) getActivity()).getMyName());
		bundle.putString("userID", ((MainActivity) getActivity()).getMyID());
		//bundle.putString("userID", (String)state_data.get("userID"));
		bundle.putString("stateID", (String)state_data.get("stateID"));
		
		bundle.putString("user_name", (String)state_data.get("user_name"));
		bundle.putString("photo", (String)state_data.get("photo"));
		bundle.putString("content", (String)state_data.get("content"));
		bundle.putString("time", (String)state_data.get("time"));
		bundle.putString("source", (String)state_data.get("source"));
		
		intent.putExtras(bundle);
=======
		//Map<String, Object> state_data=(Map<String, Object>) l.getItemAtPosition(position);
		
		Intent intent = new Intent(getActivity(), DetailsActivity.class);
		//Bundle bundle=new Bundle();
		//bundle.putString("news_type", "state");
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
		startActivity(intent);
	}
	private class RenRenStateNewsAsyncTask extends AsyncTask<String, Integer, String> {	  
		private String accessToken="";
		HttpClient clientToRenRen;
		HttpGet getURL;

<<<<<<< HEAD
		private static final String renrenNews_State_Url="https://api.renren.com/v2/feed/list?access_token=%s&feedType=UPDATE_STATUS&pageSize=30&pageNumber=%s";
=======
		private static final String renrenNews_State_Url="https://api.renren.com/v2/feed/list?access_token=%s&feedType=UPDATE_STATUS&pageSize=20&pageNumber=%s";
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
	    /**  
	     * 这里的Integer参数对应AsyncTask中的第一个参数   
	     * 这里的String返回值对应AsyncTask的第三个参数  
	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
	     */  
	    @Override  
	    protected String doInBackground(String... params) {
	    	accessToken=params[0];
			String urlString =String.format(renrenNews_State_Url, accessToken,params[1]);
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
				}catch (ClientProtocolException e){
					Log.e("TAG", e.getMessage());  
				}catch (IOException e){
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
				RenRenStateNewsAdapter mRenRenStateNewsAdapter=new RenRenStateNewsAdapter(inflater,resultJson);
				ListView listView=getListView();
				//ListView listView = (ListView) getActivity().findViewById(getActivity()R.id.@android:id/list);
				listView.setAdapter(mRenRenStateNewsAdapter);
				
				ProgressBar mProgressbar=(ProgressBar)mView.findViewById(R.id.header_progressbar);
				mProgressbar.setVisibility(ProgressBar.INVISIBLE);
				
				TextView mTitle=(TextView)mView.findViewById(R.id.mainframe_title);					
				mTitle.setText("人人状态\n（点击刷新）");
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
	    	ProgressBar mProgressbar=(ProgressBar)mView.findViewById(R.id.header_progressbar);
	    	int vlaue = values[0];  
	    	mProgressbar.setProgress(vlaue);
	    }  
	}  

}
