package org.shikimori.library.features.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import org.shikimori.library.R;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.parser.elements.PostImage;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.tools.h;
import ru.altarix.basekit.library.tools.pagecontroller.Page;

/**
 * Created by Владимир on 22.11.2016.
 */

@Page(key1 = ProfileAboutFragment.HTML)
public class ProfileAboutFragment extends BaseFragment<BaseKitActivity<ShikiAC>> {

    public static final String HTML = "html";

    private ViewGroup aboutHtml;

    @Override
    public int getActionBarTitle() {
        return R.string.about_me;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile_about;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aboutHtml = find(view, R.id.aboutHtml);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity.getLoaderController().show();
        loadHtml((String) getParam(HTML));
    }

    public void loadHtml(String test) {
        if(TextUtils.isEmpty(test)) {
            activity.getLoaderController().hide();
            h.showMsg(activity, R.string.about_me_not_set);
            return;
        }
        BodyBuild builder = new BodyBuild(activity);
        builder.setOnImageClickListener(new BodyBuild.ImageClickListener() {
            @Override
            public void imageClick(PostImage image) {
                activity.getAC().getThumbToImage().zoom(image.getImage(), ProjectTool.fixUrl(image.getImageData().getOriginal()));
            }
        });

        builder.setClickType(BodyBuild.CLICKABLETYPE.INTEXT);
        builder.parceAsync(test, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                if(activity == null)
                    return;
                activity.getLoaderController().hide();
                aboutHtml.removeAllViews();
                aboutHtml.addView(view);
            }
        });

    }
}
