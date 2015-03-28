package ru.gslive.shikimori.org.v2;

import java.io.File;
import java.io.IOException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Add_Edit_Input extends ShikiSherlockActivity {
	
	int mes_status = 0;
			
	String type;
	String commentable_id;
	String text;
	String action;
	String comment_id;
	String to_id;
	String image_load;
	
	public static final int MSG_DOWNLOADED = 0;
	
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
        
    String upLoadServerUri = Constants.SERVER + "/user_images?linked_type=Comment";
     
    @SuppressLint("SdCardPath")
	
	AsyncTask<Void, Void, Void> al;
	AsyncTask<Void, Void, Void> al_edit;
	
	EditText et_message;
	CheckBox cb_offtop, cb_review;
	private String imagepath=null;
	
	public static ActionBar actionbar;
	
	String[] smile_text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		Functions.getPreference(getBaseContext());
	
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		Context context = getBaseContext();
	    s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			    
	    //Выбор темы
	  	setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
		
		actionbar = getSupportActionBar();
        if (!Functions.isLollipop()){
            actionbar.setTitle(s);
        }
		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setDisplayShowCustomEnabled(true);

		super.onCreate(savedInstanceState);
        Functions.getPreference(getBaseContext());
        if (Functions.preference.theme.equals("ligth")){
            setContentView(R.layout.w_activity_input);
        }else{
            setContentView(R.layout.d_activity_input);
        }

        smile_text = getResources().getStringArray(R.array.smile_imgs);
		
		type = getIntent().getExtras().getString("type");
		
		try{
			action = getIntent().getExtras().getString("action");
		}catch (Exception e) {

		}
		try{
			comment_id = getIntent().getExtras().getString("comment_id");
		}catch (Exception e) {

		}
		try{
			commentable_id = getIntent().getExtras().getString("commentable_id");
		}catch (Exception e) {

		}
		try{
			text = getIntent().getExtras().getString("text");
		}catch (Exception e) {

		}
		try{
			to_id = getIntent().getExtras().getString("to_id");
		}catch (Exception e) {

		}
		
		et_message = (EditText) findViewById(R.id.et_message);
		cb_offtop = (CheckBox) findViewById(R.id.cb_offtop);
		cb_review = (CheckBox) findViewById(R.id.cb_review);
		
		if (text == null){
			et_message.setText(text);
		}else{
			et_message.setText(text.replace(getString(R.string.send_from_android),"") + " ");
		}
		
		et_message.setSelection(et_message.getText().length());
		
		String subTitle = "";
		if (type.equals("message")){
            subTitle = getString(R.string.title_message);
			cb_offtop.setVisibility(View.GONE);
			cb_review.setVisibility(View.GONE);
		}else if (type.equals("user_page")){
            subTitle = getString(R.string.title_user_page);
			cb_offtop.setVisibility(View.GONE);
			cb_review.setVisibility(View.GONE);
		}else if(type.equals("comment")){
            subTitle = getString(R.string.title_comment);
			cb_offtop.setVisibility(View.VISIBLE);
			cb_review.setVisibility(View.GONE);
		}else if(type.equals("review")){
            subTitle = getString(R.string.title_review);
			cb_offtop.setVisibility(View.GONE);
			cb_review.setVisibility(View.VISIBLE);
			cb_review.setChecked(true);
		}
        if (Functions.isLollipop()){
            actionbar.setTitle(subTitle);
        }else{
            actionbar.setSubtitle(subTitle);
        }
	}

	//Actions меню
  	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
  		switch (item.getItemId()) {
          	case android.R.id.home:
          		finish();
          		break;
  		}
  		return super.onOptionsItemSelected(item);        
    }
  	
  	public void onBtnLoadImage(View view){
  		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
    }
  	
    public String getPath(Uri uri) {
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = managedQuery(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
    }

	
	public void onBtnClickB(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[b][/b]", 0, "[b][/b]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/b]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[b]", 0, "[b]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/b]", 0, "[/b]".length());
			
		}
	}
	
	public void onBtnClickI(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[i][/i]", 0, "[i][/i]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/i]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[i]", 0, "[i]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/i]", 0, "[/i]".length());
			
		}
	}

	public void onBtnClickU(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[u][/u]", 0, "[u][/u]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/u]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[u]", 0, "[u]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/u]", 0, "[/u]".length());
			
		}
	}
	
	public void onBtnClickSpoiler(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[spoiler=спойлер][/spoiler]", 0, "[spoiler=спойлер][/spoiler]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/spoiler]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[spoiler=спойлер]", 0, "[spoiler=спойлер]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/spoiler]", 0, "[/spoiler]".length());
			
		}
	}
	
	public void onBtnClickLink(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[url=]Ссылка[/url]", 0, "[url=]Ссылка[/url]".length());
			et_message.setSelection(et_message.getSelectionStart() - "]Ссылка[/url]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[url=", 0, "[url=".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"]Ссылка[/url]", 0, "]Ссылка[/url]".length());
			
		}
	}
	
	public void onBtnClickImg(View view){
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[img][/img]", 0, "[img][/img]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/img]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[img]", 0, "[img]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/img]", 0, "[/img]".length());
			
		}
	}

	public void onBtnClickS(View view){
		////Log.d("Start, End", "" + et_message.getSelectionStart() + et_message.getSelectionEnd());
		if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end),"[s][/s]", 0, "[s][/s]".length());
			et_message.setSelection(et_message.getSelectionStart() - "[/s]".length());
		}else{
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end),Math.min(start, end),"[s]", 0, "[s]".length());
			et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/s]", 0, "[/s]".length());
			
		}
	}
		
	public void onBtnClickSmile(View view){
		Intent intent_smile = new Intent(this, Activity_SmileDialog.class);
    	startActivityForResult(intent_smile, 0);
	}

    public void onBtnClickColor(View view){
        Intent intent_color = new Intent(this, Activity_ColorDialog.class);
        startActivityForResult(intent_color, 2);
    }

	public void onBtnSendClick(View view){
        if (!et_message.getText().toString().trim().equals("")) {
            if (action == null) {
                al = new AsyncSend().execute();
            } else if (action.equals("edit")) {
                al_edit = new AsyncEdit().execute();
            }
        }else{
            Toast.makeText(getBaseContext(), "Нельзя отправить пустое сообщение.", Toast.LENGTH_SHORT).show();
        }
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK) {
            //Bitmap photo = (Bitmap) data.getData().getPath(); 
           
            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            dialog = ProgressDialog.show(Activity_Add_Edit_Input.this, "", "Загрузка изображения на сервер...", true);

            new Thread(new Runnable() {
                public void run() {
                                     
                     //uploadFile(imagepath);
                	String charset = "UTF-8";
                	Log.d(Constants.LogTag, "-> " + imagepath);
                	if (imagepath != null){
	                	File uploadFile = new File(imagepath); 
	                	String[] temp = SSDK_API.getToken(Functions.preference.kawai); 
	                    try {
	                        MultipartUtility multipart = new MultipartUtility(upLoadServerUri, charset, temp[0], temp[1]);
	                                                  
	                        multipart.addFormField("authenticity_token", temp[0]);
	                         
	                        multipart.addFilePart("image", uploadFile);
	             
	                        image_load = multipart.finish();

	                    } catch (IOException ex) {
	                        System.err.println(ex);
	                    }  
                	}else{
                		image_load = "";
                	}
                	handler.sendEmptyMessage(MSG_DOWNLOADED);
                }
              }).start();
             
        }
		if (requestCode == 0 && data != null) {
			int start = Math.max(et_message.getSelectionStart(), 0);
			int end = Math.max(et_message.getSelectionEnd(), 0);
			et_message.getText().replace(Math.min(start, end), Math.max(start, end), smile_text[resultCode], 0, smile_text[resultCode].length());
			switch (resultCode){
			case 1:
				
				break;
			}
		}
        if (requestCode == 2 && data != null) {
            final int c = resultCode;
            String htmlColor = String.format("#%X", c);
            htmlColor = "#" + htmlColor.substring(3);
            Log.d(Constants.LogTag, "color -> " + htmlColor);
            String tag = "[color=" + htmlColor + "]";
            String tag2 = "[color=" + htmlColor + "]"+"[/color]";
            if (et_message.getSelectionStart() == et_message.getSelectionEnd()){
                int start = Math.max(et_message.getSelectionStart(), 0);
                int end = Math.max(et_message.getSelectionEnd(), 0);
                et_message.getText().replace(Math.min(start, end), Math.max(start, end),tag2, 0, tag2.length());
                et_message.setSelection(et_message.getSelectionStart() - "[/color]".length());
            }else{
                int start = Math.max(et_message.getSelectionStart(), 0);
                int end = Math.max(et_message.getSelectionEnd(), 0);
                et_message.getText().replace(Math.min(start, end),Math.min(start, end),tag, 0, tag.length());
                et_message.getText().replace(Math.max(et_message.getSelectionEnd(), 0),Math.max(et_message.getSelectionEnd(), 0),"[/color]", 0, "[/color]".length());

            }
        }
	}
	
	
	private Handler handler = new Handler() {

	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case MSG_DOWNLOADED:
	            	if (image_load.equals("null") || image_load.equals("")){
	            		Toast.makeText(getBaseContext(), "Файл поврежден или что то пошло не так.", Toast.LENGTH_SHORT).show();
	            	}else{
		            	int start = Math.max(et_message.getSelectionStart(), 0);
		            	int end = Math.max(et_message.getSelectionEnd(), 0);
		            	et_message.getText().replace(Math.min(start, end), Math.max(start, end),
		            			image_load, 0, image_load.length());
	            	}
                    dialog.dismiss();
	                break;
	            }
	        }
	    };
	
	public void send(){
		if (type.equals("user_page")){
			mes_status = SSDK_API.sendUserPage(et_message.getText().toString() + getString(R.string.send_from_android), commentable_id);
		}else if(type.equals("message")){
			mes_status = SSDK_API.sendMessage(et_message.getText().toString() + getString(R.string.send_from_android), commentable_id, to_id);
		}else if(type.equals("comment")){
			//mes_status = SSDK_API.creatComment(et_message.getText().toString() + getString(R.string.send_from_android), commentable_id, "Entry", cb_offtop.isChecked(), cb_review.isChecked());
			mes_status = SSDK_API.sendComment(et_message.getText().toString() + getString(R.string.send_from_android), commentable_id);
		}
	}
	
	public void edit(){
		mes_status = SSDK_API.editComment(et_message.getText().toString() + getString(R.string.send_from_android), comment_id, String.valueOf(cb_offtop.isChecked()));
	}
	
	//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
	private class AsyncSend extends AsyncTask<Void, Void, Void> {
			  
			  	@Override
			  	protected void onPreExecute() {
			  		setSupportProgressBarIndeterminateVisibility(true);
			  		
				}
				  
			  	protected void onPostExecute(Void result1) {
				  	setSupportProgressBarIndeterminateVisibility(false);
				  	if (mes_status == 200 || mes_status == 201){
				  		Toast.makeText(Activity_Add_Edit_Input.this, "Сообщение отправлено.", Toast.LENGTH_SHORT).show();
				  		setResult(1, new Intent());
				  		finish();
				  	}else{
				  		Toast.makeText(Activity_Add_Edit_Input.this, "Сообщение не отправлено.\nПовторите попытку позже.", Toast.LENGTH_SHORT).show();
				  	}
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					send();	
					return null;
				}

				
		  }
	
	//Асинхронная загрузка (Логинимся и получаем информацию о пользователе)
	private class AsyncEdit extends AsyncTask<Void, Void, Void> {
				  
				  	@Override
				  	protected void onPreExecute() {
				  		setSupportProgressBarIndeterminateVisibility(true);
				  		
					}
					  
				  	protected void onPostExecute(Void result1) {
					  	setSupportProgressBarIndeterminateVisibility(false);
					  	if (mes_status == 200){
					  		Toast.makeText(Activity_Add_Edit_Input.this, "Сообщение отправлено.", Toast.LENGTH_SHORT).show();
					  		setResult(1, new Intent());
					  		finish();
					  	}else{
					  		Toast.makeText(Activity_Add_Edit_Input.this, "Сообщение не отправлено.\nПовторите попытку позже.", Toast.LENGTH_SHORT).show();
					  	}
					}

					@Override
					protected Void doInBackground(Void... arg0) {
							edit();	
						return null;
					}

					
			  }

}
