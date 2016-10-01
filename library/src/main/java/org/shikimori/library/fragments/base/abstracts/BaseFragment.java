package org.shikimori.library.fragments.base.abstracts;

import android.support.v7.app.AppCompatActivity;

import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.controllers.ShikiFC;

import ru.altarix.basekit.library.activities.BaseKitActivity;
import ru.altarix.basekit.library.fragment.BaseKitFragment;

/**
 * Created by Владимир on 02.07.2014.
 */
public class BaseFragment<T extends AppCompatActivity> extends BaseKitFragment<T, ShikiFC> {

    @Override
    public ShikiFC initFragmentController() {
        return new ShikiFC(this, ((BaseKitActivity<ShikiAC>) activity).getAC());
    }

}
