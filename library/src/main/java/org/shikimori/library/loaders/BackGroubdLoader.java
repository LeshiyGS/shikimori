package org.shikimori.library.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.shikimori.library.interfaces.OnAdvancedCheck;
import org.shikimori.library.interfaces.OnViewBuildLister;

import ru.altarix.basekit.library.tools.objBuilder.JsonParseable;
import ru.altarix.basekit.library.tools.objBuilder.ObjectBuilder;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import java.util.List;

public class BackGroubdLoader<T extends JsonParseable> extends AsyncTaskLoader<List<T>> {

    private BodyBuild bodyBuilder;
    private int maxLenght;
    private JSONArray array;
    private Class<T> tClass;
    private OnAdvancedCheck listener;
    ObjectBuilder builder = new ObjectBuilder();

    public BackGroubdLoader(Context context, BodyBuild bodyBuilder, JSONArray array, Class<T> tClass) {
        this(context, bodyBuilder, 0, array, tClass);
    }

    public BackGroubdLoader(Context context, BodyBuild bodyBuilder, int maxLenght, JSONArray array, Class<T> tClass) {
        super(context);
        this.bodyBuilder = bodyBuilder;
        this.maxLenght = maxLenght;
        this.array = array;
        this.tClass = tClass;
    }

    public void setAdvancedCheck(OnAdvancedCheck listener){
        this.listener = listener;
    }

    @Override
    public List<T> loadInBackground() {
        return builder.getDataList(array, tClass,
                new ObjectBuilder.AdvanceChecker<T>() {
                    @Override
                    public boolean check(T item, int position) {
                        if (onAdvancesCheck(item, position))
                            return false;
                        LinearLayout body = new LinearLayout(getContext());
                        body.setLayoutParams(hs.getDefaultParams());
                        body.setOrientation(LinearLayout.VERTICAL);

                        ((OnViewBuildLister)item).setBuildView(body);
                        bodyBuilder.parce(((OnViewBuildLister)item).getHtml(), body, maxLenght);
                        return false;
                    }
                }
        );
    }

    @Override
    public void deliverResult(List<T> data) {
        super.deliverResult(data);
    }

    public boolean onAdvancesCheck(T item, int position){
        if(listener!=null)
            return listener.ckeck(item, position);
        return false;
    }
}