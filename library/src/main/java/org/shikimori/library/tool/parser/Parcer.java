//package org.shikimori.library.tool.parser;
//
//import android.app.Activity;
//import android.widget.LinearLayout;
//
//import org.shikimori.library.tool.parser.elements.Spoiler;
//
//import java.util.ArrayList;
//
///**
// * Created by Владимир on 09.04.2015.
// */
//public class Parcer {
//
//    public static void addComment(String body, ArrayList<String> comments_sname, LinearLayout layout, final Activity activity, Boolean is_link){
//        ArrayList<LinearLayout> spoiler_layout = new ArrayList<>();
//        spoiler_layout.add(layout);
//        body = body.replace("[Отправлено с Android]", "");
//
//        if (body.contains("[SEP]")){
//            final String[] body_elements = body.split("\\[SEP\\]");
//            for (int i=0;i < body_elements.length; i++){
//                //Если кусок комментария начало спойлера
//                if (body_elements[i].contains("SP:true")){
//                    //Получаем номер названия спойлера
//                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
//                    body_elements[i] = body_elements[i].replace("SP:true:"+tag+"!", ""); //Удаляем служебную информацию
//                    //Создаем элементы комментария
//
//                    Spoiler sp = new Spoiler(activity);
//                    sp.setTitle(comments_sname.get(tag));
//
////                    final LinearLayout spoiler = new LinearLayout(context);
////                    final TextView text = new TextView(context);
////                    TextView spoiler_name = new TextView(context);
////                    if (Functions.preference.theme.equals("ligth")){
////                        spoiler_name.setTextColor(context.getResources().getColor(R.color.white));
////                        spoiler_name.setBackgroundColor(context.getResources().getColor(R.color.gray_dark));
////                    }else{
////                        spoiler_name.setTextColor(context.getResources().getColor(R.color.black));
////                        spoiler_name.setBackgroundColor(context.getResources().getColor(R.color.gray_ligth));
////                    }
////                    spoiler_name.setPadding(5, 2, 5, 5);
////                    spoiler_name.setTypeface(null, Typeface.BOLD);
////                    spoiler_name.setText("[" + comments_sname.get(tag) + "]");
////                    spoiler.setVisibility(View.GONE);
////                    //Обработчик нажатия на название спойлера
////                    spoiler_name.setOnClickListener(new OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            if (spoiler.getVisibility() == View.VISIBLE) {
////                                spoiler.setVisibility(View.GONE);
////                            } else {
////                                spoiler.setVisibility(View.VISIBLE);
////                            }
////
////                        }
////                    });
//                    //Загрузка смайлов и авок
//                    final int finalI = i;
//                    ImageGetter imgGetter1 = new ImageGetter() {
//
//                        public Drawable getDrawable(String source) {
//                            if (source.contains("missing_logo")){
//                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                            }
//                            if (!source.contains("http")){
//                                source = Constants.SERVER + source;
//                            }
//                            if (mDrawableCache.containsKey(source))
//                                //return mDrawableCache.get(source);
//                                return mDrawableCache.get(source).get();
//                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
//                            return null;
//                        }
//                    };
//                    text.setText(Html.fromHtml(body_elements[i], imgGetter1, null));
//                    if(is_link) {
//                        text.setMovementMethod(LinkMovementMethod.getInstance());
//                    }
////                    if (Functions.preference.theme.equals("ligth")){
////                        spoiler.setBackgroundColor(context.getResources().getColor(R.color.gray_ligth));
////                    }else{
////                        spoiler.setBackgroundColor(context.getResources().getColor(R.color.gray_medium));
////                    }
//                    spoiler.setOrientation(LinearLayout.VERTICAL);
//                    spoiler.setPadding(15, 0, 15, 0);
//                    //Добавляем элементы на слой спойлера
//                    spoiler_layout.add(spoiler);
//                    spoiler_layout.get(spoiler_layout.size()-2).addView(spoiler_name);
//                    spoiler_layout.get(spoiler_layout.size()-2).addView(spoiler);
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
//                    //Если кусок комментария конец спойлера
//                }else if(body_elements[i].contains("SP:false")){
//                    //закрываем слой и добавляем текстовое поле в предидущем
//                    int tag = Integer.parseInt(body_elements[i].substring(0, body_elements[i].indexOf("!")).split(":")[2]);
//                    body_elements[i] = body_elements[i].replace("SP:false:"+tag+"!", ""); //Удаляем служебную информацию
//                    final TextView text = new TextView(context);
//                    final int finalI = i;
//                    //Обработчик нажатия на название спойлера
//                    ImageGetter imgGetter2 = new ImageGetter() {
//
//                        public Drawable getDrawable(String source) {
//                            if (source.contains("missing_logo")){
//                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                            }
//                            if (!source.contains("http")){
//                                source = Constants.SERVER + source;
//                            }
//                            if (mDrawableCache.containsKey(source))
//                                //return mDrawableCache.get(source);
//                                return mDrawableCache.get(source).get();
//                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
//                            return null;
//                        }
//                    };
//                    text.setText(Html.fromHtml(body_elements[i], imgGetter2, null));
//                    if (Functions.preference.theme.equals("ligth")){
//                        text.setTextColor(context.getResources().getColor(R.color.black));
//                    }else{
//                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//                    }
//                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
//                    if(is_link) {
//                        text.setMovementMethod(LinkMovementMethod.getInstance());
//                    }
//                    //Удаляем слой из массива
//                    spoiler_layout.remove(spoiler_layout.size()-1);
//                    //Добавляем текст после спойлера
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
//                }else if(body_elements[i].contains("CI:")){
//                    final String tag = body_elements[i].substring(0, body_elements[i].indexOf("!")+1);
//                    body_elements[i] = body_elements[i].replace(tag, ""); //Удаляем служебную информацию
//                    //Вставляем картинку
//                    ImageView image = new ImageView(context);
//                    if (Functions.preference.theme.equals("ligth")){
//                        image.setBackgroundResource(R.drawable.w_border);
//                    }else{
//                        image.setBackgroundResource(R.drawable.d_border);
//                    }
//                    Display display = activity.getWindowManager().getDefaultDisplay();
//                    //int width = display.getWidth(); // ((display.getWidth()*20)/100)
//                    int height = 0;
//                    if (tag.contains("x64")){
//                        height = ((display.getHeight() * 10) / 100);// ((display.getHeight()*30)/100)
//                    }else {
//                        height = ((display.getHeight() * 25) / 100);// ((display.getHeight()*30)/100)
//                    }
//                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
//                    image.setLayoutParams(parms);
//
//                    image.setOnClickListener(new OnClickListener() {
//                        @SuppressLint("InlinedApi")
//                        @Override
//                        public void onClick(View view) {
//                            final Dialog mSplashDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar);
//                            mSplashDialog.setContentView(R.layout.fullscreen_web);
//                            WebView web = (WebView) mSplashDialog.findViewById(R.id.wv_web);
//                            if (tag.contains("/images/user_image/thumbnail/")){
//                                web.loadUrl(tag.substring(3, tag.length() - 1).replace("thumbnail", "original"));
//                                //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1).replace("thumbnail", "original"), true, false, 0);
//                            }else {
//                                if (tag.contains("/person/x64/") || tag.contains("/character/x64/")){
//                                    web.loadUrl(tag.substring(3, tag.length() - 1).replace("x64", "original"));
//                                    //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1).replace("x64", "original"), true, false, 0);
//                                }else {
//                                    web.loadUrl(tag.substring(3, tag.length() - 1));
//                                    //aq.id(web).progress(R.id.progress).webImage(tag.substring(3, tag.length() - 1), true, false, 0);
//                                }
//                            }
//                            mSplashDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                            mSplashDialog.setCancelable(true);
//                            mSplashDialog.show();
//
//
//                        }
//                    });
//
//
//                    final TextView text = new TextView(context);
//                    final int finalI = i;
//                    //Обработчик нажатия на название спойлера
//                    ImageGetter imgGetter3 = new ImageGetter() {
//
//                        public Drawable getDrawable(String source) {
//                            if (source.contains("missing_logo")){
//                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                            }
//                            if (!source.contains("http")){
//                                source = Constants.SERVER + source;
//                            }
//                            if (mDrawableCache.containsKey(source))
//                                //return mDrawableCache.get(source);
//                                return mDrawableCache.get(source).get();
//                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
//                            return null;
//                        }
//                    };
//                    text.setText(Html.fromHtml(body_elements[i], imgGetter3, null));
//                    if (Functions.preference.theme.equals("ligth")){
//                        text.setTextColor(context.getResources().getColor(R.color.black));
//                    }else{
//                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//                    }
//                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
//                    if(is_link) {
//                        text.setMovementMethod(LinkMovementMethod.getInstance());
//                    }
//
//                    Ion.with(image)
//                            //.placeholder(R.drawable.ic_launcher)
//                            .animateLoad(R.anim.spin_animation)
//                            .error(R.drawable.missing_preview)
//                            .load(tag.substring(3, tag.length() - 1));
//                    //ImageLoader.getInstance().displayImage(tag.substring(3, tag.length()-1), image);
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(image);
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
//                }else if(body_elements[i].contains("VI:")){
//                    ////
//                    final String tag = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[2];
//                    final String tag2 = "http:" + body_elements[i].substring(3, body_elements[i].indexOf("!")).split("http:")[1];
//                    //
//                    body_elements[i] = body_elements[i].replace(body_elements[i].substring(0, body_elements[i].indexOf("!")+1), ""); //Удаляем служебную информацию
//                    //Вставляем картинку
//                    ImageView image = new ImageView(context);
//                    if (Functions.preference.theme.equals("ligth")){
//                        image.setBackgroundResource(R.drawable.w_border);
//                    }else{
//                        image.setBackgroundResource(R.drawable.d_border);
//                    }
//                    Display display = activity.getWindowManager().getDefaultDisplay();
//                    //int width = display.getWidth(); // ((display.getWidth()*20)/100)
//                    int height = 0;
//                    if (tag.contains("x64")){
//                        height = ((display.getHeight() * 10) / 100);// ((display.getHeight()*30)/100)
//                    }else {
//                        height = ((display.getHeight() * 25) / 100);// ((display.getHeight()*30)/100)
//                    }
//                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
//                    image.setLayoutParams(parms);
//
//                    image.setOnClickListener(new OnClickListener() {
//                        @SuppressLint("InlinedApi")
//                        @Override
//                        public void onClick(View view) {
//                            try {
//                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tag2));
//                                context.startActivity(myIntent);
//                            } catch (ActivityNotFoundException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    });
//
//
//                    final TextView text = new TextView(context);
//                    final int finalI = i;
//                    //Обработчик нажатия на название спойлера
//                    ImageGetter imgGetter3 = new ImageGetter() {
//
//                        public Drawable getDrawable(String source) {
//                            if (source.contains("missing_logo")){
//                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                            }
//                            if (!source.contains("http")){
//                                source = Constants.SERVER + source;
//                            }
//                            if (mDrawableCache.containsKey(source))
//                                //return mDrawableCache.get(source);
//                                return mDrawableCache.get(source).get();
//                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
//                            return null;
//                        }
//                    };
//                    text.setText(Html.fromHtml(body_elements[i], imgGetter3, null));
//                    if (Functions.preference.theme.equals("ligth")){
//                        text.setTextColor(context.getResources().getColor(R.color.black));
//                    }else{
//                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//                    }
//                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
//                    if(is_link) {
//                        text.setMovementMethod(LinkMovementMethod.getInstance());
//                    }
//
//                    Ion.with(image)
//                            //.placeholder(R.drawable.ic_launcher)
//                            .animateLoad(R.anim.spin_animation)
//                            .error(R.drawable.missing_preview)
//                            .load(tag);
//                    //ImageLoader.getInstance().displayImage(tag.substring(3, tag.length()-1), image);
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(image);
//                    spoiler_layout.get(spoiler_layout.size()-1).addView(text);
//                }else{
//                    //Просто вставляем текстовое поле
//                    final TextView text = new TextView(context);
//                    final int finalI = i;
//                    //Обработчик нажатия на название спойлера
//                    ImageGetter imgGetter4 = new ImageGetter() {
//
//                        public Drawable getDrawable(String source) {
//                            if (source.contains("missing_logo")){
//                                source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                            }
//                            if (!source.contains("http")){
//                                source = Constants.SERVER + source;
//                            }
//                            if (mDrawableCache.containsKey(source))
//                                //return mDrawableCache.get(source);
//                                return mDrawableCache.get(source).get();
//                            new ImageDownloadAsyncTask2(source, body_elements[finalI], text,  context).execute();
//                            return null;
//                        }
//                    };
//                    text.setText(Html.fromHtml(body_elements[i], imgGetter4, null));
//                    text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//                    if (Functions.preference.theme.equals("ligth")){
//                        text.setTextColor(context.getResources().getColor(R.color.black));
//                    }else{
//                        text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//                    }
//                    text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
//                    if(is_link) {
//                        text.setMovementMethod(LinkMovementMethod.getInstance());
//                    }
//                    layout.addView(text);
//                }
//
//            }
//        }else{
//            final TextView text = new TextView(context);
//            //Обработчик нажатия на название спойлера
//            final String finalBody = body;
//            ImageGetter imgGetter5 = new ImageGetter() {
//
//                public Drawable getDrawable(String source) {
//                    if (source.contains("missing_logo")){
//                        source = Constants.SERVER + "/assets/globals/missing_original.jpg";
//                    }
//                    if (!source.contains("http")){
//                        source = Constants.SERVER + source;
//                    }
//                    if (mDrawableCache.containsKey(source))
//                        //return mDrawableCache.get(source);
//                        return mDrawableCache.get(source).get();
//                    new ImageDownloadAsyncTask2(source, finalBody, text,  context).execute();
//                    return null;
//                }
//            };
//            text.setText(Html.fromHtml(body, imgGetter5, null), TextView.BufferType.SPANNABLE);
//            if (Functions.preference.theme.equals("ligth")){
//                text.setTextColor(context.getResources().getColor(R.color.black));
//            }else{
//                text.setTextColor(context.getResources().getColor(R.color.gray_ligth));
//            }
//            text.setLinkTextColor(context.getResources().getColor(R.color.blue_shiki));
//            if(is_link) {
//                text.setMovementMethod(LinkMovementMethod.getInstance());
//            }
//            layout.addView(text);
//        }
//
//
//
//    }
//
//}
