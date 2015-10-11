package org.shikimori.library.activity;

import android.os.Bundle;

import org.shikimori.library.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.fragments.CharacterDetailsFragment;
import org.shikimori.library.fragments.ClubDetailsFragment;
import org.shikimori.library.fragments.DiscusionFragment;
import org.shikimori.library.fragments.FavoriteFragment;
import org.shikimori.library.fragments.MangaDeatailsFragment;
import org.shikimori.library.fragments.profile.ProfileShikiFragment;
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
    public static final int DISCUSSION = 10;
    public static final int ADD_ELEMENT = 11;
    protected Bundle params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeArrow(true);
        params = getIntent().getExtras();
        chosePage();
    }

    protected void chosePage() {
        String title = params!= null ? params.getString(Constants.ACTION_BAR_TITLE) : null;
        if(title!=null)
            setTitle(title);

        if (page == ANIME_PAGE) {
            addPageFragment(AnimeDeatailsFragment.newInstance(params), R.string.anime);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
        } else if (page == MANGA_PAGE) {
            addPageFragment(MangaDeatailsFragment.newInstance(params), R.string.manga);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
        } else if (page == CHARACTER_PAGE) {
            addPageFragment(CharacterDetailsFragment.newInstance(params), R.string.character);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
        } else if (page == FAVORITES_PAGE) {
            loadPage(FavoriteFragment.newInstance(params));
            setTitle(R.string.favorite);
            return;
        } else if (page == DISCUSSION) {
            loadPage(DiscusionFragment.newInstance(params));
            return;
        } else if (page == OFTOPIC_PAGE) {
            addPageFragment(TopicMainCommentFragment.newInstance(params), R.string.topic);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
        } else if (page == USER_PROFILE) {
            addPageFragment(ProfileShikiFragment.newInstance(params), R.string.info);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.lenta);
        } else if (page == CLUB_PAGE) {
            addPageFragment(ClubDetailsFragment.newInstance(params), R.string.description);
            addPageFragment(DiscusionFragment.newInstance(params), R.string.discusion);
        } else if (page == FORUMS_PAGE) {
            loadPage(UserClubsFragment.newInstance(params));
            setTitle(R.string.clubs);
            return;
        }
        showPages();
    }
}
