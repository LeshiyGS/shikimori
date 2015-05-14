package org.shikimori.library.tool.actionmode;

import android.support.v7.view.ActionMode;

/**
 * @author Александр Свиридов on 10.12.13.
 */
public abstract class DestroyActionCallback implements ActionMode.Callback {

    private DestroyAction action;

    public DestroyActionCallback(DestroyAction action) {
        this.action = action;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        action.destroyActionMode();
    }

    public interface DestroyAction {
        public void destroyActionMode();
    }
}