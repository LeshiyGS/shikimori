package ru.gslive.shikimori.org.v2;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;

import net.simonvt.menudrawer.MenuDrawer;

import org.apache.http.util.ByteArrayBuffer;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import com.actionbarsherlock.app.ActionBar;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import com.commonsware.cwac.task.AsyncTaskEx;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.style.MetricAffectingSpan;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ToggleButton;

public class Functions {
	
	//Глобальная переменная настроек
	public static SSDK_Preference preference = new SSDK_Preference();
	
	static TextView tv_menu_login, tv_unread;
	static ImageView iv_menu_avatar;
		
	//Активити для меню
	@SuppressWarnings("rawtypes")
	public static Class[] Menu_Activity = { Activity_MainProfile.class, Activity_Animes.class, Activity_Mangas.class, Activity_Calendar.class,
		Activity_Topics.class, Activity_Community.class, Activity_Preference.class, Activity_About.class};
	
	//Глобальный счетчик оповещений
	public static String[] unread = new String[] {"0","0","0"};
	static int count_unread = 0;
	
	//ХешМап для изображений внутри постов
	//private final static HashMap <String, Drawable> mDrawableCache = new HashMap <String, Drawable>();
	public static Map<String, WeakReference<Drawable>> mDrawableCache = Collections.synchronizedMap(new WeakHashMap<String, WeakReference<Drawable>>());

    static public class HeightAnimation extends Animation {
	    protected final int originalHeight;
	    protected final View view;
	    protected float perValue;

	    public HeightAnimation(View view, int fromHeight, int toHeight) {
	        this.view = view;
	        this.originalHeight = fromHeight;
	        this.perValue = (toHeight - fromHeight);
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
	        view.requestLayout();
	    }

	    @Override
	    public boolean willChangeBounds() {
	        return true;
	    }
	}
	
	//Названия пунктов меню
	public static int[] menu_content = new int[] {R.string.profile, R.string.anime, R.string.manga,
			R.string.calendar, R.string.news, R.string.community,
			R.string.settings, R.string.about};
	
