package org.shikimori.client.fragments;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.shikimori.client.R;

/**
 * Created by Владимир on 16.06.2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PreferenceShikiPragment extends PreferenceFragment {

    public static PreferenceShikiPragment newInstance() {
        return new PreferenceShikiPragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

}
