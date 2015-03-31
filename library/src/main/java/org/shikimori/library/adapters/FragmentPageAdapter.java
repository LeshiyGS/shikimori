package org.shikimori.library.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageAdapter extends
        FragmentPagerAdapter {
    List<Fragment> pagesFrags;

    public FragmentPageAdapter(FragmentManager fragmentManager, List<Fragment> pagesFrags) {
		super(fragmentManager);
        this.pagesFrags = pagesFrags;
    }

    public void setPagesFrags(ArrayList<Fragment> pagesFrags){
        this.pagesFrags = pagesFrags;
    }

    @Override
    public Fragment getItem(int position) {
        return pagesFrags.get(position);
    }

    @Override
    public int getCount() {
        return pagesFrags.size();
    }

}
