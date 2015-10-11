package org.shikimori.library.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.shikimori.library.fragments.AMListMediaFragment;
import org.shikimori.library.tool.constpack.Constants;

/**
 * Created by Феофилактов on 11.10.2015.
 */
public class AddItemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle params = getIntent().getExtras();
        switch (params.getString(Constants.TYPE)) {
            case Constants.ANIME:
            case Constants.MANGA:
                AMListMediaFragment frag = AMListMediaFragment.newInstance(params);
                frag.setSelectOutside(true);
                loadPage(frag);
                break;
        }
    }
}
