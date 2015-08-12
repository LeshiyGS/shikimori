package org.shikimori.library.adapters;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.tool.hs;

import java.util.List;

/**
 * Author: Artemiy Garin
 * Date: 03.09.13
 */
public class PhotoSimplePagerAdapter extends PagerAdapter {

    private Activity mContext;
    private List<View> views;
    private float sizeitem;

    public PhotoSimplePagerAdapter(Activity mContext, List<View> views) {
        super();
        this.mContext = mContext;
        this.views = views;
        initSizeElement();
    }

    private void initSizeElement() {
        Point size = hs.getScreenSize(mContext);

        float padding = mContext.getResources().getDimension(R.dimen.defaultPadding);
        float windElement = mContext.getResources().getDimension(R.dimen.defaultPadding);

        sizeitem = 1 / ((size.x - (padding*3))/windElement);
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

    @Override
    public float getPageWidth(int position) {
        return sizeitem > 0 ? sizeitem : (.6f);
    }
}