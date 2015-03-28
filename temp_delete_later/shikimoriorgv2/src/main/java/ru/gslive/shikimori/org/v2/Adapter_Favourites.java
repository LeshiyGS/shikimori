package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_Favourites extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 7;
    protected static final String[] CONTENT = new String[] { "Аниме", "Манга", "Персонажи", "Сейю", "Мангаки", "Продюсеры", "Люди"};
 
    public Adapter_Favourites(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_Favourites.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
            Fragment_FAnimes fragmenttab1 = new Fragment_FAnimes();
            return fragmenttab1;

        // Open FragmentTab2.java
        case 1:
            Fragment_FMangas fragmenttab2 = new Fragment_FMangas();
            return fragmenttab2;
        
	     // Open FragmentTab2.java
	    case 2:
	        Fragment_FCharacters fragmenttab3 = new Fragment_FCharacters();
	        return fragmenttab3;
	    
		 // Open FragmentTab2.java
		case 3:
		    Fragment_FSeyu fragmenttab4 = new Fragment_FSeyu();
		    return fragmenttab4;
		
		//Open FragmentTab2.java
		case 4:
		    Fragment_FMangakas fragmenttab5 = new Fragment_FMangakas();
		    return fragmenttab5;
		
		//Open FragmentTab2.java
		case 5:
		    Fragment_FProducers fragmenttab6 = new Fragment_FProducers();
		    return fragmenttab6;
		
		//Open FragmentTab2.java
		case 6:
		    Fragment_FPeople fragmenttab7 = new Fragment_FPeople();
		    return fragmenttab7;
		}
        return null;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }
 
}