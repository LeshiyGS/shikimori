package org.shikimori.library.custom.emoji;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.gars.emoji.library.BaseEmojiPage;

import org.shikimori.library.R;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.hs;

import java.util.HashMap;
import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;

/**
 * Created by Владимир on 15.10.2015.
 */
public class EmojiView extends BaseEmojiPage implements BaseQuery.OnQuerySuccessListener, AdapterView.OnItemClickListener {
    private SmileAdapter adapter;
    private Query query;
    private EditText etText;
    ObjectBuilder builder = new ObjectBuilder();
    private ImageView smileBtn;
    public static HashMap<String, String> cash = new HashMap<>();

    public EmojiView(Context context, Query query, EditText etText, ImageView ivSmails) {
        super(context);
        this.query = query;
        this.etText = etText;
        this.smileBtn = ivSmails;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_drawer_manga;
    }

    @Override
    protected void init() {
        super.init();
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onEmojiShow(boolean show) {
        if(show){
            if(adapter == null){
                query.in(ShikiPath.SMILEY)
                     .setCache(true, BaseQuery.HOUR * 24 * 7)
                     .getResultArray(this);
            }
        }

        if(smileBtn!=null){
            smileBtn.setImageResource(show ? R.mipmap.ic_action_keyboard : R.mipmap.ic_action_smail);
        }
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        List<SmileItem> list = builder.getDataList(res.getResultArray(), SmileItem.class);
//        if(cash.size() == 0){
//            for (SmileItem sm : list) {
//                cash.put(sm.bbcode, sm.path);
//            }
//        }
        adapter = new SmileAdapter(getContext(), list);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SmileItem item = adapter.getItem(position);
        hs.insertTextEditText(etText, item.bbcode + " ");
//        ((QuoteEditText) etText.getTag()).setText(etText.getText().toString());
    }
}
