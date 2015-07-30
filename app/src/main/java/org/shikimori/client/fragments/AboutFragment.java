package org.shikimori.client.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.shikimori.client.BuildConfig;
import org.shikimori.client.R;
import org.shikimori.client.tool.UpdateApp;
import org.shikimori.library.fragments.base.abstracts.BaseFragment;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.controllers.ShikiAC;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.h;

/**
 * Created by Владимир on 30.07.2015.
 */
public class AboutFragment extends BaseFragment<BaseKitActivity<ShikiAC>> implements View.OnClickListener, Query.OnQuerySuccessListener, UpdateApp.UpdateApkProgressListener {

    TextView tvVersion,tvNewVersion;
    ProgressBar pbLoaderApk;
    Button bLoadApk;
    private String versionUrl;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shiki_about, null);
        setBaseView(v);

        tvVersion = find(R.id.tvVersion);
        tvNewVersion = find(R.id.tvNewVersion);
        pbLoaderApk = find(R.id.pbLoaderApk);
        bLoadApk = find(R.id.bLoadApk);

        bLoadApk.setOnClickListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvVersion.setText(activity.getString(R.string.app_version_template, BuildConfig.VERSION_NAME));
        checkVersion();
    }

    private void checkVersion() {
        if (ProjectTool.isFullVersion()) {
            getFC().getQuery().init("http://anibreak.ru/v.0.3/get/shiki/version")
                   .setCache(true)
                   .getResultObject(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bLoadApk) {
            if(versionUrl == null){
                Crouton.showText(activity, R.string.url_apk_notfound, Style.ALERT);
                return;
            }
            h.setVisible(pbLoaderApk);
            h.setVisibleGone(bLoadApk);

            UpdateApp lod = new UpdateApp(activity);
            lod.setProgresListener(this);

            if(Build.VERSION.SDK_INT > 10){
                lod.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, versionUrl);
            } else
                lod.execute(versionUrl);
        }
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        int newVersion = res.getParameterInt("version");
        versionUrl = res.getParameter("url");
        if(newVersion > BuildConfig.VERSION_CODE){
            h.setVisible(bLoadApk,tvNewVersion);
            tvNewVersion.setText(activity.getString(R.string.app_new_version_template, res.getParameter("version_name")));
        }
    }

    @Override
    public void update(int progress) {
        pbLoaderApk.setProgress(progress);
    }

    @Override
    public void finish() {
        h.setVisibleGone(pbLoaderApk);
    }
}
