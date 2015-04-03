package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.adapters.FragmentPageAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.interfaces.PageNextlistener;
import org.shikimori.library.tool.h;

import java.util.List;

import ru.altarix.ui.ExSlidingTabLayout;

/**
 * Wrapper pages wizard
 * Created by Владимир on 15.07.2014.
 */
public class PagerAdapterFragment extends BaseFragment<BaseActivity> implements PageNextlistener, ViewPager.OnPageChangeListener {

    /**
     * animate page scrolling
     */
    private ViewPager pager;
    /**
     * page adapter
     */
    private FragmentPageAdapter pageAdapter;
    /**
     * Indicator pages
     */
    private ExSlidingTabLayout pagerStrip;
    /**
     * List of pages
     */
    private List<Fragment> pages;
    /**
     * Current page
     */
    private String[] titles;

    public static PagerAdapterFragment newInstance(List<Fragment> wizardPages, String ... titles){
        PagerAdapterFragment frag = new PagerAdapterFragment();
        frag.setPages(wizardPages);
        frag.setTitles(titles);
        return frag;
    }

    private void setTitles(String[] titles) {
        this.titles = titles;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setPages(List<Fragment> pages) {
        this.pages = pages;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_pager_fragment, null);
        pagerStrip = (ExSlidingTabLayout)v.findViewById(R.id.pagerStrip);
        pager = (ViewPager) v.findViewById(R.id.viewPager);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pageAdapter = new FragmentPageAdapter(activity.getSupportFragmentManager(), pages);
        pager.setAdapter(pageAdapter);
        int colorId = h.getAttributeResourceId(activity, R.attr.altarixUiAttrSelectedColorTab);
        int colorLabelId = h.getAttributeResourceId(activity, R.attr.altarixUiAttrLabelColor);
        int colorIndicator = h.getAttributeResourceId(activity, R.attr.altarixUiAttrDividerColor);
        // цвет теста табов
        int clr = activity.getResources().getColor(colorId);
        int clrNotSelected = activity.getResources().getColor(colorLabelId);
        int clrIndicator = activity.getResources().getColor(colorIndicator);
        // set colors
        pagerStrip.setSelectedIndicatorColors(clrIndicator);
        pagerStrip.setTextColors(clr,clrNotSelected);

        pagerStrip.setTitles(titles);
        pagerStrip.setViewPager(pager);
    }

    /**
     * When user click button to move next page
     * @param page
     */
    @Override
    public void moveToNextPage(int page) {
        if(pages.size() < page)
            pager.setCurrentItem(page+1);
    }

    @Override
    public void moveToPage(int page){
        pager.setCurrentItem(page);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(activity == null || activity.isFinishing())
            return;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction fr = manager.beginTransaction();
        for (Fragment frag : pages)
            fr.remove(frag);
        fr.commitAllowingStateLoss();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
       // set indicator page
        pagerStrip.setCurrentItem(position, true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
