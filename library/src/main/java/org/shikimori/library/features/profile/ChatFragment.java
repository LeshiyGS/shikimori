package org.shikimori.library.features.profile;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.shikimori.library.R;
import org.shikimori.library.custom.EditTextSender;
import org.shikimori.library.features.profile.adapter.ChatRecyclerAdapter;
import org.shikimori.library.custom.actionmode.QuotePartCallback;
import org.shikimori.library.fragments.base.abstracts.recycleview.BaseRecycleViewFragment;
import org.shikimori.library.fragments.base.abstracts.recycleview.ListRecycleAdapter;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.LoadAsyncBuildHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.api.ApiMessageController;
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
public class ChatFragment extends BaseRecycleViewFragment implements View.OnClickListener, BaseQuery.OnQuerySuccessListener<ShikiStatusResult> {

    private EditText etMessage;
    private View ivSend;
    private String toUserNickname;
    private SendMessageController messageController;
    private ChatRecyclerAdapter adptr;
    private String toUserId;
    private BodyBuild bodyBuilder;
    private ApiMessageController apiController;
    private LoadAsyncBuildHelper lah;
    private EditTextSender etSender;

    @Override
    public int getLayoutId() {
        return R.layout.view_shiki_list_comment_recycle;
    }

//    @Override
//    protected boolean isOptionsMenu() {
//        return false;
//    }

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
        etSender = find(R.id.etSender);
        etMessage = etSender.getEtText();
        ivSend = etSender.getIvSend();

        ivSend.setOnClickListener(this);

        return v;
    }

    @Override
    public String getActionBarTitleString() {
        return getParam(Constants.USER_NICKNAME);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ReadMessageController.newInstance(getFC().getQuery());
        lah = new LoadAsyncBuildHelper(activity, this);
        apiController = new ApiMessageController(getFC().getQuery());
        apiController.setErrorListener(this);
        toUserNickname = getParam(Constants.USER_NICKNAME);
        toUserId = getParam(Constants.TO_USER_ID);

//        activity.setTitle(toUserNickname);
        messageController = new SendMessageController(activity, getFC().getQuery(), etMessage, SendMessageController.Type.MESSAGE);
        etSender.setType(SendMessageController.Type.MESSAGE);
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        if(Build.VERSION.SDK_INT > 10){
            QuotePartCallback actionMode = new QuotePartCallback();
            actionMode.setEditText(etMessage);
            actionMode.setTyle(QuotePartCallback.TYPE.MESSAGE);
            bodyBuilder.setActionClick(actionMode);
        }
        showRefreshLoader();
        loadData();
    }

    String getUrlPath(){
        return ShikiApi.getUrl(ShikiPath.DIALOGS_ID, toUserNickname);
    }

    @Override
    public void loadData() {
        getFC().getQuery().init(getUrlPath(), ShikiStatusResult.TYPE.ARRAY)
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
    public void onQuerySuccess(ShikiStatusResult res) {
        if(activity == null)
            return;
        lah.loadAsyncBuild(bodyBuilder, res.getResultArray(),ItemNewsUserShiki.class);
    }

    @Override
    public ListRecycleAdapter getAdapter(List<?> list) {
        adptr = new ChatRecyclerAdapter(activity, (List<ItemNewsUserShiki>) list);
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

        apiController.init().sendPrivateMessage(getFC().getUserId(), toUserId, text, new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
            @Override
            public void onQuerySuccess(ShikiStatusResult res) {
                if(activity == null)
                    return;
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

        apiController.init().updatePrivateMessage(messageController.getUpdateId(), text, new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
            @Override
            public void onQuerySuccess(ShikiStatusResult res) {
                if(activity == null)
                    return;
                clearData();
                hs.hideKeyboard(activity, etMessage);
                etMessage.setText("");
                ItemNewsUserShiki item = new ItemNewsUserShiki().create(res.getResultObject());
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

        final ItemNewsUserShiki obj = (ItemNewsUserShiki) v.getTag();
        messageController.showPopup(v, new SendMessageController.MessageData<ItemNewsUserShiki>() {

            @Override
            public void removeItem() {
//                Parcelable state = null;
//                if(page == DEFAULT_FIRST_PAGE)
//                    state = getListView().onSaveInstanceState();
                ChatFragment.this.removeItem(obj);
                clearData();
//                if(state!=null)
//                    getListView().onRestoreInstanceState(state);
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
    public void onQueryError(ShikiStatusResult res) {
        super.onQueryError(res);
        etMessage.setEnabled(true);
    }
}
