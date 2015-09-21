package org.shikimori.library.custom.actionmode;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.objects.one.ItemCommentsShiki;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class BaseQuoteCallback implements ActionMode.Callback {

    protected TextView textView;
    protected EditText editText;

    public BaseQuoteCallback(TextView textView, EditText editText){
        this.textView = textView;
        this.editText = editText;
    }

    public BaseQuoteCallback(){}

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.quote_menu, menu);
        return true; 
    } 
 
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; 
    } 
 
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        return false; 
    }
 
    public void onDestroyActionMode(ActionMode mode) {
//        if(textView!=null)
//            textView.setTextIsSelectable(false);
    }
} 