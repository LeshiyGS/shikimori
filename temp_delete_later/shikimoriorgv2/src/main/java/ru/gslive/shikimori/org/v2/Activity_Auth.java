package ru.gslive.shikimori.org.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressWarnings("deprecation")
public class Activity_Auth extends ShikiSherlockActivity {

	String kawai = null;
	String login = "";
	String site;
	
	SharedPreferences mSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//ќглавление SherlckBar
		setTheme(R.style.AppTheme_ActionBarStyle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setSubtitle(getString(R.string.auth));
				
		super.onCreate(savedInstanceState);
				
		site = getIntent().getExtras().getString("site");
			
		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		CookieManager.getInstance().setAcceptCookie(true);
		WebView webview = new WebView(this);
		setContentView(webview);
				
		webview.loadUrl("http://shikimori.org/users/auth/" + site);
				
		webview.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {

				String cookies = CookieManager.getInstance().getCookie(url);
				if (cookies.contains("_kawai_session")){
					kawai = cookies.substring(cookies.indexOf("_kawai_session=")+15, cookies.length());
					mSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					Editor editor = mSettings.edit();
					editor.putString(Constants.APP_PREFERENCES_SESSION, kawai);
					if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
						editor.apply();
					}
                    else {
						editor.commit();
					}
					CookieSyncManager.getInstance().stopSync();
					setResult(RESULT_OK, new Intent());
					finish();
				}
			}
		});
	}

}
