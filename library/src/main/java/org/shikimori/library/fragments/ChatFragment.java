package org.shikimori.library.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.shikimori.library.R;
import org.shikimori.library.adapters.ChatAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ApiMessageController;
import org.shikimori.library.tool.controllers.ReadMessageController;
import org.shikimori.library.tool.controllers.SendMessageController;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

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
    private BodyBuild bodyBuilder;
    private ApiMessageController apiController;

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
        ReadMessageController.newInstance(getFC().getQuery());
        apiController = new ApiMessageController(getFC().getQuery());
        apiController.setErrorListener(this);
        toUserNickname = getParam(Constants.USER_NICKNAME);
        toUserId = getParam(Constants.TO_USER_ID);
        messageController = new SendMessageController(activity, getFC().getQuery(), etMessage, SendMessageController.Type.MESSAGE);
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        showRefreshLoader();
        loadData();
    }

    String getUrlPath(){
        return ShikiApi.getUrl(ShikiPath.DIALOGS_ID, toUserNickname);
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getUrlPath(), StatusResult.TYPE.ARRAY)
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
        loadAsyncBuild(bodyBuilder, res.getResultArray(),ItemNewsUserShiki.class);
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
        String text = errorEmpty();
        if (text == null)
            return;

        if (toUserNickname == null)
            return;

        showRefreshLoader();
        etMessage.setEnabled(false);

        apiController.init().sendPrivateMessage(getFC().getUserId(), toUserId, text, new Query.OnQuerySuccessListener() {
            @Override
            public void onQuerySuccess(StatusResult res) {
                onStartRefresh();
                etMessage.setEnabled(true);
                etMessage.setText("");
                hs.hideKeyboard(activity, etMessage);
            }
        });
    }

    // TODO сделать другой message controller и убрать дубликаты из DiscusionFragment и от сюда
    private void updateMessage() {
        String text = errorEmpty();
        if (text == null)
            return;

        apiController.init().updatePrivateMessage(messageController.getUpdateId(), text, new Query.OnQuerySuccessListener() {
            @Override
            public void onQuerySuccess(StatusResult res) {
                clearData();
                hs.hideKeyboard(activity, etMessage);
                etMessage.setText("");
                ItemNewsUserShiki item = new ItemNewsUserShiki().createFromJson(res.getResultObject());
                // парсим заного сообщение и отображаем
                if (!TextUtils.isEmpty(item.getHtml())) {
                    ItemNewsUserShiki inList = adptr.getItemById(item.id);
                    if (inList != null) {
                        inList.htmlBody = item.htmlBody;
                        inList.parsedContent = new LinearLayout(activity);
                        inList.parsedContent.setLayoutParams(hs.getDefaultParams());
                        bodyBuilder.parce(inList.htmlBody, inList.parsedContent);
                        adptr.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private String errorEmpty() {
        String text = etMessage.getText().toString().trim();
        if (text.length() == 0) {
            Crouton.showText(activity, R.string.set_message, Style.ALERT);
            return null;
        }
        return text;
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
                Parcelable state = null;
                if(page == DEFAULT_FIRST_PAGE)
                    state = getListView().onSaveInstanceState();
                ChatFragment.this.removeItem(position);
                clearData();
                if(state!=null)
                    getListView().onRestoreInstanceState(state);
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

            @Override
            public String getMessagetext() {
                return obj.body;
            }
        });
    }

    void clearData() {
        getFC().getQuery().invalidateCache(getUrlPath());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            if(!etMessage.isEnabled()){
                Crouton.makeText(activity, R.string.wait_load_data, Style.ALERT).show();
                return;
            }
            if(messageController.getUpdateId()!=null)
                updateMessage();
            else
                sendMessageToServer();
        } else if (v.getId() == R.id.icSettings) {
            answerMessage(v);
        }
    }

    @Override
    public void onQueryError(StatusResult res) {
        super.onQueryError(res);
        etMessage.setEnabled(true);
    }
}
