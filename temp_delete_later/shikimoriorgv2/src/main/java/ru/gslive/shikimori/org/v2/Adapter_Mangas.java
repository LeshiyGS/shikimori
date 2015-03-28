package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_Mangas extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Список", "Фильтр"};
 
    public Adapter_Mangas(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_Mangas.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
        	Fragment_Mangas fragmenttab2 = new Fragment_Mangas();
        	return fragmenttab2;

        // Open FragmentTab2.java
        case 1:
        	Fragment_MangasFilter fragmenttab1 = new Fragment_MangasFilter();
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