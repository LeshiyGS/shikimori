package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_Animes extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Список", "Фильтр"};
 
    public Adapter_Animes(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_Animes.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
        	Fragment_Animes fragmenttab2 = new Fragment_Animes();
        	return fragmenttab2;

        // Open FragmentTab2.java
        case 1:
        	Fragment_AnimesFilter fragmenttab1 = new Fragment_AnimesFilter();
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