	//Есть ли доступ к сети
	public static Boolean isNetwork(Context context){
		String cs = Context.CONNECTIVITY_SERVICE;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(cs);
		if (cm.getActiveNetworkInfo() == null){
			Toast.makeText(context, "Проверьте интернет соединение.", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			return true;
		}
	}
	
	public static void setFirstTitle(TextView tv, String ru, String en){
		if (Functions.preference.english_first){
			if (ru.equals("null")){
				tv.setText(en);
			}else{
				tv.setText(en + " / " + ru);
			}
    	}else{
    		if (ru.equals("null")){
				tv.setText(en);
			}else{
				tv.setText(ru + " / " + en);  
			}
    	}
	}

	//Определяем планшет или нет
	public static boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
	
	//Определяем нажатие на основное меню
	public static boolean menu_item(Context context, @SuppressWarnings("rawtypes") Class activity, int position){
		if (!activity.equals(Menu_Activity[position])){
			Intent intent_class = new Intent(context, Menu_Activity[position]);
			intent_class.putExtra("login", "");
			intent_class.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent_class.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			context.startActivity(intent_class);
			return true;
		}else{
			return false;
		}
	
	}
	
	public static void saveFile(String path,String FileName, String text, Context context) {
	    FileOutputStream fos;
	    File root = new File (path);             

        File dir = new File (root.getAbsolutePath());
        if(dir.exists()==false) {
             dir.mkdirs();
        }

        File file = new File(dir, FileName);
	    try {
	    	
	        fos = new FileOutputStream(file);
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
	        outputStreamWriter.write(text);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        //Log.e("Exception", "File write failed: " + e.toString());
	    } 

	}
    
	
	public static String download(Context context, String name, String cache, String iUrl) {
        try {
            File root = new File (cache);             

            File dir = new File (root.getAbsolutePath());
            if(dir.exists()==false) {
                 dir.mkdirs();
            }

            URL url = new URL(iUrl); //you can write here any link
            File file = new File(dir, name);
            if(file.exists()){
            	
            }else{
            	//long startTime = System.currentTimeMillis();
            	
            	/* Open a connection to that URL. */
            	URLConnection ucon = url.openConnection();

            	/*
            	 * Define InputStreams to read from the URLConnection.
            	 */
            	InputStream is = ucon.getInputStream();
            	BufferedInputStream bis = new BufferedInputStream(is);

            	/*
            	 * Read bytes to the Buffer until there is nothing more to read(-1).
            	 */
            	ByteArrayBuffer baf = new ByteArrayBuffer(1024);
            	int current = 0;
            	while ((current = bis.read()) != -1) {
            		baf.append((byte) current);
            	}


            	/* Convert the Bytes read to a String. */
            	FileOutputStream fos = new FileOutputStream(file);
            	fos.write(baf.toByteArray());
            	fos.flush();
            	fos.close();
            }

    } catch (IOException e) {
        //Log.d("DownloadManager", "Error: " + e);
    }

		return null;
    }
	
	static void disableEnableControls(boolean enable, LinearLayout layout){
		for (int i = 0; i < layout.getChildCount(); i++) {
		    View child = layout.getChildAt(i);
		    child.setEnabled(false);
		}
	}
	
	//Холдер для любого кастомного адаптера
	static class ViewHolder {
        public TextView plusView;
        public TextView minusView;
        public TextView itemView;
        public ImageView imageView;
        public View_ImageView newimageView;
        public TextView textView;
        public TextView dateView;
        public TextView infoView;
        public TextView titleView;
        public TextView newView;
        public TextView review;
        public TextView offtopicView;
        public ImageView userView;
        public TextView clientView;
        public LinearLayout layoutView;
        public CheckBox checkView;
        public ToggleButton onoffView;
		public RadioButton optionView;
		public LinearLayout itemBack;
    }

	//Динамическая подгрузка картинок в текст сообщения
	private static ImageGetter imgGetter = new ImageGetter() {
		
		public Drawable getDrawable(String source) {
			if (source.contains("missing_logo")){
				source = Constants.SERVER + "/assets/globals/missing_original.jpg";
			}
			if (!source.contains("http")){
				source = Constants.SERVER + source;
			}
			if (mDrawableCache.containsKey(source))
				//return mDrawableCache.get(source);
				return mDrawableCache.get(source).get();
			new ImageDownloadAsyncTask(source).execute();
			return null;
		}
	};

    public static void parseHtml(String textHTML, int iii, int mmm, boolean clean, String body_clean, ArrayList<String> spoiler_names, Context context){
        if (clean){
            iii = 0;
        }
        int iiii = iii;

        Document json = Jsoup.parse(textHTML);

        for(Element element : json.select("a[class^=c-video b-video]")){
            element.after(" <a href="+element.attr("href")+"a>Ссылка на видео</a>");
        }

        for(Element element : json.select("img")){
            if (!element.attr("src").contains("http")) 	element.attr("src", Constants.SERVER + element.attr("src"));
        }

        for(Element element : json.select("a")){
            if (!element.attr("href").contains("http")) element.attr("href", Constants.SERVER + element.attr("href"));
        }

        for(Element element : json.select("div[class=after]")){
            element.before("[SEP]SP:false:" + iii + "!");
            element.remove();
            iii++;
        }

        for(Element element : json.select("div[class=b-spoiler unprocessed]")){
            spoiler_names.add(element.child(0).select("label").text());
            element.child(0).select("label").remove();
        }

        for(Element element : json.select("div[class=before")){
            element.after("[SEP]SP:true:" + iiii + "!");
            element.remove();
            iiii++;
        }

        for(Element element : json.select("del")){
            element.remove();
        }

        if (clean){
            mmm = 0;
        }
        for(Element element : json.select("img")){
            if (!element.attr("src").contains("/images/user/") && !element.attr("src").contains("/images/smileys/")){
                element.before("[SEP]CI:" + element.attr("src")+"!");
                element.remove();
                mmm++;
            }
        }

        body_clean = json.html();
    }


    public static Boolean isLollipop(){
        Boolean result;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion == Build.VERSION_CODES.LOLLIPOP){
            result = true;
        } else{
            result = false;
        }
        //return result;
        return false;
    }
	
