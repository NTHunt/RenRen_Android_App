package net.minirenren.fragment;

import net.minirenren.fragment.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class DetailsActivity extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);

		ImageView mImageView=(ImageView)findViewById(R.id.iv_details_back);
		mImageView.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
	}
}
