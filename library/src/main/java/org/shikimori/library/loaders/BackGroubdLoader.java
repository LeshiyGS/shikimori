package org.shikimori.library.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.shikimori.library.interfaces.OnViewBuildLister;
import org.shikimori.library.objects.abs.ObjectBuilder;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

public class BackGroubdLoader<T extends OnViewBuildLister> extends AsyncTaskLoader<List<T>> {

    private BodyBuild bodyBuilder;
    private JSONArray array;
    private Class<T> tClass;

    public BackGroubdLoader(Context context, BodyBuild bodyBuilder, JSONArray array, Class<T> tClass) {
        super(context);
        this.bodyBuilder = bodyBuilder;
        this.array = array;
        this.tClass = tClass;
    }

    @Override
    public List<T> loadInBackground() {
        ObjectBuilder builder = new ObjectBuilder(array, tClass,
                new ObjectBuilder.AdvanceCheck<T>() {
                    @Override
                    public boolean check(T item, int position) {
                        LinearLayout body = new LinearLayout(getContext());
                        body.setLayoutParams(h.getDefaultParams());
                        body.setOrientation(LinearLayout.VERTICAL);
                        item.setBuildView(body);
                        bodyBuilder.parce(item.getHtml(), body);
                        return false;
                    }
                }
        );
        return builder.list;
    }

    @Override
    public void deliverResult(List<T> data) {
        super.deliverResult(data);
    }
}