	public static void addComment(String body, ArrayList<String> comments_sname, LinearLayout layout, final Context context, final Activity activity, Boolean is_link){
		ArrayList<LinearLayout> spoiler_layout = new ArrayList<LinearLayout>();
        spoiler_layout.add(layout);
        body = body.replace("[Отправлено с Android]", "");

        if (body.contains("[SEP]")){
			final String[] body_elements = body.split("\\[SEP\\]");
			for (int i=0;i < body_elements.length; i++){
                //Если кусок комментария начало спойлера
				if (body_elements[i].contains("SP:true")){
                    //Получаем номер названия спойлера
                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
                    body_elements[i] = body_elements[i].replace("SP:true:"+tag+"!", ""); //Удаляем служебную информацию
                    //Создаем элементы комментария
					final LinearLayout spoiler = new LinearLayout(context);
					final TextView text = new TextView(context);
                    TextView spoiler_name = new TextView(context);
                    if (Functions.preference.theme.equals("ligth")){
                        spoiler_name.setTextColor(context.getResources().getColor(R.color.white));
                        spoiler_name.setBackgroundColor(context.getResources().getColor(R.color.gray_dark));
                    }else{
                        spoiler_name.setTextColor(context.getResources().getColor(R.color.black));
                        spoiler_name.setBackgroundColor(context.getResources().getColor(R.color.gray_ligth));
                    }
                    spoiler_name.setPadding(5, 2, 5, 5);
                    spoiler_name.setTypeface(null, Typeface.BOLD);
                    spoiler_name.setText("[" + comments_sname.get(tag) + "]");
                    spoiler.setVisibility(View.GONE);
                    //Обработчик нажатия на название спойлера
                    spoiler_name.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (spoiler.getVisibility() == View.VISIBLE) {
                                spoiler.setVisibility(View.GONE);
                            } else {
                                spoiler.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    //Загрузка смайлов и авок
                    final int finalI = i;
                    ImageGetter imgGetter1 = new ImageGetter() {

                        public Drawable getDrawable(String source) {
                            if (source.contains("missing_logo")){
                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                            }
                            if (!source.contains("http")){
                                source = Constants.SERVER + source;
                            }
                            if (mDrawableCache.containsKey(source))
                                //return mDrawableCache.get(source);
                                return mDrawableCache.get(source).get();
                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                            return null;
                        }
                    };
                    text.setText(Html.fromHtml(body_elements[i], imgGetter1, null));
                    if (Functions.preference.theme.equals("ligth")){
                        text.setTextColor(context.getResources().getColor(R.color.black));
                    }else{
                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
                    }
                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
                    if(is_link) {
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    if (Functions.preference.theme.equals("ligth")){
                        spoiler.setBackgroundColor(context.getResources().getColor(R.color.gray_ligth));
                    }else{
                        spoiler.setBackgroundColor(context.getResources().getColor(R.color.gray_medium));
                    }
                    spoiler.setOrientation(LinearLayout.VERTICAL);
                    spoiler.setPadding(15, 0, 15, 0);
                    //Добавляем элементы на слой спойлера
                    spoiler_layout.add(spoiler);
                    spoiler_layout.get(spoiler_layout.size()-2).addView(spoiler_name);
                    spoiler_layout.get(spoiler_layout.size()-2).addView(spoiler);
                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
				//Если кусок комментария конец спойлера
                }else if(body_elements[i].contains("SP:false")){
                    //закрываем слой и добавляем текстовое поле в предидущем
                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
                    body_elements[i] = body_elements[i].replace("SP:false:"+tag+"!", ""); //Удаляем служебную информацию
					final TextView text = new TextView(context);
                    final int finalI = i;
                    //Обработчик нажатия на название спойлера
                    ImageGetter imgGetter2 = new ImageGetter() {

                        public Drawable getDrawable(String source) {
                            if (source.contains("missing_logo")){
                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                            }
                            if (!source.contains("http")){
                                source = Constants.SERVER + source;
                            }
                            if (mDrawableCache.containsKey(source))
                                //return mDrawableCache.get(source);
                                return mDrawableCache.get(source).get();
                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                            return null;
                        }
                    };
                    text.setText(Html.fromHtml(body_elements[i], imgGetter2, null));
                    if (Functions.preference.theme.equals("ligth")){
                        text.setTextColor(context.getResources().getColor(R.color.black));
                    }else{
                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
                    }
                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
                    if(is_link) {
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    //Удаляем слой из массива
                    spoiler_layout.remove(spoiler_layout.size()-1);
                    //Добавляем текст после спойлера
                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
				}else if(body_elements[i].contains("CI:")){
                    final String tag = body_elements[i].substring(0, body_elements[i].indexOf("!")+1);
                    body_elements[i] = body_elements[i].replace(tag, ""); //Удаляем служебную информацию
                    //Вставляем картинку
					ImageView image = new ImageView(context);
                    if (Functions.preference.theme.equals("ligth")){
                        image.setBackgroundResource(R.drawable.w_border);
                    }else{
                        image.setBackgroundResource(R.drawable.d_border);
                    }
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    //int width = display.getWidth(); // ((display.getWidth()*20)/100)
                    int height = 0;
                    if (tag.contains("x64")){
                        height = ((display.getHeight() * 10) / 100);// ((display.getHeight()*30)/100)
                    }else {
                        height = ((display.getHeight() * 25) / 100);// ((display.getHeight()*30)/100)
                    }
                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
                    image.setLayoutParams(parms);

                    image.setOnClickListener(new OnClickListener() {
                        @SuppressLint("InlinedApi")
                        @Override
                        public void onClick(View view) {
                            final Dialog mSplashDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar);
                            mSplashDialog.setContentView(R.layout.fullscreen_web);
                            WebView web = (WebView) mSplashDialog.findViewById(R.id.wv_web);
                            if (tag.contains("/images/user_image/thumbnail/")){
                                web.loadUrl(tag.substring(3, tag.length() - 1).replace("thumbnail", "original"));
                                //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1).replace("thumbnail", "original"), true, false, 0);
                            }else {
                                if (tag.contains("/person/x64/") || tag.contains("/character/x64/")){
                                    web.loadUrl(tag.substring(3, tag.length() - 1).replace("x64", "original"));
                                    //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1).replace("x64", "original"), true, false, 0);
                                }else {
                                    web.loadUrl(tag.substring(3, tag.length() - 1));
                                    //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1), true, false, 0);
                                }
                            }
                            mSplashDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            mSplashDialog.setCancelable(true);
                            mSplashDialog.show();


                        }
                    });


                    final TextView text = new TextView(context);
                    final int finalI = i;
                    //Обработчик нажатия на название спойлера
                    ImageGetter imgGetter3 = new ImageGetter() {

                        public Drawable getDrawable(String source) {
                            if (source.contains("missing_logo")){
                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                            }
                            if (!source.contains("http")){
                                source = Constants.SERVER + source;
                            }
                            if (mDrawableCache.containsKey(source))
                                //return mDrawableCache.get(source);
                                return mDrawableCache.get(source).get();
                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                            return null;
                        }
                    };
					text.setText(Html.fromHtml(body_elements[i], imgGetter3, null));
                    if (Functions.preference.theme.equals("ligth")){
                        text.setTextColor(context.getResources().getColor(R.color.black));
                    }else{
                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
                    }
                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
                    if(is_link) {
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                    Ion.with(image)
                            //.placeholder(R.drawable.ic_launcher)
                            .animateLoad(R.anim.spin_animation)
                            .error(R.drawable.missing_preview)
                            .load(tag.substring(3, tag.length() - 1));
					//ImageLoader.getInstance().displayImage(tag.substring(3, tag.length()-1), image);
                    spoiler_layout.get(spoiler_layout.size()-1).addView(image);
                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
				}else if(body_elements[i].contains("VI:")){
                    ////
                    final String tag = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[2];
                    final String tag2 = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[1];
                    //
                    body_elements[i] = body_elements[i].replace(body_elements[i].substring(0, body_elements[i].indexOf("!")+1), ""); //Удаляем служебную информацию
                    //Вставляем картинку
                    ImageView image = new ImageView(context);
                    if (Functions.preference.theme.equals("ligth")){
                        image.setBackgroundResource(R.drawable.w_border);
                    }else{
                        image.setBackgroundResource(R.drawable.d_border);
                    }
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    //int width = display.getWidth(); // ((display.getWidth()*20)/100)
                    int height = 0;
                    if (tag.contains("x64")){
                        height = ((display.getHeight() * 10) / 100);// ((display.getHeight()*30)/100)
                    }else {
                        height = ((display.getHeight() * 25) / 100);// ((display.getHeight()*30)/100)
                    }
                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
                    image.setLayoutParams(parms);

                    image.setOnClickListener(new OnClickListener() {
                        @SuppressLint("InlinedApi")
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tag2));
                                context.startActivity(myIntent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }


                        }
                    });


                    final TextView text = new TextView(context);
                    final int finalI = i;
                    //Обработчик нажатия на название спойлера
                    ImageGetter imgGetter3 = new ImageGetter() {

                        public Drawable getDrawable(String source) {
                            if (source.contains("missing_logo")){
                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                            }
                            if (!source.contains("http")){
                                source = Constants.SERVER + source;
                            }
                            if (mDrawableCache.containsKey(source))
                                //return mDrawableCache.get(source);
                                return mDrawableCache.get(source).get();
                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                            return null;
                        }
                    };
                    text.setText(Html.fromHtml(body_elements[i], imgGetter3, null));
                    if (Functions.preference.theme.equals("ligth")){
                        text.setTextColor(context.getResources().getColor(R.color.black));
                    }else{
                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
                    }
                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
                    if(is_link) {
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }

                    Ion.with(image)
                            //.placeholder(R.drawable.ic_launcher)
                            .animateLoad(R.anim.spin_animation)
                            .error(R.drawable.missing_preview)
                            .load(tag);
                    //ImageLoader.getInstance().displayImage(tag.substring(3, tag.length()-1), image);
                    spoiler_layout.get(spoiler_layout.size()-1).addView(image);
                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
                }else{
                    //Просто вставляем текстовое поле
					final TextView text = new TextView(context);
                    final int finalI = i;
                    //Обработчик нажатия на название спойлера
                    ImageGetter imgGetter4 = new ImageGetter() {

                        public Drawable getDrawable(String source) {
                            if (source.contains("missing_logo")){
                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                            }
                            if (!source.contains("http")){
                                source = Constants.SERVER + source;
                            }
                            if (mDrawableCache.containsKey(source))
                                //return mDrawableCache.get(source);
                                return mDrawableCache.get(source).get();
                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
                            return null;
                        }
                    };
					text.setText(Html.fromHtml(body_elements[i], imgGetter4, null));
                    text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
					if (Functions.preference.theme.equals("ligth")){
		    			text.setTextColor(context.getResources().getColor(R.color.black));
					}else{
						text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
					}
		    		text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
                    if(is_link) {
                        text.setMovementMethod(LinkMovementMethod.getInstance());
                    }
					layout.addView(text);
				}
				
			}
		}else{
			final TextView text = new TextView(context);
            //Обработчик нажатия на название спойлера
            final String finalBody = body;
            ImageGetter imgGetter5 = new ImageGetter() {

                public Drawable getDrawable(String source) {
                    if (source.contains("missing_logo")){
                        source = Constants.SERVER + "/assets/globals/missing_original.jpg";
                    }
                    if (!source.contains("http")){
                        source = Constants.SERVER + source;
                    }
                    if (mDrawableCache.containsKey(source))
                        //return mDrawableCache.get(source);
                        return mDrawableCache.get(source).get();
                    new ImageDownloadAsyncTask2(source, finalBody, text,  context).execute();
                    return null;
                }
            };
			text.setText(Html.fromHtml(body, imgGetter5, null), TextView.BufferType.SPANNABLE);
    		if (Functions.preference.theme.equals("ligth")){
    			text.setTextColor(context.getResources().getColor(R.color.black));
			}else{
				text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
			}
    		text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
            if(is_link) {
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
			layout.addView(text);
		}
		
		
		
	}

	//Первичный загрузчик изображений
	public static class ImageDownloadAsyncTask extends AsyncTaskEx<Void, Void, Void> {
			private String source;

		    public ImageDownloadAsyncTask(String source) {
		        this.source = source;
		    }

		    @Override
		    protected Void doInBackground(Void... params) {
		        if (!mDrawableCache.containsKey(source)) {
		            try {
		                 
		            	//Скачиваем картинку в наш кэш
		                URL url = new URL(source);
		                URLConnection connection = url.openConnection();
		                InputStream is = connection.getInputStream();
	
		                Drawable drawable = Drawable.createFromStream(is, "src");
	
		                is.close();
	
		                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
			            //mDrawableCache.put(source, drawable);
		                mDrawableCache.put(source, new WeakReference<Drawable>(drawable));
		            } catch (MalformedURLException e) {
		                e.printStackTrace();
		            } catch (Throwable t) {
		                t.printStackTrace();
		            }
		        }
		        return null;
		    }

		    @Override
		    protected void onPostExecute(Void result) {

		    }
		}
		
	//Вторичный загрузчик изображений
	public static	class ImageDownloadAsyncTask2 extends AsyncTaskEx<Void, Void, Void> {
			private String source;
		    private String message;
		    private TextView textView;
		    private Context context;

		    public ImageDownloadAsyncTask2(String source, String message,
		            TextView textView, Context context) {
		        this.source = source;
		        this.message = message;
		        this.textView = textView;
		        this.context = context;
		    }



        @Override
		    protected Void doInBackground(Void... params) {
		        if (!mDrawableCache.containsKey(source)) {
		            try {
		            	//Скачиваем картинку в наш кэш
		                URL url = new URL(source);
		                URLConnection connection = url.openConnection();
		                InputStream is = connection.getInputStream();

		                /*if (mDrawableCache.size() > 40){
		                	mDrawableCache.clear();
		                }*/
		                
		                Bitmap bmp = BitmapFactory.decodeStream(is);
		                DisplayMetrics dm =
		                context.getResources().getDisplayMetrics();
		                if (bmp.getWidth() < 20){
		                	bmp.setDensity((int) (dm.densityDpi/1.5));
		                }else{
		                    bmp.setDensity(dm.densityDpi); 
		                }
		                Drawable drawable = new BitmapDrawable(context.getResources(),bmp);

		                is.close();

		                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
		                        drawable.getIntrinsicHeight());
		                //mDrawableCache.put(source, drawable);
		                mDrawableCache.put(source, new WeakReference<Drawable>(drawable));
		            } catch (MalformedURLException e) {
		                e.printStackTrace();
		            } catch (Throwable t) {
		                t.printStackTrace();
		            }
		        }
		        return null;
		    }

		    @Override
		    protected void onPostExecute(Void result) {
		        // Переустанавливаем содержимое нашего поля
		        textView.setText(Html.fromHtml(message, imgGetter, null));
		    }
		    
		}
	
