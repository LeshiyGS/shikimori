package org.shikimori.library.tool;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.shikimori.library.fragments.base.abstracts.OnBaseListListener;
import org.shikimori.library.interfaces.OnAdvancedCheck;
import org.shikimori.library.loaders.BackGroubdLoader;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;

/**
 * Created by Владимир on 24.09.2015.
 */
public class LoadAsyncBuildHelper {

    private Context context;
    private OnBaseListListener baseListener;

    public LoadAsyncBuildHelper(Context context, OnBaseListListener listener){
        this.context = context;
        this.baseListener = listener;
    }

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, Class<? extends JsonParseable> cl) {
        loadAsyncBuild(bodyBuild, array, 0, cl, null);
    }

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, int maxLenght, Class<? extends JsonParseable> cl) {
        loadAsyncBuild(bodyBuild, array, maxLenght, cl, null);
    }

    public void loadAsyncBuild(final BodyBuild bodyBuild, JSONArray array, int maxLenght, Class<? extends JsonParseable> cl, OnAdvancedCheck listener) {
        if (context == null)
            return;
        BackGroubdLoader<JsonParseable> backBuilder = new BackGroubdLoader<JsonParseable>(context, bodyBuild, maxLenght, array, (Class<JsonParseable>) cl) {
            @Override
            public void deliverResult(List data) {
                if (context == null)
                    return;
                super.deliverResult(data);
                baseListener.stopRefresh();
                baseListener.prepareData(data, true, true);
                bodyBuild.loadPreparedImages();
            }
        };
        backBuilder.setAdvancedCheck(listener);
        backBuilder.forceLoad();
    }
}
