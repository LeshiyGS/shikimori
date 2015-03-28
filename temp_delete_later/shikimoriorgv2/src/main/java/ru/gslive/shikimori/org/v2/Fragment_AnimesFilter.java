package ru.gslive.shikimori.org.v2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
 
public class Fragment_AnimesFilter extends SherlockFragment{
 
	static View_GridView gv_status, gv_type, gv_season, gv_sort, gv_episode, gv_rating, gv_mylist, gv_genre;
	
	String fstatus = "";
	String ftype = "";
	String fseason = "";
	String forder_by = "";
	String fduration = "";
	String frating = "";
	String fmylist = "";
	String fgenre = "";
	View view;
	
	boolean[] list = new boolean[]{false, false, false, false, false, false, false, false};
	
	String[] status = new String[]{"Анонсированно","Онгоинг","Недавно вышедшее","Вышедшее"};
	String[] status_value = new String[]{"planned,","ongoing,","latest,","released,"};
	
	String[] type = new String[]{"Сериал","Полнометражное","OVA","ONA", "Спешиал", "Клип"};
	String[] type_value = new String[]{"TV,","Movie,","OVA,","ONA,","Special,","Music,"};
	
	String[] season 		= new String[]{"Лето 2014","Весна 2014","Зима 2014","Осень 2013", "2013 год", "2012 год", "2010-2011","2005-2009","2000-2004","90е годы","80е годы","Более старые"};
	String[] season_value 	= new String[]{"summer_2014,","spring_2014,","winter_2014,","fall_2013,","2013,","2012,","2010_2011,","2005_2009,","2000_2004,","199x,","198x,","ancient,"};
	
	String[] order_by 		= new String[]{"По рейтингу","По популярности","По алфавиту","По дате выхода", "По дате добавления"};
	String[] order_by_value = new String[]{"ranked","popularity","name","aired_on","id"};
	
	String[] duration 		= new String[]{"До 10 минут","До 30 минут","Более 30 минут"};
	String[] duration_value = new String[]{"S,","D,","F,"};
	
	String[] rating 		= new String[]{"G","PG","PG-13","R","NC-17"};
	String[] rating_value 	= new String[]{",G,",",PG,","PG-13,","R,","NC-17,"};
	
	String[] mylist 		= new String[]{"Запланировано","Смотрю","Просмотрено","Отложено", "Брошено"};
	String[] mylist_value 	= new String[]{"0,","1,","2,","3,","4,"};
	
	String[] genre 			= new String[]{"Сёнен","Сёнен Ай","Сейнен","Сёдзе","Сёдзе Ай","Дзёсей","Комедия",
			"Романтика","Школа","Безумие","Боевые искусства","Вампиры","Военное","Гарем","Демоны","Детское",
			"Драма","Игры","Исторический","Космос","Магия","Машины","Меха","Мистика","Музыка","Пародия",
			"Повседневность","Полиция","Приключения","Психологическое","Самураи","Сверхъестественное","Смена пола",
			"Спорт","Супер сила","Ужасы","Фантастика","Фэнтези","Экшен","Этти","Триллер","Хентай","Яой","Юри"};
	String[] genre_value 	= new String[]{"27-Shounen,","28-Shounen-Ai,","42-Seinen,","25-Shoujo,","26-Shoujo-Ai,","43-Josei,","4-Comedy",
			"22-Romance,","23-School,","5-Dementia,","17-Martial-Arts,","32-Vampire,","38-Military,","35-Harem,","6-Demons,","15-Kids,",
			"8-Drama,","11-Game,","13-Historical,","29-Space,","16-Magic,","3-Cars,","18-Mecha,","7-Mystery,","19-Music,","20-Parody,",
			"36-Slice-of-Life,","39-Police,","2-Adventure,","40-Psychological,","21-Samurai,","37-Supernatural,","44-Gender-Bender,",
			"30-Sports,","31-Super-Power,","14-Horror,","24-Sci-Fi,","10-Fantasy,","1-Action,","9-Ecchi,","41-Thriller,","12-Hentai,","33-Yaoi,","34-Yuri,"};
	
	
	StatusAdapter adapter_status;
	TypeAdapter adapter_type;
	SeasonAdapter adapter_season;
	OrderAdapter adapter_sort;
	DurationAdapter adapter_duration;
	RatingAdapter adapter_rating;
	MylistAdapter adapter_mylist;
	GenreAdapter adapter_genre;
	