	//Адаптер для меню
	public static class MenuAdapter extends BaseAdapter {
  		private LayoutInflater mLayoutInflater;
  		private Context context;

  		public MenuAdapter (Context ctx) {  
  		      mLayoutInflater = LayoutInflater.from(ctx);  
  		      context = ctx;
  		}

  		public int getCount () {  
  		      return menu_content.length;  
  		    }  
  		          
  		    public Object getItem (int position) {  
  		      return position;  
  		    }  
  		          
  		    public long getItemId (int position) {  
  		      return position;  
  		    }  
  		          
  		    public String getString (int position) {  
  		      return "" + position;  
  		    }  
  		    
  		    
  		    @SuppressLint({ "ViewHolder", "InflateParams" })
			public View getView(int position, View convertView, ViewGroup parent) {   
 		    	
  		    	if (Functions.preference.theme.equals("ligth")){
  		    		convertView = mLayoutInflater.inflate(R.layout.w_item_slide_menu, null);
  				}else{
  					convertView = mLayoutInflater.inflate(R.layout.d_item_slide_menu, null);
  				}

  		    	final ViewHolder holder;
  		    	holder = new ViewHolder();
 		      
  		    	holder.itemView = (TextView) convertView.findViewById(R.id.tv_menu_item);
  		    	convertView.setTag(holder);
  		    	
				holder.itemView.setText(context.getString(menu_content[position]));

  		    	return convertView;  
  		    }
  	}
	
