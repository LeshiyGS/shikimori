package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_Club extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 3;
    protected static final String[] CONTENT = new String[] { "Моё меню", "Описание", "Обсуждение"};
 
    public Adapter_Club(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_Club.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
        case 0:
        	Fragment_Club_Menu fragmenttab1 = new Fragment_Club_Menu();
            return fragmenttab1;
        case 1:
        	Fragment_Club_Info fragmenttab2 = new Fragment_Club_Info();
            return fragmenttab2;
	    case 2:
	    	Fragment_Club_Comments fragmenttab3 = new Fragment_Club_Comments();
	        return fragmenttab3;
		}
        return null;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }
 
}