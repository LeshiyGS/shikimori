package org.shikimori.client.activity;

import android.os.Bundle;
import org.shikimori.client.fragments.AuthFragment;

/**
 * Created by Владимир on 30.03.2015.
 */
public class AuthActivity extends ProjectActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage(AuthFragment.newInstance());
    }
}
