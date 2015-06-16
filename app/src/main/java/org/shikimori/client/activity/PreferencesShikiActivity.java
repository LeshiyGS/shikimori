package org.shikimori.client.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.shikimori.client.R;

/**
 * Created by Владимир on 16.06.2015.
 */
public class PreferencesShikiActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