	//Использование стороннего шрифта
  	public static class TypefaceSpan extends MetricAffectingSpan {
  	    private LruCache<String, Typeface> sTypefaceCache =  new LruCache<String, Typeface>(12);

  	    private Typeface mTypeface;

  	    public TypefaceSpan(Context context, String typefaceName) {
  	        mTypeface = sTypefaceCache.get(typefaceName);

  	        if (mTypeface == null) {
  	            mTypeface = Typeface.createFromAsset(context.getApplicationContext()
  	                    .getAssets(), String.format("fonts/%s", typefaceName));

  	            sTypefaceCache.put(typefaceName, mTypeface);
  	        }
  	    }

  	    @Override
  	    public void updateMeasureState(TextPaint p) {
  	        p.setTypeface(mTypeface);
  	        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
  	    }

  	    @Override
  	    public void updateDrawState(TextPaint tp) {
  	        tp.setTypeface(mTypeface);
  	        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
  	    }
  	}
  	
  	public static String shiki_translate(String tr){
  		return tr.replaceAll("Japanese", "Сэйю")
		.replaceAll("Theme Song Performance", "Исполнение гл. муз. темы")
		.replaceAll("Inserted Song Performance", "Музыкальное сопровождение")
		.replaceAll("Storyboard", "Раскадровка")		
		.replaceAll("Episode Director", "Режиссёр эпизодов")
		.replaceAll("Assistant Director", "Помощник режиссёра")
		.replaceAll("Story & Art", "Сюжет и рисовка")
		.replaceAll("Original Creator", "Автор оригинала")
		.replaceAll("Music","Музыка")
		.replaceAll("Theme Song Arrangement","Аранжировка гл. муз. темы")
		.replaceAll("Theme Song Composition","Композитор гл. муз. темы")
		.replaceAll("Sound Director","Звукорежиссер")
		.replaceAll("Script","Сценарий")
		.replaceAll("Series Composition","Компоновка серий")
		.replaceAll("Screenplay","Сценарий")
		.replaceAll("Story","Сюжет")
		.replaceAll("Executive Producer","Исполнительн. продюсер")
		.replaceAll("Sound Effects","Звуковые эффекты")
		.replaceAll("Recording","Звукооператор")
		.replaceAll("Sound Supervisor","Звукорежиссёр")
		.replaceAll("Editing","Монтаж")
		.replaceAll("Character Design","Дизайн персонажей")
		.replaceAll("Key Animation","Ключевая анимация")
		.replaceAll("Chief Animation Director","Главный аниматор")
		.replaceAll("Assistant Animation Director","Помощник режиссера анимации")
		.replaceAll("In-Between Animation","Промежуточная анимация")
		.replaceAll("Mechanical Design","Дизайн макетов")
		.replaceAll("","")
		.replaceAll("","")
		.replaceAll("","")
		.replaceAll("","")
		.replaceAll("","")
		.replaceAll("","")
		.replaceAll("Animation Director","Режиссер анимации")
		.replaceAll("Producer", "Продюсер")
  		.replaceAll("Director", "Режиссёр");
  	}

