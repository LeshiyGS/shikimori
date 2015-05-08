package org.shikimori.library.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;

import org.shikimori.library.R;
import org.shikimori.library.adapters.NewsUserAdapter;
import org.shikimori.library.fragments.base.abstracts.BaseListViewFragment;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.objects.one.ItemNewsUserShiki;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Феофилактов on 06.05.2015.
 */
public class ChatFragment extends BaseListViewFragment implements View.OnClickListener {

    private EditText etMessage;
    private View ivSend;
    private String toUserId;

    @Override
    protected int getLayoutId() {
        return R.layout.view_shiki_list_comment;
    }

    public static ChatFragment newInstance(String toUserId) {
        Bundle b = new Bundle();
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
        toUserId = getParam(Constants.TO_USER_ID);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        ObjectBuilder<ItemNewsUserShiki> builder = new ObjectBuilder<>(res.getResultArray(), ItemNewsUserShiki.class);
        prepareData(builder.list, true, true);
    }

    @Override
    public ArrayAdapter<ItemNewsUserShiki> getAdapter(List list) {
        return new NewsUserAdapter(activity, list);
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

        if (toUserId == null)
            return;

        showRefreshLoader();
        etMessage.setEnabled(false);
        query.init(ShikiApi.getUrl(ShikiPath.COMMENTS))
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
     * @param v
     */
    private void answerMessage(View v) {
        int position = (int) v.getTag();
        ListAdapter adp = getListView().getAdapter();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            sendMessageToServer();
        } else if (v.getId() == R.id.icSettings){
            answerMessage(v);
        }
    }
}
