package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_UserStatistic extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Аниме", "Манга"};
 
    public Adapter_UserStatistic(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_UserStatistic.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
        case 0:
        	Fragment_UserStatistic_Anime fragmenttab1 = new Fragment_UserStatistic_Anime();
            return fragmenttab1;
        case 1:
        	Fragment_UserStatistic_Manga fragmenttab2 = new Fragment_UserStatistic_Manga();
            return fragmenttab2;
		}
        return null;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }
 
}