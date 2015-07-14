package org.shikimori.library.activity;

import android.os.Bundle;

import org.shikimori.library.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.CharacterDetailsFragment;
import org.shikimori.library.fragments.ClubDetailsFragment;
import org.shikimori.library.fragments.CommunityClubsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.FavoriteFragment;
import org.shikimori.library.fragments.InfoMediaFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;
import org.shikimori.library.fragments.ProfileShikiFragment;
import org.shikimori.library.fragments.TopicMainCommentFragment;
import org.shikimori.library.fragments.UserClubsFragment;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Феофилактов on 07.04.2015.
 */
public class ShowPageActivity extends PageActivity {

    public static final int ANIME_PAGE = 1;
    public static final int MANGA_PAGE = 2;
    public static final int TOPIC_PAGE = 3;
    public static final int OFTOPIC_PAGE = 4;
    public static final int CHARACTER_PAGE = 5;
    public static final int FAVORITES_PAGE = 6;
    public static final int USER_PROFILE = 7;
    public static final int CLUB_PAGE = 8;
    public static final int FORUMS_PAGE = 9;
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
        } else if(page == FAVORITES_PAGE){
            loadPage(FavoriteFragment.newInstance());
            setTitle(R.string.favorite);
        } else if(page == OFTOPIC_PAGE){
            addPageFragment(TopicMainCommentFragment.newInstance(params), R.string.topic);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
        } else if(page == USER_PROFILE){
            addPageFragment(ProfileShikiFragment.newInstance(params), R.string.info);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.lenta);
            showPages();
            //loadPage(ProfileShikiFragment.newInstance(params));
        } else if(page == CLUB_PAGE){
            addPageFragment(ClubDetailsFragment.newInstance(params), R.string.description);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
            showPages();
        } else if(page == FORUMS_PAGE){
            addPageFragment(UserClubsFragment.newInstance(params), R.string.clubs);
            //addPageFragment(blablablaFragment.newInstance(), R.string.topics);
            showPages();
        }
    }
}
