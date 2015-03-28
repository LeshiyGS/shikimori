package ru.gslive.shikimori.org.v2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class Adapter_UserProfile extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 2;
    protected static final String[] CONTENT = new String[] { "Профиль", "Лента", "Настройки"};
 
    public Adapter_UserProfile(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public CharSequence getPageTitle(int position) {
      return Adapter_UserProfile.CONTENT[position % CONTENT.length];
    }
    
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
            Fragment_UserProfile fragmenttab1 = new Fragment_UserProfile();
            return fragmenttab1;
 
        // Open FragmentTab2.java
        case 1:
            Fragment_UserWall fragmenttab2 = new Fragment_UserWall();
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