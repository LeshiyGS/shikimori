package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_Community extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Пользователи", "Клубы"};
 
    public Adapter_Community(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_Community.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
        	Fragment_Community_User fragmenttab2 = new Fragment_Community_User();
        	return fragmenttab2;

        // Open FragmentTab2.java
        case 1:
        	Fragment_Community_Club fragmenttab1 = new Fragment_Community_Club();
            return fragmenttab1;
           
        }
		return null;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }
 
}