package org.shikimori.library.tool.controllers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jmpergar.awesometext.AwesomeTextHandler;
import com.nineoldandroids.animation.Animator;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.LoaderController;
import org.shikimori.library.tool.baselisteners.BaseAnimationListener;
import org.shikimori.library.tool.edittext.PostSpanRenderer;
import org.shikimori.library.tool.edittext.QuoteEditText;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.pmc.PopupMenuCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 07.05.2015.
 */
public class SendMessageController {
    private final Context context;
    private Type type;
    private final QuoteEditText quoteTool;
    private LoaderController loader;
    private final Query query;
    private EditText editText;
    private MessageData data;

    private String updateId;

    public enum Type{
        COMMENT, MESSAGE
    }

    public SendMessageController(Context context, Query query, EditText editText, Type type) {
        this.context = context;
        this.type = type;
        this.loader = query.getLoader();
        this.query = query;
        this.editText = editText;

        quoteTool = new QuoteEditText(editText);
    }

    protected void answer(String id, String nickname) {
        String msgType = type == Type.COMMENT ? "comment" : "message";

        StringBuilder str = new StringBuilder("[");
        str.append(msgType).append("=")
           .append(id).append("]")
            .append(nickname)
            .append("[/")
            .append(msgType)
            .append("] ");

        h.insertTextEditText(editText, str.toString());
        quoteTool.setText(editText.getText().toString());
        h.showKeyboard(context, editText);
    }

    protected void deleteMessage() {
        loader.show();
        query.init(ShikiApi.getUrl(data.deleteUrl() == null ?
                ShikiPath.COMMENTS : data.deleteUrl()) + "/" + data.getMessageId())
                .setMethod(Query.METHOD.DELETE)
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        loader.hide();
                        YoYo.with(Techniques.FadeOutUp)
                                .withListener(new BaseAnimationListener(){
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        data.removeItem();
                                    }
                                })
                                .duration(300)
                                .playOn(data.getParent());
                    }
                });
    }

    public void showPopup(View v, final MessageData data) {
        this.data = data;
        PopupMenuCompat popup = PopupMenuCompat.newInstance(context, v);
        popup.inflate(R.menu.discus_menu);

        if(!data.isOwner()){
            popup.getMenu().removeItem(R.id.icDelete);
            popup.getMenu().removeItem(R.id.icUpdate);
        }

        popup.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                updateId = null;
                if (item.getItemId() == R.id.icAnswer) {
                    answer(data.getMessageId(), data.getNickName());
                    return true;
                } else if (item.getItemId() == R.id.icDelete) {
                    deleteMessage();
                    return true;
                } else if (item.getItemId() == R.id.icUpdate) {
                    updateId = data.getMessageId();
                    quoteTool.setText(data.getMessagetext());
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public String getUpdateId() {
        return updateId;
    }

    public void clearUpdateId() {
        updateId = null;
    }

    public interface MessageData<T> {

        public void removeItem();

        public String getMessageId();

        public String getNickName();

        public View getParent();

        public boolean isOwner();

        public String deleteUrl();

        public String getMessagetext();
    }
}
