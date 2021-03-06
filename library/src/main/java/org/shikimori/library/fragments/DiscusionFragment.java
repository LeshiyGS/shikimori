package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.custom.EditTextSender;
import org.shikimori.library.custom.actionmode.QuotePartCallback;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.api.ApiMessageController;
import org.shikimori.library.tool.controllers.SendMessageController;
import org.shikimori.library.tool.controllers.SendMessageController.MessageData;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class DiscusionFragment extends BaseListViewFragment implements ExtraLoadInterface, View.OnClickListener {

    private String topicId;
    private String userId;
    private String disType;
    BodyBuild bodyBuilder;
    private EditText etMessage;
    private TextView tvEmptyView;
    View ivSend;
    private SendMessageController messageController;
    private CommentsAdapter adaptr;
    private ApiMessageController apiController;
    private boolean adminRole;
    private EditTextSender etSender;

    @Override
    public int getLayoutId() {
        return R.layout.view_shiki_list_comment;
    }

    @Override
    public int getWrapperId() {
        return R.id.swipeLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setBaseView(v);
        etSender = find(R.id.etSender);
        etMessage = etSender.getEtText();
        ivSend = etSender.getIvSend();
        tvEmptyView = find(R.id.tvEmptyView);
        ivSend.setOnClickListener(this);

        tvEmptyView.setTypeface(null, Typeface.ITALIC);
        return v;
    }

    public static DiscusionFragment newInstance(Bundle b) {
        DiscusionFragment frag = new DiscusionFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBodyBuilder();
        etSender.setType(SendMessageController.Type.COMMENT);
        apiController = new ApiMessageController(getFC().getQuery());
        apiController.setErrorListener(this);
        messageController = new SendMessageController(activity, getFC().getQuery(), etMessage, SendMessageController.Type.COMMENT);

        initParams();

        if (topicId != null) {
            showRefreshLoader();
            loadData();
        }
    }

    private void initBodyBuilder() {
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        if(Build.VERSION.SDK_INT < 11)
            return;
        QuotePartCallback action = new QuotePartCallback();
        action.setEditText(etMessage);
        action.setTyle(QuotePartCallback.TYPE.COMMENT);
        bodyBuilder.setActionClick(action);
    }

    private void initParams() {
        if (topicId == null)
            topicId = getParam(Constants.TOPIC_ID);

        if (userId == null)
            userId = getParam(Constants.USER_ID);

        if (disType == null)
            disType = getParam(Constants.DISSCUSION_TYPE, Constants.TYPE_ENTRY);

        if (topicId == null) topicId = userId;
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        clearData();
        loadData();
    }

    void clearData() {
        ContentValues cv = new ContentValues();
        cv.put("commentable_id", topicId);
        cv.put("commentable_type", disType);
        getFC().getQuery().invalidateCache(ShikiApi.getUrl(ShikiPath.COMMENTS), cv);
        messageController.clearUpdateId();
    }

    // TODO create loader list
    public void loadData() {
        if (getFC().getQuery() == null)
            return;
        getFC().getQuery().init(ShikiApi.getUrl(ShikiPath.COMMENTS), ShikiStatusResult.TYPE.ARRAY)
                .addParam("commentable_id", topicId)
                .addParam("commentable_type", disType)
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, QueryShiki.FIVE_MIN)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(final ShikiStatusResult res) {
        loadAsyncBuild(bodyBuilder, res.getResultArray(), ItemCommentsShiki.class);
    }

    @Override
    public void prepareData(List<?> list, boolean removeLastItem, boolean limitOver) {
        super.prepareData(list, removeLastItem, limitOver);
        hs.setVisibleGone(getAllList().size() != 0, tvEmptyView);
    }

    @Override
    public ArrayAdapter<ItemCommentsShiki> getAdapter(List list) {
        adaptr = new CommentsAdapter(activity, list);
        adaptr.setOnSettingsListener(this);
        return adaptr;
    }

    @Override
    public void extraLoad(String itemId, Bundle params) {
        this.topicId = itemId;
        if(params!=null){
            adminRole = "admin".equals(params.getString(Constants.ROLE_CLUB));
        }
        // Ждем пока фрагмент присасется к активити и не инициализируется
        Thread mythread = new Thread() {
            public void run() {
                try {
                    while (activity == null || getFC().getQuery() == null) {
                        sleep(500);
                    }
                } catch (Exception e) {
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showRefreshLoader();
                        loadData();
                    }
                });
            }
        };
        mythread.start();
    }

    @Override
    public void onClick(final View v) {
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

    /**
     * Менюшка у сообщения
     * цитировать сообщение пользователя
     *
     * @param v
     */
    private void answerMessage(final View v) {

        final int position = (int) v.getTag();
        final ItemCommentsShiki obj = adaptr.getItem(position);
        messageController.showPopup(v, new MessageData<ItemCommentsShiki>() {

            @Override
            public void removeItem() {
                DiscusionFragment.this.removeItem(position);
                clearData();
            }

            @Override
            public String getMessageId() {
                return obj.id;
            }

            @Override
            public String getNickName() {
                return obj.nickname;
            }

            @Override
            public View getParent() {
                return (View) v.getParent().getParent();
            }

            @Override
            public boolean isOwner() {
                return DiscusionFragment.this.getFC()
                        .getUserId().equals(obj.user_id);
            }

            @Override
            public String deleteUrl() {
                return null;
            }

            @Override
            public String getMessagetext() {
                return obj.body;
            }
        });
    }

    void sendMessageToServer() {
        String text = errorEmpty();
        if (text == null)
            return;

        if (topicId == null)
            return;

        showRefreshLoader();
        etMessage.setEnabled(false);

        apiController.init().sendComment(topicId, getFC().getUserId(), disType, text, new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
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

    private void updateMessage() {
        String text = errorEmpty();
        if (text == null)
            return;

        apiController.updateComment(messageController.getUpdateId(), text, new QueryShiki.OnQuerySuccessListener<ShikiStatusResult>() {
            @Override
            public void onQuerySuccess(ShikiStatusResult res) {
                if(activity == null)
                    return;
                clearData();
                hs.hideKeyboard(activity, etMessage);
                etMessage.setText("");
                ItemCommentsShiki item = new ItemCommentsShiki().create(res.getResultObject());
                // парсим заного сообщение и отображаем
                if (!TextUtils.isEmpty(item.getHtml())) {
                    ItemCommentsShiki inList = adaptr.getItemById(item.id);
                    if (inList != null) {
                        inList.html_body = item.html_body;
                        inList.parsedContent = new LinearLayout(activity);
                        inList.parsedContent.setLayoutParams(hs.getDefaultParams());
                        bodyBuilder.parce(inList.html_body, inList.parsedContent);
                        adaptr.notifyDataSetChanged();
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

    @Override
    public void onQueryError(ShikiStatusResult res) {
        super.onQueryError(res);
        etMessage.setEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        etSender.onActivityResult(requestCode,resultCode,data);
    }
}
