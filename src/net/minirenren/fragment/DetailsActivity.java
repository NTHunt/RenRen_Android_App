<<<<<<< HEAD
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
=======
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
>>>>>>> c171dfe974f6f0bede63cb5e8d3d31ff6e8c84f3
