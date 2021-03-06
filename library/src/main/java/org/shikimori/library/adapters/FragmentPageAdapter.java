package org.shikimori.library.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentPageAdapter extends
        FragmentStatePagerAdapter {
    List<Fragment> pagesFrags;

    public FragmentPageAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
    }

    public FragmentPageAdapter(FragmentManager fragmentManager, List<Fragment> pagesFrags) {
		super(fragmentManager);
        this.pagesFrags = pagesFrags;
    }

    public void setPagesFrags(ArrayList<Fragment> pagesFrags){
        this.pagesFrags = pagesFrags;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        if(pagesFrags!=null)
            return pagesFrags.get(position);
        return null;
    }

    @Override
    public int getCount() {
        if(pagesFrags!=null)
            return pagesFrags.size();
        return 0;
    }

}
