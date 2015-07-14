package org.shikimori.library.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.tool.h;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class FiltersDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    ViewGroup llContainer;
    private View bFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filters_dialog, null);
        llContainer = h.get(v, R.id.llContainer);
        bFilter = h.get(v, R.id.bFilter);

        bFilter.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bFilter){

        }
    }
}
