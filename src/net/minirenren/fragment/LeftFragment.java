package net.minirenren.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class LeftFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View view = inflater.inflate(R.layout.left_fragment, null);
		LinearLayout userLayout = (LinearLayout) view
				.findViewById(R.id.userLayout);
		userLayout.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				//创建用户信息界面
				UserFragment user = UserFragment.newInstance(((MainActivity)getActivity()).getToken());
				FragmentTransaction ft = getActivity()
						.getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.center_frame, user);
				ft.commit();
				((MainActivity) getActivity()).showLeft();
			}
		});

		LinearLayout mainPage = (LinearLayout) view.findViewById(R.id.mainPage);//添加主页按钮响应
		mainPage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).showCenter();
				((MainActivity) getActivity()).showLeft();
			}
		});
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	


}