  	@SuppressLint("SimpleDateFormat")
	public static String getDate(long milliSeconds, String dateFormat)
  	{
  	    // Create a DateFormatter object for displaying date in specified format.
  	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

  	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
  	     Calendar calendar = Calendar.getInstance();
  	     calendar.setTimeInMillis(milliSeconds);
  	     return formatter.format(calendar.getTime());
  	}
  	
  	
	@SuppressLint("SimpleDateFormat")
	public static String in_time(String site_date, Boolean full){
		//Log.d(Constants.LogTag, "-> " + site_date);
		if (full){
		long res_time = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        try {
            Date mDate = format.parse(site_date.replace("T", " ").replace(site_date.substring(site_date.indexOf("."), site_date.indexOf(".")+4), " GMT"));
            long timeInMilliseconds = mDate.getTime();
            res_time = timeInMilliseconds;
        } catch (ParseException e) {
                    e.printStackTrace();
        }
        	return getDate(res_time,"d MMMM yyyy HH:mm");
        }else{
        	long res_time = 0;
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date mDate = format.parse(site_date);
                long timeInMilliseconds = mDate.getTime();
                res_time = timeInMilliseconds;
            } catch (ParseException e) {
                        e.printStackTrace();
            }
        	return getDate(res_time,"d MMMM yyyy");
        }
	}
	
	//Асинхронное удаление комментария
	static class CommentDelete extends AsyncTaskEx<Void, Void, Void> {
		String kawai;
		String comment_id;
		PullToRefreshListView list;
	
		public CommentDelete (String comment_id, String kawai, PullToRefreshListView list) {  
			this.kawai = kawai;
			this.comment_id = comment_id;
			this.list = list;
		}
		
		@Override
		protected void onPreExecute() {
				
		}
						  
		protected void onPostExecute(Void result1) {
			list.setRefreshing(true);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			SSDK_API.deleteComment(comment_id);
			String[] token = SSDK_API.getToken(kawai);
			
			try {
				Response res = Jsoup
						.connect(Constants.SERVER + "/comments/" + comment_id)
						.ignoreContentType(true)
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36")
						.header("X-CSRF-Token", token[0])
						.cookie("_kawai_session", token[1])
						.data("_method","delete")
						.method(Method.POST)
						.execute();
				res.statusCode();
					
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return null;
		}				
	}
	
    //Получаем Настройки приложения
    public static void getPreference(Context context){ 
    	preference = SSDK_Preference.parse(context);
    }
    
    //Создаем Контекстное меню коментариев
    public static ContextMenu createContextMenu(ContextMenu menu, SSDK_Comments item){
    	menu.setHeaderTitle("Выберите:");
		if (!item.user_id.equals(Functions.preference.user_id)){
			menu.add(0, 1005, 0, "Ответить");
			menu.add(0, 1001, 0, "Цитировать");
		}
		if (item.user_id.equals(Functions.preference.user_id) && item.can_be_edited){
			menu.add(0, 1002, 0, "Редактировать");	
		}
		if (item.can_be_edited){
			menu.add(0, 1003, 0, "Удалить");
		}
		menu.add(0, 1004, 0, "Копировать текст");
		Document link = Jsoup.parse(item.body_clean);
		Elements link_src = link.select("a[href]");
		for (Element src : link_src){
			if (!src.attr("abs:href").equals("")){
				if (src.attr("abs:href").contains("youtube.com")){
					menu.add(0, 1010, 0, "[YouTube] " + src.attr("abs:href"));
				}else{
					menu.add(0, 1010, 0, src.attr("abs:href"));
				}
			}
		}
    	
    	
    	return menu;
    }

    //Действия при выборе контекстного меню комментария
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Intent choiseContextItem(MenuItem item, SSDK_Comments comments, String type, String target_id, Context context, PullToRefreshListView list){
    	Intent intent_add = new Intent(context, Activity_Add_Edit_Input.class);
    	intent_add.putExtra("type", type);
	    switch (item.getItemId()) {
	    case 1000:
	    	//Личное сообщение
    		intent_add.putExtra("commentable_id", comments.id);
	        return intent_add;
	    case 1005:
	    	//Цитата
    		intent_add.putExtra("commentable_id", target_id);
    		intent_add.putExtra("text","[comment="+comments.id+"]" + comments.user_name + "[/comment],");
	        return intent_add;
	    case 1001:
	    	//Цитата
    		intent_add.putExtra("commentable_id", target_id);
    		intent_add.putExtra("text","[quote="+comments.user_name+"]"+comments.body+"[/quote]");
	        return intent_add;
	    case 1002:
	    	//Редактирование своего сообщения
    		intent_add.putExtra("comment_id", comments.id);
    		intent_add.putExtra("text", comments.body);
    		intent_add.putExtra("action", "edit");
	        return intent_add;
	    case 1003:
	    	//Удалить сообщение
	    	new CommentDelete(comments.id, Functions.preference.kawai, list).execute();
	        return null;
	    case 1004:
	    	//Копировать сообщение
	    	
	    	int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	    	if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB){
	    	     android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
	    	        ClipData clip = ClipData.newPlainText("comment", comments.body);
	    	        clipboard.setPrimaryClip(clip); 
	    	} else{
	    	    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE); 
	    	    clipboard.setText(comments.body);
	    	}
	    		    	
			//ClipboardManager ClipMan = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	        //ClipMan.setText(comments.body);
	        return null;
	    case 1010:
			//Переход по ссылке
	    	String url = item.getTitle().toString().replace("[YouTube] ", "");
	    	if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
	    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	        return browserIntent;
	    default:
	        return null;
	    }
    	
    	
    }
    
    
    public static MenuDrawer setMenuDrawer(ActionBar actionbar, String subTitle, int layout, int rotate, final Context context, final Activity activity){

    	final MenuDrawer mDrawer;
    	final float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (280 * scale + 0.5f);
		
		SpannableString  s = new SpannableString(context.getResources().getString(R.string.app_name));
        s.setSpan(new Functions.TypefaceSpan(context, context.getString(R.string.shiki_font)), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (isLollipop()){
            actionbar.setTitle(subTitle);
        }else{
            actionbar.setTitle(s);
            actionbar.setSubtitle(subTitle);
        }

        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayHomeAsUpEnabled(true);
	    
	    if (Functions.isTablet(context) && ((rotate == Surface.ROTATION_0) || (rotate == Surface.ROTATION_180))){
			mDrawer = MenuDrawer.attach(activity, MenuDrawer.Type.STATIC);
		}else{
			mDrawer = MenuDrawer.attach(activity);
		}
		mDrawer.setMenuSize(pixels);
		mDrawer.setContentView(layout);
		if (Functions.preference.theme.equals("ligth")){
			 mDrawer.setMenuView(R.layout.w_menu);
			 mDrawer.setDropShadow(R.drawable.w_shadow);
			 mDrawer.setDropShadowSize((int) (8 * scale));
		}else{
			 mDrawer.setMenuView(R.layout.d_menu);
			 mDrawer.setDropShadow(R.drawable.d_shadow);
			 mDrawer.setDropShadowSize((int) (8 * scale));
		}
	    mDrawer.setDropShadowEnabled(true);
	    
	    mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
		    
	    
	    TextView tv_menu_shiki = (TextView) mDrawer.findViewById(R.id.tv_menu_shiki);
        tv_menu_shiki.setText(s);
        
        //Настройка меню
        View_GridView lv_menu = (View_GridView) mDrawer.findViewById(R.id.lv_menu);
        MenuAdapter adapter_menu = new Functions.MenuAdapter(activity);
        lv_menu.setAdapter(adapter_menu);
        lv_menu.setExpanded(true);
        //lv_menu.setDivider(null);
        //lv_menu.setDividerHeight(0);
        
        final MenuDrawer mDrawer2 = mDrawer;
        
        lv_menu.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		if (!Functions.menu_item(context, activity.getClass(), position)) mDrawer2.toggleMenu(true); 
        			else mDrawer2.toggleMenu(false);  
		
        	}
        });
        
      //-----------------
        tv_menu_login = (TextView) mDrawer.findViewById(R.id.tv_menu_login);
        iv_menu_avatar = (ImageView) mDrawer.findViewById(R.id.iv_menu_avatar);
              
        tv_menu_login.setText(Functions.preference.login);
        ImageLoader.getInstance().displayImage(Functions.preference.avatar, iv_menu_avatar);
      		
        tv_unread = (TextView) mDrawer.findViewById(R.id.tv_unread);
      		      		
        tv_unread.setOnClickListener(new View.OnClickListener() {
      		    public void onClick(View v) {
      		    	tv_unread.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
      		    	if (!Functions.unread[0].equals("0")){
      		    		Intent intent_1 = new Intent(context, Activity_News.class);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      		    		context.startActivity(intent_1);
      		    		mDrawer.toggleMenu(false);
      		    	}else if(!Functions.unread[1].equals("0")){
      		    		Intent intent_1 = new Intent(context, Activity_Inbox.class);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      		    		context.startActivity(intent_1);
      		    		mDrawer.toggleMenu(false);
      		    	}else if(!Functions.unread[2].equals("0")){
      		    		Intent intent_1 = new Intent(context, Activity_Notify.class);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      		    		intent_1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      		    		context.startActivity(intent_1);
      		    		mDrawer.toggleMenu(false);
      		    	}
      		    }
      		});
      		//-----------------
              return mDrawer;
    }
    
	static void updateWidget(Context context) { 
        Intent i = new Intent(context, Widget_1x1_Provider.class); 
        i.setAction(Constants.WIDGET_1X1_UPDATE); 
        context.sendBroadcast(i); 
    } 
	
	static void slideIn(View view, int hight, int duration)
	{
		HeightAnimation heightAnim = new HeightAnimation(view, hight, 0);
		heightAnim.setDuration(duration);
		view.startAnimation(heightAnim);
	}
	static void slideOut(View view, int hight, int duration)
	{
		HeightAnimation heightAnim = new HeightAnimation(view, 0, hight);
		heightAnim.setDuration(duration);
		view.startAnimation(heightAnim);
	}
	

	
	
}
