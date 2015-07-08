package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import org.shikimori.library.R;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.h;

import ru.altarix.ui.CustomSpinner;

/**
 * Created by Феофилактов on 08.07.2015.
 */
public class AddRateDialogFragment extends DialogFragment {
    private LayoutInflater inflater;
    private int title;
    private ProjectTool.TYPE type;

    CustomSpinner csRating, csStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initTitle();

        this.inflater = inflater;
        View v = inflater.inflate(R.layout.add_rate_dialog_fragment, null);

        csRating = h.get(v, R.id.csRating);
        csStatus = h.get(v, R.id.csStatus);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(type == ProjectTool.TYPE.ANIME)
            csStatus.setList(getActivity().getResources().getStringArray(R.array.status_anime));
        else
            csStatus.setList(getActivity().getResources().getStringArray(R.array.status_manga));

    }

    private void initTitle(){
        if (title != 0)
            getDialog().setTitle(title);
        else
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

    }

    public void setTitle(int title) {
        this.title = title;
    }

    public void setType(ProjectTool.TYPE type){
        this.type = type;
    }
}
