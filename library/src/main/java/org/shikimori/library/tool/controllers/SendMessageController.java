package org.shikimori.library.tool.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import ru.altarix.basekit.library.tools.DialogCompat;
import ru.altarix.basekit.library.tools.LoaderController;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.edittext.QuoteEditText;
import org.shikimori.library.tool.hs;

/**
 * Created by Феофилактов on 07.05.2015.
 */
public class SendMessageController {
    private final Context context;
    private Type type;
    private String mestype;
    private final QuoteEditText quoteTool;
    private LoaderController loader;
    private final QueryShiki query;
    private EditText editText;
    private MessageData data;

    private String updateId;

    public enum Type{
        COMMENT, MESSAGE
    }

    public SendMessageController(Context context, QueryShiki query, EditText editText, Type type) {
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

        hs.insertTextEditText(editText, str.toString());
        quoteTool.setText(editText.getText().toString());
        hs.showKeyboard(context, editText);
    }

    protected void deleteMessage() {
        loader.show();
        query.init(ShikiApi.getUrl(data.deleteUrl() == null ?
                ShikiPath.COMMENTS : data.deleteUrl()) + "/" + data.getMessageId())
                .setMethod(QueryShiki.METHOD.DELETE)
                .getResult(new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
                    @Override
                    public void onQuerySuccess(ShikiStatusResult res) {
                        loader.hide();
                        data.removeItem();
//                        YoYo.with(Techniques.FadeOutUp)
//                                .withListener(new BaseAnimationListener(){
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        data.removeItem();
//                                    }
//                                })
//                                .duration(300)
//                                .playOn(data.getParent());
                    }
                });
    }

    public void showPopup(View v, final MessageData data) {
        this.data = data;

        AlertDialog.Builder dialog = new DialogCompat((Activity) context)
                .getDialog();

//        PopupMenuCompat popup = PopupMenuCompat.newInstance(context, v);
//        popup.inflate(R.menu.discus_menu);

        if(!data.isOwner()){
            dialog.setItems(R.array.select_answer, answerClickDialog);
//            popup.getMenu().removeItem(R.id.icDelete);
//            popup.getMenu().removeItem(R.id.icUpdate);
        } else {
            dialog.setItems(R.array.select_answers, answerClickDialog);
        }
        dialog.show();
//        popup.setOnMenuItemClickListener(new PopupMenuCompat.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                updateId = null;
//                if (item.getItemId() == R.id.icAnswer) {
//                    answer(data.getMessageId(), data.getNickName());
//                    return true;
//                } else if (item.getItemId() == R.id.icDelete) {
//                    deleteMessage();
//                    return true;
//                } else if (item.getItemId() == R.id.icUpdate) {
//                    updateId = data.getMessageId();
//                    quoteTool.setText(data.getMessagetext());
//                    return true;
//                }
//                return false;
//            }
//        });
//        popup.show();
    }

    DialogInterface.OnClickListener answerClickDialog = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            updateId = null;
            if (which == 0) {
                answer(data.getMessageId(), data.getNickName());
            } else if (which == 1) {
                deleteMessage();
            } else if (which == 2) {
                updateId = data.getMessageId();
                quoteTool.setText(data.getMessagetext());
            }
        }
    };

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
