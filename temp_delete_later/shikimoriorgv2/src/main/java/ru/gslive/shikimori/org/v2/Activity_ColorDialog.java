package ru.gslive.shikimori.org.v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;


public class Activity_ColorDialog extends ShikiSherlockActivity {

	ActionBar actionbar;
	

	
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		SpannableString s = new SpannableString(getResources().getString(R.string.app_name));
		Context context = getBaseContext();
	    s.setSpan(new Functions.TypefaceSpan(context, getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    		
	    setTheme(R.style.AppTheme_ActionBarStyle_BackHome);
	    
		actionbar = getSupportActionBar();
		actionbar.setTitle(s);
		actionbar.setSubtitle("Выбор цвета");
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		super.onCreate(savedInstanceState);
        if (Functions.preference.theme.equals("ligth")){
            setContentView(R.layout.w_activity_color_dialog);
        }else{
            setContentView(R.layout.d_activity_color_dialog);
        }


        final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        //OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);
        Button btn_select = (Button) findViewById(R.id.btn_select);

        picker.addSVBar(svBar);
        //picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

//To get the color
        picker.getColor();

//To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
// adds listener to the colorpicker which is implemented
//in the activity
        //picker.setOnColorChangedListener(this);

//to turn of showing the old color
        picker.setShowOldCenterColor(false);


        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                picker.setOldCenterColor(picker.getColor());
                setResult(picker.getColor(), new Intent());
                finish();
            }
        });
//adding onChangeListeners to bars
        /*opacitybar.setOnOpacityChangeListener(new OnOpacityChangeListener …)
        valuebar.setOnValueChangeListener(new OnValueChangeListener …)
        saturationBar.setOnSaturationChangeListener(new OnSaturationChangeListener …)*/


	}
	
	//—обыти¤ нажатий на меню
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
