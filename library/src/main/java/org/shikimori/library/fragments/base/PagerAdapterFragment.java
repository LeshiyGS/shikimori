package org.shikimori.library.fragments.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.adapters.FragmentPageAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.interfaces.PageNextlistener;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.tools.pagecontroller.ExTabs;
import ru.altarix.ui.ExSlidingTabLayout;

/**
 * Wrapper pages wizard
 * Created by Владимир on 15.07.2014.
 */
@ExTabs
public class PagerAdapterFragment extends BaseFragment<BaseKitActivity<ShikiAC>> implements PageNextlistener, ViewPager.OnPageChangeListener {

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
    private List<String> titles;

    public static PagerAdapterFragment newInstance(List<Fragment> wizardPages, String ... titles){
        PagerAdapterFragment frag = new PagerAdapterFragment();
        frag.setPages(wizardPages);
        frag.setTitles(new ArrayList<>(Arrays.asList(titles)));
        return frag;
    }

    public static PagerAdapterFragment newInstance(String titleActionBar, List<Fragment> wizardPages, String ... titles){
        PagerAdapterFragment frag = newInstance(wizardPages, titles);
        Bundle b = new Bundle();
        b.putString(Constants.ACTION_BAR_TITLE, titleActionBar);
        frag.setArguments(b);
        return frag;
    }

    protected void setTitles(List<String> titles) {
        this.titles = titles;
    }

    protected void setPages(List<Fragment> pages) {
        this.pages = pages;
    }

    protected void addPage(Fragment frag, String title){
        if(pages == null){
            pages = new ArrayList<>();
            titles = new ArrayList<>();
        }
        pages.add(frag);
        titles.add(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_pager_fragment, container, false);
        pager = (ViewPager) v.findViewById(R.id.viewPager);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pagerStrip = activity.getTabs();
        showPages();
    }

    protected FragmentPageAdapter getPagerAdapter(){
        return new FragmentPageAdapter(activity.getSupportFragmentManager(), pages);
    }

    protected void showPages(){
        if(titles == null || pages == null)
            return;
        buildPages();
    }

    protected void buildPages(){
        pageAdapter = getPagerAdapter();
        pager.setAdapter(pageAdapter);
        if(titles!=null){
            String[] titlesArray = new String[titles.size()];
            titles.toArray(titlesArray); // fill the array
            pagerStrip.setArrayTitles(titlesArray);
        }
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
    public void onDestroyView() {
        super.onDestroyView();

        if(activity == null)
            return;
        activity.clearBackStack(pages);
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
