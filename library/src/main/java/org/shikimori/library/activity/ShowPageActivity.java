package org.shikimori.library.activity;

import android.os.Bundle;

import org.shikimori.library.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.CharacterDetailsFragment;
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
    public static final int CHARACTER_PAGE = 5;
    protected Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        params = getIntent().getExtras();
        chosePage();
    }

    protected void chosePage() {
        if(page == ANIME_PAGE){
            addPageFragment(AnimeDeatailsFragment.newInstance(params), R.string.anime);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
        } else if(page == MANGA_PAGE){
            addPageFragment(MangaDeatailsFragment.newInstance(params), R.string.manga);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
        } else if(page == CHARACTER_PAGE){
            addPageFragment(CharacterDetailsFragment.newInstance(params), R.string.character);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
        } else if(page == OFTOPIC_PAGE){
            loadPage(DiscusionFragment.newInstance(params));
        }
    }
}
