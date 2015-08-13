package org.shikimori.client.activity;

import android.os.Bundle;

import org.shikimori.client.fragments.AboutFragment;
import org.shikimori.library.activity.BaseActivity;

/**
 * Created by Владимир on 13.08.2015.
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage(AboutFragment.newInstance());
    }
}
