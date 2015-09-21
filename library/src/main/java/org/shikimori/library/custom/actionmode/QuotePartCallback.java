package org.shikimori.library.custom.actionmode;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.shikimori.library.R;
import org.shikimori.library.objects.ActionQuote;
import org.shikimori.library.tool.edittext.QuoteEditText;
import org.shikimori.library.tool.hs;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class QuotePartCallback extends BaseQuoteCallback {

    private TYPE type;
    private QuoteEditText quoteTool;

    public enum TYPE{
        MESSAGE, COMMENT
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.removeItem(android.R.id.selectAll);
        // Remove the "cut" option
        menu.removeItem(android.R.id.cut);
        // Remove the "copy all" option
        menu.removeItem(android.R.id.copy);
        return super.onPrepareActionMode(mode, menu);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        ActionQuote obj  = getItemObject();

        if (obj == null || textView == null) {
            mode.finish();
            return true;
        }

        int start = textView.getSelectionStart();
        int end = textView.getSelectionEnd();

        if (item.getItemId() == R.id.icQuote) {

            final CharSequence selectedText = textView.getText()
                    .subSequence(start, end);

//            [quote=c1476610;22422;Kōri no shita ni]возвращает обратно[/quote]
            StringBuilder str = new StringBuilder();
            if(start > 0)
                str.append(" ");
            str.append("[quote=");
            str.append(type == TYPE.COMMENT ? "c" : "m").append(obj.getCommentId()).append(";")
                    .append(obj.getUserId()).append(";")
                    .append(obj.getUserName()).append("]")
                    .append(selectedText)
                    .append("[/quote] ");

            insertText(str.toString());

            mode.finish();
            // here is where I would grab the selected text
            return true;
        }
        return false;
    }

    public ActionQuote getItemObject() {
        if(textView == null)
            return null;

        if(textView.getParent() == null || !(textView.getParent() instanceof View))
            return null;

        View parent = (View) textView.getParent();
        do{
            ActionQuote obj = (ActionQuote) parent.getTag(R.id.icQuote);
            if(obj!=null)
                return obj;
        }while ((parent = (View) parent.getParent())!=null);


        return null;
    }

    @Override
    public void setEditText(EditText editText) {
        super.setEditText(editText);
        quoteTool = new QuoteEditText(editText);
    }

    private void insertText(String text) {
        if (editText == null)
            return;
        hs.insertTextEditText(editText, text);
        if(quoteTool!=null)
            quoteTool.setText(editText.getText().toString());
    }

    public void setTyle(TYPE type){
        this.type = type;
    }

}