	//Адаптер status
	public class StatusAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public StatusAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		
		@Override
		public int getCount() {
			return status.length;
		}
				
		@Override
		public Object getItem(int position) {
			return status[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (convertView == null) {
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
				}
						
				holder = new Functions.ViewHolder();
				holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
				holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
			    		
				convertView.setTag(holder);	
			}else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}
						
			final int pos = position;
			convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.checkView.isChecked()){
						fstatus += status_value[pos];
						holder.onoffView.setVisibility(View.VISIBLE);
						holder.onoffView.setChecked(true);
					}else{
						fstatus = fstatus.replaceAll("!"+status_value[pos], "");
						fstatus = fstatus.replaceAll(status_value[pos], "");
						holder.onoffView.setVisibility(View.GONE);
						holder.onoffView.setChecked(false);
						adapter_status.notifyDataSetChanged();
					}
				}
			});
		    		
			convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (holder.onoffView.isChecked()){
						fstatus = fstatus.replaceAll("!"+status_value[pos], status_value[pos]);
					}else{
						fstatus = fstatus.replaceAll(status_value[pos], "!"+status_value[pos]);
					}
				}
			});
			
			holder.checkView.setText(status[position]);
			holder.onoffView.setVisibility(View.GONE);
			
			if (fstatus.contains(status_value[position])){
				holder.checkView.setChecked(true);
				holder.onoffView.setVisibility(View.VISIBLE);
				if (fstatus.contains("!"+status_value[position])){
					holder.onoffView.setChecked(false);
				}else{
					holder.onoffView.setChecked(true);
				}
			}else{
				holder.checkView.setChecked(false);
			}		    		
			
			return convertView;
		}
	}
	
	//Адаптер type
	public class TypeAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public TypeAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
			
		@Override
		public int getCount() {
			return type.length;
		}
					
		@Override
		public Object getItem(int position) {
			return type[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
				
			if (convertView == null) {
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
				}			
				holder = new Functions.ViewHolder();
				holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
				holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
				    		
				convertView.setTag(holder);	
			}else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}
							
			final int pos = position;
			convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.checkView.isChecked()){
						ftype += type_value[pos];
							holder.onoffView.setVisibility(View.VISIBLE);
							holder.onoffView.setChecked(true);
						}else{
							ftype = ftype.replaceAll("!"+type_value[pos], "");
							ftype = ftype.replaceAll(type_value[pos], "");
							holder.onoffView.setVisibility(View.GONE);
							holder.onoffView.setChecked(false);
							adapter_status.notifyDataSetChanged();
						}
					}
				});
			    		
			convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
					
				@Override
				public void onClick(View v) {
					if (holder.onoffView.isChecked()){
						ftype = ftype.replaceAll("!"+type_value[pos], type_value[pos]);
					}else{
						ftype = ftype.replaceAll(type_value[pos], "!"+type_value[pos]);
					}
				}
			});
				
			holder.checkView.setText(type[position]);
			holder.onoffView.setVisibility(View.GONE);
				
			if (ftype.contains(type_value[position])){
				holder.checkView.setChecked(true);
				holder.onoffView.setVisibility(View.VISIBLE);
				if (ftype.contains("!"+type_value[position])){
					holder.onoffView.setChecked(false);
				}else{
					holder.onoffView.setChecked(true);
				}
			}else{
				holder.checkView.setChecked(false);
			}		    		
				
			return convertView;
		}
	}	
	
	//Адаптер season
	public class SeasonAdapter extends BaseAdapter {
			private LayoutInflater mLayoutInflater;

			public SeasonAdapter (Context ctx) {  
				mLayoutInflater = LayoutInflater.from(ctx);  
			}
				
			@Override
			public int getCount() {
				return season.length;
			}
						
			@Override
			public Object getItem(int position) {
				return season[position];
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}
				
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final Functions.ViewHolder holder;
					
				if (convertView == null) {
					if (Functions.preference.theme.equals("ligth")){
						convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
					}else{
						convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
					}			
					holder = new Functions.ViewHolder();
					holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
					holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
					    		
					convertView.setTag(holder);	
				}else {
					holder = (Functions.ViewHolder) convertView.getTag();
				}
								
				final int pos = position;
				convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (holder.checkView.isChecked()){
							fseason += season_value[pos];
								holder.onoffView.setVisibility(View.VISIBLE);
								holder.onoffView.setChecked(true);
							}else{
								fseason = fseason.replaceAll("!"+season_value[pos], "");
								fseason = fseason.replaceAll(season_value[pos], "");
								holder.onoffView.setVisibility(View.GONE);
								holder.onoffView.setChecked(false);
								adapter_season.notifyDataSetChanged();
							}
						}
					});
				    		
				convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
						
					@Override
					public void onClick(View v) {
						if (holder.onoffView.isChecked()){
							fseason = fseason.replaceAll("!"+season_value[pos], season_value[pos]);
						}else{
							fseason = fseason.replaceAll(season_value[pos], "!"+season_value[pos]);
						}
					}
				});
					
				holder.checkView.setText(season[position]);
				holder.onoffView.setVisibility(View.GONE);
					
				if (fseason.contains(season_value[position])){
					holder.checkView.setChecked(true);
					holder.onoffView.setVisibility(View.VISIBLE);
					if (fseason.contains("!"+season_value[position])){
						holder.onoffView.setChecked(false);
					}else{
						holder.onoffView.setChecked(true);
					}
				}else{
					holder.checkView.setChecked(false);
				}		    		
					
				return convertView;
			}
		}	
		
	//Адаптер order
	public class OrderAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public OrderAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
					
		@Override
		public int getCount() {
			return order_by.length;
		}
							
		@Override
		public Object getItem(int position) {
			return order_by[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
					
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (convertView == null) {
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_optionbox_filter, null);
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_optionbox_filter, null);
				}
				
									
						holder = new Functions.ViewHolder();
						holder.optionView = (RadioButton) convertView.findViewById(R.id.rb_item);
						    		
						convertView.setTag(holder);	
					}else {
						holder = (Functions.ViewHolder) convertView.getTag();
					}
									
					final int pos = position;
					convertView.findViewById(R.id.rb_item).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (holder.optionView.isChecked()){
								forder_by = order_by_value[pos];
							}
							adapter_sort.notifyDataSetChanged();
						}});
					    		
						
					holder.optionView.setText(order_by[position]);
						
					if (forder_by.equals(order_by_value[position])){
						holder.optionView.setChecked(true);
					}else{
						holder.optionView.setChecked(false);
					}
						
					return convertView;
				}
			}	
		
	//Адаптер duration
	public class DurationAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public DurationAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
					
		@Override
		public int getCount() {
			return duration.length;
		}
		
		@Override
		public Object getItem(int position) {
			return duration[position];
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (convertView == null) {
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
				}					
				holder = new Functions.ViewHolder();
				holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
				holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
				
				convertView.setTag(holder);	
			}else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}
			
			final int pos = position;
			convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.checkView.isChecked()){
						fduration += duration_value[pos];
						holder.onoffView.setVisibility(View.VISIBLE);
						holder.onoffView.setChecked(true);
					}else{
						fduration = fduration.replaceAll("!"+duration_value[pos], "");
						fduration = fduration.replaceAll(duration_value[pos], "");
						holder.onoffView.setVisibility(View.GONE);
						holder.onoffView.setChecked(false);
						adapter_duration.notifyDataSetChanged();
					}
				}
			});
			
			convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (holder.onoffView.isChecked()){
						fduration = fduration.replaceAll("!"+duration_value[pos], duration_value[pos]);
					}else{
						fduration = fduration.replaceAll(duration_value[pos], "!"+duration_value[pos]);
					}
				}
			});
			
			holder.checkView.setText(duration[position]);
			holder.onoffView.setVisibility(View.GONE);
			
			if (fduration.contains(duration_value[position])){
				holder.checkView.setChecked(true);
				holder.onoffView.setVisibility(View.VISIBLE);
				if (fduration.contains("!"+duration_value[position])){
					holder.onoffView.setChecked(false);
				}else{
					holder.onoffView.setChecked(true);
				}
			}else{
				holder.checkView.setChecked(false);
			}		    		
			
			return convertView;
		}
	}	
	
	//Адаптер rating
	public class RatingAdapter extends BaseAdapter {
				private LayoutInflater mLayoutInflater;

				public RatingAdapter (Context ctx) {  
					mLayoutInflater = LayoutInflater.from(ctx);  
				}
					
				@Override
				public int getCount() {
					return rating.length;
				}
							
				@Override
				public Object getItem(int position) {
					return rating[position];
				}

				@Override
				public long getItemId(int position) {
					return 0;
				}
					
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final Functions.ViewHolder holder;
						
					if (convertView == null) {
						if (Functions.preference.theme.equals("ligth")){
							convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
						}else{
							convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
						}			
						holder = new Functions.ViewHolder();
						holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
						holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
						    		
						convertView.setTag(holder);	
					}else {
						holder = (Functions.ViewHolder) convertView.getTag();
					}
									
					final int pos = position;
					convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (holder.checkView.isChecked()){
								frating += rating_value[pos];
									holder.onoffView.setVisibility(View.VISIBLE);
									holder.onoffView.setChecked(true);
								}else{
									frating = frating.replaceAll("!"+rating_value[pos], "");
									frating = frating.replaceAll(rating_value[pos], "");
									holder.onoffView.setVisibility(View.GONE);
									holder.onoffView.setChecked(false);
									adapter_rating.notifyDataSetChanged();
								}
							}
						});
					    		
					convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
							
						@Override
						public void onClick(View v) {
							if (holder.onoffView.isChecked()){
								frating = frating.replaceAll("!"+rating_value[pos], rating_value[pos]);
							}else{
								frating = frating.replaceAll(rating_value[pos], "!"+rating_value[pos]);
							}
						}
					});
						
					holder.checkView.setText(rating[position]);
					holder.onoffView.setVisibility(View.GONE);
						
					if (frating.contains(rating_value[position])){
						holder.checkView.setChecked(true);
						holder.onoffView.setVisibility(View.VISIBLE);
						if (frating.contains("!"+rating_value[position])){
							holder.onoffView.setChecked(false);
						}else{
							holder.onoffView.setChecked(true);
						}
					}else{
						holder.checkView.setChecked(false);
					}		    		
						
					return convertView;
				}
			}	
		
	//Адаптер mylist
	public class MylistAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public MylistAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
					
		@Override
		public int getCount() {
			return mylist.length;
		}
							
		@Override
		public Object getItem(int position) {
			return mylist[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
					
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Functions.ViewHolder holder;
			
			if (convertView == null) {
				if (Functions.preference.theme.equals("ligth")){
					convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
				}else{
					convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
				}
				holder = new Functions.ViewHolder();
				holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
				holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
				
				convertView.setTag(holder);	
			}else {
				holder = (Functions.ViewHolder) convertView.getTag();
			}
			
			final int pos = position;
			convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (holder.checkView.isChecked()){
						fmylist += mylist_value[pos];
									holder.onoffView.setVisibility(View.VISIBLE);
									holder.onoffView.setChecked(true);
								}else{
									fmylist = fmylist.replaceAll("!"+mylist_value[pos], "");
									fmylist = fmylist.replaceAll(mylist_value[pos], "");
									holder.onoffView.setVisibility(View.GONE);
									holder.onoffView.setChecked(false);
									adapter_mylist.notifyDataSetChanged();
								}
							}
						});
					    		
					convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
							
						@Override
						public void onClick(View v) {
							if (holder.onoffView.isChecked()){
								fmylist = fmylist.replaceAll("!"+mylist_value[pos], mylist_value[pos]);
							}else{
								fmylist = fmylist.replaceAll(mylist_value[pos], "!"+mylist_value[pos]);
							}
						}
					});
						
					holder.checkView.setText(mylist[position]);
					holder.onoffView.setVisibility(View.GONE);
						
					if (fmylist.contains(mylist_value[position])){
						holder.checkView.setChecked(true);
						holder.onoffView.setVisibility(View.VISIBLE);
						if (fmylist.contains("!"+mylist_value[position])){
							holder.onoffView.setChecked(false);
						}else{
							holder.onoffView.setChecked(true);
						}
					}else{
						holder.checkView.setChecked(false);
					}	    		
						
					return convertView;
				}
			}	
		
	//Адаптер genre
	public class GenreAdapter extends BaseAdapter {
		private LayoutInflater mLayoutInflater;

		public GenreAdapter (Context ctx) {  
			mLayoutInflater = LayoutInflater.from(ctx);  
		}
		
		@Override
		public int getCount() {
			return genre.length;
		}
			
				@Override
				public Object getItem(int position) {
					return genre[position];
				}

				@Override
				public long getItemId(int position) {
					return 0;
				}
					
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					final Functions.ViewHolder holder;
						
					if (convertView == null) {
						
						if (Functions.preference.theme.equals("ligth")){
							convertView = mLayoutInflater.inflate(R.layout.w_item_checkbox_filter, null);   
						}else{
							convertView = mLayoutInflater.inflate(R.layout.d_item_checkbox_filter, null); 
						}
									
						holder = new Functions.ViewHolder();
						holder.checkView = (CheckBox) convertView.findViewById(R.id.cb_item);
						holder.onoffView = (ToggleButton) convertView.findViewById(R.id.tb_on_off);
						    		
						convertView.setTag(holder);	
					}else {
						holder = (Functions.ViewHolder) convertView.getTag();
					}
									
					final int pos = position;
					convertView.findViewById(R.id.cb_item).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (holder.checkView.isChecked()){
								fgenre += genre_value[pos];
									holder.onoffView.setVisibility(View.VISIBLE);
									holder.onoffView.setChecked(true);
								}else{
									fgenre = fgenre.replaceAll("!"+genre_value[pos], "");
									fgenre = fgenre.replaceAll(genre_value[pos], "");
									holder.onoffView.setVisibility(View.GONE);
									holder.onoffView.setChecked(false);
									adapter_genre.notifyDataSetChanged();
								}
							}
						});
					    		
					convertView.findViewById(R.id.tb_on_off).setOnClickListener(new OnClickListener() {
							
						@Override
						public void onClick(View v) {
							if (holder.onoffView.isChecked()){
								fgenre = fgenre.replaceAll("!"+genre_value[pos], genre_value[pos]);
							}else{
								fgenre = fgenre.replaceAll(genre_value[pos], "!"+genre_value[pos]);
							}
						}
					});
						
					holder.checkView.setText(genre[position]);
					holder.onoffView.setVisibility(View.GONE);
						
					if (fgenre.contains(genre_value[position])){
						holder.checkView.setChecked(true);
						holder.onoffView.setVisibility(View.VISIBLE);
						if (fgenre.contains("!"+genre_value[position])){
							holder.onoffView.setChecked(false);
						}else{
							holder.onoffView.setChecked(true);
						}
					}else{
						holder.checkView.setChecked(false);
					}
						
					return convertView;
				}
			}	
		
	//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	if (Functions.preference.theme.equals("ligth")){
    		view = inflater.inflate(R.layout.w_fragment_animesfilter, container, false);    
		}else{
			view = inflater.inflate(R.layout.d_fragment_animesfilter, container, false);     
		}

        gv_status = (View_GridView) view.findViewById(R.id.gv_status);
        gv_type = (View_GridView) view.findViewById(R.id.gv_type);
        gv_season = (View_GridView) view.findViewById(R.id.gv_season);
        gv_sort = (View_GridView) view.findViewById(R.id.gv_sort);
        gv_episode = (View_GridView) view.findViewById(R.id.gv_episode);
        gv_rating = (View_GridView) view.findViewById(R.id.gv_rating);
        gv_mylist = (View_GridView) view.findViewById(R.id.gv_mylist);
        gv_genre = (View_GridView) view.findViewById(R.id.gv_genre);
        
        Button btn_clear = (Button) view.findViewById(R.id.btn_filter_clear);
        btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				fstatus = "";
				ftype = "";
				fseason = "";
				forder_by = "";
				fduration = "";
				frating = "";
				fmylist = "";
				fgenre = "";
				Activity_Animes.fstatus = "";
				Activity_Animes.ftype = "";
				Activity_Animes.fseason = "";
				Activity_Animes.forder_by = "";
				Activity_Animes.fduration = "";
				Activity_Animes.frating = "";
				Activity_Animes.fmylist = "";
				Activity_Animes.fgenre = "";
				adapter_status.notifyDataSetChanged();
				adapter_type.notifyDataSetChanged();
				adapter_season.notifyDataSetChanged();
				adapter_sort.notifyDataSetChanged();
				adapter_duration.notifyDataSetChanged();
				adapter_rating.notifyDataSetChanged();
				adapter_mylist.notifyDataSetChanged();
				adapter_genre.notifyDataSetChanged();
				Fragment_Animes.al.execute();
			}
        });
        
        Button btn_apply = (Button) view.findViewById(R.id.btn_filter_apply);
        btn_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Activity_Animes.fstatus = fstatus;
				Activity_Animes.ftype = ftype;
				Activity_Animes.fseason = fseason;
				Activity_Animes.forder_by = forder_by;
				Activity_Animes.fduration = fduration;
				Activity_Animes.frating = frating;
				Activity_Animes.fmylist = fmylist;
				Activity_Animes.fgenre = fgenre;
				Fragment_Animes.al.execute();
				Activity_Animes.titleIndicator.setCurrentItem(0);
			}
        });
        
        TextView btn_status = (TextView) view.findViewById(R.id.btn_status);
        btn_status.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_status.getVisibility() == View.VISIBLE){
					gv_status.setVisibility(View.GONE);
					list[0] = false;
				}else{
					gv_status.setVisibility(View.VISIBLE);
					list[0] = true;
				}
			}
        });
        
        TextView btn_type = (TextView) view.findViewById(R.id.btn_type);
        btn_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_type.getVisibility() == View.VISIBLE){
					gv_type.setVisibility(View.GONE);
					list[1] = false;
				}else{
					gv_type.setVisibility(View.VISIBLE);
					list[1] = true;
				}
			}
        });
        
        TextView btn_season = (TextView) view.findViewById(R.id.btn_season);
        btn_season.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_season.getVisibility() == View.VISIBLE){
					gv_season.setVisibility(View.GONE);
					list[2] = false;
				}else{
					gv_season.setVisibility(View.VISIBLE);
					list[2] = true;
				}
			}
        });
        
        TextView btn_sort = (TextView) view.findViewById(R.id.btn_sort);
        btn_sort.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_sort.getVisibility() == View.VISIBLE){
					gv_sort.setVisibility(View.GONE);
					list[3] = false;
				}else{
					gv_sort.setVisibility(View.VISIBLE);
					list[3] = true;
				}
			}
        });
        
        TextView btn_episode = (TextView) view.findViewById(R.id.btn_episode);
        btn_episode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_episode.getVisibility() == View.VISIBLE){
					gv_episode.setVisibility(View.GONE);
					list[4] = false;
				}else{
					gv_episode.setVisibility(View.VISIBLE);
					list[4] = true;
				}
			}
        });
        
        TextView btn_rating = (TextView) view.findViewById(R.id.tv_rating);
        btn_rating.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_rating.getVisibility() == View.VISIBLE){
					gv_rating.setVisibility(View.GONE);
					list[5] = false;
				}else{
					gv_rating.setVisibility(View.VISIBLE);
					list[5] = true;
				}
			}
        });
        
        TextView btn_mylist = (TextView) view.findViewById(R.id.btn_mylist);
        btn_mylist.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_mylist.getVisibility() == View.VISIBLE){
					gv_mylist.setVisibility(View.GONE);
					list[6] = false;
				}else{
					gv_mylist.setVisibility(View.VISIBLE);
					list[6] = true;
				}
			}
        });
        
        TextView btn_genre = (TextView) view.findViewById(R.id.btn_genre);
        btn_genre.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (gv_genre.getVisibility() == View.VISIBLE){
					gv_genre.setVisibility(View.GONE);
					list[7] = false;
				}else{
					gv_genre.setVisibility(View.VISIBLE);
					list[7] = true;
				}
			}
        });

        gv_status.setExpanded(true);
        adapter_status = new StatusAdapter(getActivity());
        gv_status.setAdapter(adapter_status);
        //
        gv_type.setExpanded(true);
        adapter_type = new TypeAdapter(getActivity());
        gv_type.setAdapter(adapter_type);
        //
        gv_season.setExpanded(true);
        adapter_season = new SeasonAdapter(getActivity());
        gv_season.setAdapter(adapter_season);
        //
        gv_sort.setExpanded(true);
        adapter_sort = new OrderAdapter(getActivity());
        gv_sort.setAdapter(adapter_sort);
        //
        gv_episode.setExpanded(true);
        adapter_duration = new DurationAdapter(getActivity());
        gv_episode.setAdapter(adapter_duration);
        //
        gv_rating.setExpanded(true);
        adapter_rating = new RatingAdapter(getActivity());
        gv_rating.setAdapter(adapter_rating);
        //
        gv_mylist.setExpanded(true);
        adapter_mylist = new MylistAdapter(getActivity());
        gv_mylist.setAdapter(adapter_mylist);
        //
        gv_genre.setExpanded(true);
        adapter_genre = new GenreAdapter(getActivity());
        gv_genre.setAdapter(adapter_genre);

        if ((savedInstanceState != null)) {
        	list = savedInstanceState.getBooleanArray("list");
        	fstatus = savedInstanceState.getString("fstatus");
			ftype = savedInstanceState.getString("ftype");
			fseason = savedInstanceState.getString("fseason");
			forder_by = savedInstanceState.getString("forder_by");
			fduration = savedInstanceState.getString("fduration");
			frating = savedInstanceState.getString("frating");
			fmylist = savedInstanceState.getString("fmylist");
			fgenre = savedInstanceState.getString("fgenre");
        	
        	
        	if (list[0]){
        		gv_status.setVisibility(View.VISIBLE);
        	}
        	if (list[1]){
        		gv_type.setVisibility(View.VISIBLE);
        	}
        	if (list[2]){
        		gv_season.setVisibility(View.VISIBLE);
        	}
        	if (list[3]){
        		gv_sort.setVisibility(View.VISIBLE);
        	}
        	if (list[4]){
        		gv_episode.setVisibility(View.VISIBLE);
        	}
        	if (list[5]){
        		gv_rating.setVisibility(View.VISIBLE);
        	}
        	if (list[6]){
        		gv_mylist.setVisibility(View.VISIBLE);
        	}
        	if (list[7]){
        		gv_genre.setVisibility(View.VISIBLE);
        	}
		}
        return view;
        
        
    }
      
    @Override
	public void onResume() {
    	Activity_Animes.fstatus = fstatus;
		Activity_Animes.ftype = ftype;
		Activity_Animes.fseason = fseason;
		Activity_Animes.forder_by = forder_by;
		Activity_Animes.fduration = fduration;
		Activity_Animes.frating = frating;
		Activity_Animes.fmylist = fmylist;
		Activity_Animes.fgenre = fgenre;
        super.onResume();
    }
    
	//
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);   
        outState.putBooleanArray("list", list);
        outState.putString("fstatus", fstatus);
        outState.putString("ftype",ftype);
        outState.putString("fseason",fseason);
        outState.putString("forder_by",forder_by);
        outState.putString("fduration",fduration);
        outState.putString("frating",frating);
        outState.putString("fmylist",fmylist);
		outState.putString("fgenre",fgenre);
    }
 
}