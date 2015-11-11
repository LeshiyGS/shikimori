package org.shikimori.library.adapters.holder;

import android.view.View;

import org.shikimori.library.R;

/**
 * Created by Владимир on 06.04.2015.
 */
public class MessageRecycleHolder extends SettingsRecycleHolder{
    public View llActions;
    public View bComment, bGoTo;

    public MessageRecycleHolder(View itemView) {
        super(itemView);
        bGoTo = find(R.id.bGoTo);
        bComment = find(R.id.bComment);
        llActions = find(R.id.llActions);
        tvStatus = find(R.id.tvSection);
    }
}
