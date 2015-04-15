package org.shikimori.library.activity;

import android.os.Bundle;

import org.shikimori.library.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;

/**
 * Created by Феофилактов on 07.04.2015.
 */
public class ShowPageActivity extends PageActivity {

    public static final int ANIME_PAGE = 1;
    public static final int MANGA_PAGE = 2;
    public static final int TOPIC_PAGE = 3;
    public static final int OFTOPIC_PAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chosePage();
    }

    private void chosePage() {
        Bundle b = getIntent().getExtras();
        if(page == ANIME_PAGE){
            addPageFragment(AnimeDeatailsFragment.newInstance(b), R.string.anime);
            addPageFragment(DiscusionFragment.newInstance(b), R.string.discusion);
            showPages();
        } else if(page == MANGA_PAGE){
            addPageFragment(MangaDeatailsFragment.newInstance(b), R.string.manga);
            addPageFragment(DiscusionFragment.newInstance(b), R.string.discusion);
            showPages();
        } else if(page == OFTOPIC_PAGE){
            loadPage(DiscusionFragment.newInstance(b));
        }
    }
}
