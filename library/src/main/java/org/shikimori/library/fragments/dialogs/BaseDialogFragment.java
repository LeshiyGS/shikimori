package org.shikimori.library.fragments.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public abstract class BaseDialogFragment extends DialogFragment {


    protected int getTitle(){
        return 0;
    }

    protected void initTitle() {
        if (getTitle() != 0)
            getDialog().setTitle(getTitle());
        else
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitle();
    }
}
