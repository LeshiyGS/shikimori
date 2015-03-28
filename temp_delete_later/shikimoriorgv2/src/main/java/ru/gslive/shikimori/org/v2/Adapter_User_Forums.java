package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_User_Forums extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Клубы", "Топики"};
 
    public Adapter_User_Forums(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_User_Forums.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
        case 0:
        	Fragment_User_Forum_Clubs fragmenttab1 = new Fragment_User_Forum_Clubs();
            return fragmenttab1;
        case 1:
        	Fragment_User_Forum_Topics fragmenttab2 = new Fragment_User_Forum_Topics();
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