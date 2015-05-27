package org.shikimori.library.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.shikimori.library.R;
import org.shikimori.library.adapters.CommentsAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemCommentsShiki;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.SendMessageController;
import org.shikimori.library.tool.controllers.SendMessageController.MessageData;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by LeshiyGS on 1.04.2015.
 */
public class DiscusionFragment extends BaseListViewFragment implements ExtraLoadInterface, View.OnClickListener {

    private String treadId;
    BodyBuild bodyBuilder;
    private EditText etMessage;
    View ivSend;
    private SendMessageController messageController;
    private CommentsAdapter adaptr;

    @Override
    protected int getLayoutId() {
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
        etMessage = find(R.id.etMessage);
        ivSend = find(R.id.ivSend);

        ivSend.setOnClickListener(this);

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
        bodyBuilder = new BodyBuild(activity);
        messageController = new SendMessageController(activity, query, etMessage);

        initParams();

        if (treadId != null) {
            showRefreshLoader();
            loadData();
        }
    }

    private void initParams() {
        Bundle b = getArguments();
        if (b == null)
            return;
        if (treadId == null)
            treadId = b.getString(Constants.TREAD_ID);
    }

    @Override
    public void onStartRefresh() {
        super.onStartRefresh();
        clearData();
        loadData();
    }

    void clearData() {
        ContentValues cv = new ContentValues();
        cv.put("commentable_id", treadId);
        query.invalidateCache(ShikiApi.getUrl(ShikiPath.COMMENTS), cv);
    }

    // TODO create loader list
    public void loadData() {
        if (query == null)
            return;
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS), StatusResult.TYPE.ARRAY)
                .addParam("commentable_id", treadId)
                .addParam("commentable_type", "Entry")
                .addParam("limit", LIMIT)
                .addParam("page", page)
                .addParam("desc", "1")
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    @Override
    public void onQuerySuccess(final StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemCommentsShiki> builder =
                new ObjectBuilder<>(res.getResultArray(), ItemCommentsShiki.class);

        prepareData(builder.getDataList(), true, true);
    }

    @Override
    public ArrayAdapter<ItemCommentsShiki> getAdapter(List list) {
        adaptr = new CommentsAdapter(activity, list);
        adaptr.setOnSettingsListener(this);
        return adaptr;
    }

    @Override
    public void extraLoad(String itemId) {
        this.treadId = itemId;
        // Ждем пока фрагмент присасется к активити и не инициализируеться
        Thread mythread = new Thread() {
            public void run() {
                try {
                    while (query == null) {
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
                return getUserId().equals(obj.user_id);
            }
        });
    }

    void sendMessageToServer() {
        String text = etMessage.getText().toString().trim();
        if (text.length() == 0) {
            Crouton.showText(activity, R.string.set_message, Style.ALERT);
            return;
        }

        if (treadId == null)
            return;

        showRefreshLoader();
        etMessage.setEnabled(false);
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS))
                .setMethod(Query.METHOD.POST)
                .addParam("comment[commentable_id]", treadId)
                .addParam("comment[commentable_type]", "Entry")
                .addParam("comment[user_id]", getUserId())
                .addParam("comment[body]", text)
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

}
