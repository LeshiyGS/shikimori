package org.shikimori.library.tool.imagetool;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Author: Artemiy Garin
 * Date: 03.09.13
 */
public class ThumbPagerAdapter extends PagerAdapter {

    private List<View> views;

    public ThumbPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void removeView(ViewPager viewPager, View view) {
        viewPager.setAdapter(null);
        views.remove(view);
        viewPager.setAdapter(this);
    }

    public void addView(ViewPager viewPager, View view) {
        viewPager.setAdapter(null);
        views.add(view);
        viewPager.setOffscreenPageLimit(views.size());
        viewPager.setAdapter(this);
    }
}