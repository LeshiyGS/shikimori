package org.shikimori.client.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import org.shikimori.client.R;
import org.shikimori.library.fragments.AnimeDeatailsFragment;
import org.shikimori.library.tool.ProjectTool;

/**
 * Created by Владимир on 16.06.2015.
 */
public class PreferencesShikiActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        Preference update_serie_number = findPreference("update_serie_number");
        PreferenceCategory videoGroup = (PreferenceCategory) findPreference("pref_video_data_group");
        PreferenceScreen screen = getPreferenceScreen();

        if (!ProjectTool.isFullVersion()) {
            screen.removePreference(videoGroup);
        }
        update_serie_number.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals("update_serie_number")) {
            AnimeDeatailsFragment.UPDATE_AUTO_SERIES = !AnimeDeatailsFragment.UPDATE_AUTO_SERIES;
        }
        return false;
    }
}
