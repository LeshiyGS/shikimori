package ru.gslive.shikimori.org.v2;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.perm.kate.api.Auth;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressWarnings("deprecation")
@SuppressLint("SetJavaScriptEnabled")
public class Activity_Login_VK extends ShikiSherlockActivity {

    WebView webview;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//Выбор темы
    	setTheme(R.style.AppTheme_ActionBarStyle_BackHome);

    	SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
    	Context context = getBaseContext();
    	s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    			
    	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    	getSupportActionBar().setSubtitle("Авторизация");
    	getSupportActionBar().setTitle(s);
    	getSupportActionBar().setDisplayShowCustomEnabled(true);
    	
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vk_login);
        
        webview = (WebView) findViewById(R.id.vkontakteview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.clearCache(true);

        webview.setWebViewClient(new VkontakteWebViewClient());
        CookieSyncManager.createInstance(this);
        
        //CookieManager cookieManager = CookieManager.getInstance();
        

        webview.loadUrl("https://oauth.vk.com/authorize?client_id=4018924&scope=offline,video&redirect_uri=http://oauth.vk.com/blank.html&display=mobile&response_type=token");
    }
    
    class VkontakteWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            parseUrl(url);
        }
    }
    
    private void parseUrl(String url) {
        try {
            if(url==null)
                return;
            if(url.startsWith(Auth.redirect_url))
            {
                if(!url.contains("error=")){
                    String[] auth=Auth.parseRedirectUrl(url);
                    Intent intent=new Intent();
                    intent.putExtra("token", auth[0]);
                    setResult(SherlockActivity.RESULT_OK, intent);
                }
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		finish();
          		break;
  			}
  		return super.onOptionsItemSelected(item);        
    }
    
    
}