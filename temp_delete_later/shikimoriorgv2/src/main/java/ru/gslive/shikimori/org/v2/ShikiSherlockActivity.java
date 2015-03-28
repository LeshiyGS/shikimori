package ru.gslive.shikimori.org.v2;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShikiSherlockActivity extends SherlockActivity {
    
	@Override
	protected void onDestroy() {
        super.onDestroy();
        ImageLoader.getInstance().clearMemoryCache();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Functions.getPreference(getApplicationContext());
		if (Functions.preference.animations){
			overridePendingTransition(R.anim.right_in2, R.anim.left_out2);
		}
	}
	
	@Override
	public void finish() {
		super.finish();
		if (Functions.preference.animations){
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		}
	}

}