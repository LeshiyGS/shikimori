package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.shikimori.library.R;
import org.shikimori.library.adapters.ChatAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.SendMessageController;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Феофилактов on 06.05.2015.
 */
public class ChatFragment extends BaseListViewFragment implements View.OnClickListener {

    private EditText etMessage;
    private View ivSend;
    private String toUserNickname;
    private SendMessageController messageController;
    private ChatAdapter adptr;
    private String toUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.view_shiki_list_comment;
    }

    @Override
    public int getWrapperId() {
        return R.id.swipeLayout;
    }

    @Override
    protected boolean isOptionsMenu() {
        return false;
    }

    public static ChatFragment newInstance(String toUserNickName, String toUserId) {
        Bundle b = new Bundle();
        b.putString(Constants.USER_NICKNAME, toUserNickName);
        b.putString(Constants.TO_USER_ID, toUserId);

        ChatFragment frag = new ChatFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setBaseView(v);
        etMessage = find(R.id.etMessage);
        ivSend = find(R.id.ivSend);

        ivSend.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toUserNickname = getParam(Constants.USER_NICKNAME);
        toUserId = getParam(Constants.TO_USER_ID);
        messageController = new SendMessageController(activity, query, etMessage);
        loadData();
    }

    String getUrlPath(){
        return ShikiApi.getUrl(ShikiPath.DIALOGS_ID, toUserNickname);
    }

    @Override
    public void loadData() {
        query.init(getUrlPath(), StatusResult.TYPE.ARRAY)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .getResult(this);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        clearData();
        loadData();
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemNewsUserShiki> builder = new ObjectBuilder<>(res.getResultArray(), ItemNewsUserShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        adptr = new ChatAdapter(activity, list);
        adptr.setOnSettingsListener(this);
        return adptr;
    }

    /**
     * inbox ttp://shikimori.org/messages
     * message[kind]:Private
     * message[from_id]:35934
     * message[to_id]:1
     * message[body]:[message=30068968]morr[/message], ага, работа
     */
    void sendMessageToServer() {
        String text = etMessage.getText().toString().trim();
        if (text.length() == 0) {
            Crouton.showText(activity, R.string.set_message, Style.ALERT);
            return;
        }

        if (toUserNickname == null)
            return;

        showRefreshLoader();
        etMessage.setEnabled(false);
        query.init(ShikiApi.getUrl(ShikiPath.MESSAGESPRIVATE))
                .setMethod(Query.METHOD.POST)
                .addParam("message[kind]", "Private")
                .addParam("message[from_id]", getUserId())
                .addParam("message[to_id]", toUserId)
                .addParam("message[body]", text)
                .setErrorListener(new Query.OnQueryErrorListener() {
                    @Override
                    public void onQueryError(StatusResult res) {
                        etMessage.setEnabled(true);
                    }
                })
                .getResult(new Query.OnQuerySuccessListener() {
                    @Override
                    public void onQuerySuccess(StatusResult res) {
                        onStartRefresh();
                        etMessage.setEnabled(true);
                        etMessage.setText("");
                    }
                });
    }

    /**
     * Менюшка у сообщения
     * цитировать сообщение пользователя
     *
     * @param v
     */
    private void answerMessage(final View v) {

        final int position = (int) v.getTag();
        final ItemNewsUserShiki obj = adptr.getItem(position);
        messageController.showPopup(v, new SendMessageController.MessageData<ItemNewsUserShiki>() {

            @Override
            public void removeItem() {
                ChatFragment.this.removeItem(position);
                clearData();
            }

            @Override
            public String getMessageId() {
                return obj.id;
            }

            @Override
            public String getNickName() {
                return obj.from.nickname;
            }

            @Override
            public View getParent() {
                return (View) v.getParent().getParent();
            }

            @Override
            public boolean isOwner() {
                return true;
            }

            @Override
            public String deleteUrl() {
                return ShikiPath.MESSAGESPRIVATE;
            }
        });
    }

    void clearData() {
        query.invalidateCache(getUrlPath());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            sendMessageToServer();
        } else if (v.getId() == R.id.icSettings) {
            answerMessage(v);
        }
    }
}
