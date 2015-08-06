package org.shikimori.library.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.shikimori.library.R;
import org.shikimori.library.activity.BaseActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.adapters.AniHistoryAdapter;
import org.shikimori.library.custom.ExpandableHeightGridView;
import org.shikimori.library.interfaces.ExtraLoadInterface;
import org.shikimori.library.loaders.ShikiApi;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.objects.one.AMShiki;
import org.shikimori.library.objects.one.ItemCharacter;
import org.shikimori.library.pull.PullableFragment;
import org.shikimori.library.tool.LinkHelper;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiImage;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.ShikiAC;
import org.shikimori.library.tool.h;
import org.shikimori.library.tool.parser.jsop.BodyBuild;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.ui.CustomTextView;

/**
 * Created by Владимир on 17.04.2015.
 */
public class CharacterDetailsFragment extends PullableFragment<BaseKitActivity<ShikiAC>> implements Query.OnQuerySuccessListener, View.OnClickListener {

    TextView tvTitle;
    ImageView ivPoster;
    ViewGroup llInfo,tvReview;
    View tvAnimes, tvMangas;
    ScrollView svMain;
    ExpandableHeightGridView pageAnime, pageManga;
    private String itemId;
    private ItemCharacter item;
    private BodyBuild bodyBuilder;

    public static CharacterDetailsFragment newInstance(Bundle b) {
        CharacterDetailsFragment frag = new CharacterDetailsFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_shiki_deatales_character, null);
        setBaseView(v);
        svMain   = find(R.id.svMain);
        tvTitle   = find(R.id.tvTitle);
        tvReview  = find(R.id.llReview);
        ivPoster  = find(R.id.ivPoster);
        llInfo    = find(R.id.llInfo);
        tvAnimes  = find(R.id.tvAnimes);
        tvMangas  = find(R.id.tvMangas);
        pageAnime = find(R.id.pageAnime);
        pageManga = find(R.id.pageManga);

        ivPoster.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initArgiments();
        showRefreshLoader();
        bodyBuilder = ProjectTool.getBodyBuilder(activity, BodyBuild.CLICKABLETYPE.INTEXT);
        loadData();
    }

    String getUrl(){
        return ShikiApi.getUrl(ShikiPath.CHARACTER_ID) + itemId;
    }

    @Override
    public void onStartRefresh() {
        getFC().getQuery().invalidateCache(getUrl());
        loadData();
    }

    void loadData(){
        getFC().getQuery().init(getUrl())
                .setCache(true, Query.HOUR)
                .getResult(this);
    }

    private void initArgiments() {
        Bundle b = getArguments();
        if(b == null)
            return;

        itemId = b.getString(Constants.ITEM_ID);
    }

    @Override
    public int pullableViewId() {
        return R.id.svMain;
    }

    @Override
    public void onQuerySuccess(StatusResult res) {
        stopRefresh();
        llInfo.removeAllViews();
        item = new ItemCharacter().create(res.getResultObject());

//        tvTitle.setText(ProjectTool.getTitleElement(item.russian, item.name, "#d9000000"));

        addInfo(R.string.name, item.russian);
        if(!TextUtils.isEmpty(item.altname))
            addInfo(R.string.altname, item.altname);
        if(!TextUtils.isEmpty(item.japanese))
            addInfo(R.string.japanase_name, item.japanese);

        bodyBuilder.parceAsync(item.descriptionHtml, new BodyBuild.ParceDoneListener() {
            @Override
            public void done(ViewGroup view) {
                if (activity == null || getView() == null)
                    return;
                tvReview.removeAllViews();
                tvReview.addView(view);
                bodyBuilder.loadPreparedImages();
            }
        });
        if(item.image!=null)
            ShikiImage.show(item.image.original, ivPoster);

        showHistory(item.animes.size() != 0, tvAnimes, pageAnime);
        showHistory(item.mangas.size() != 0, tvMangas, pageManga);

        pageAnime.setAdapter(new AniHistoryAdapter(activity, item.animes));
        pageManga.setAdapter(new AniHistoryAdapter(activity, item.mangas));
        pageAnime.setOnItemClickListener(goToUrl);
        pageManga.setOnItemClickListener(goToUrl);
        if(getView()!=null)
            getView().post(new Runnable() {
                @Override
                public void run() {
                    svMain.scrollTo(0,0);
                }
            });

        if (activity instanceof ExtraLoadInterface)
            ((ExtraLoadInterface) activity).extraLoad(item.threadId);
    }

    /**
     * Постраничный вывод манги или аниме с картинками и названием
     * @param items
     * @param type
     */

    AdapterView.OnItemClickListener goToUrl = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AMShiki _item = (AMShiki) parent.getAdapter().getItem(position);
            LinkHelper.goToUrl(activity, _item.url);
        }
    };

    /**
     * Открываем карточку с мангой или аниме
     * @param item
     * @param type
     */
    void showDetails(AMShiki item, ProjectTool.TYPE type){
        Intent i = new Intent(activity, ShowPageActivity.class);
        i.putExtra(Constants.ITEM_ID, item.id);
        if (type == ProjectTool.TYPE.ANIME) {
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.ANIME_PAGE);
        } else if (type == ProjectTool.TYPE.MANGA){
            i.putExtra(Constants.PAGE_FRAGMENT, ShowPageActivity.MANGA_PAGE);
        }
        activity.startActivity(i);
    }

    /**
     * Показываем или скрываем ссылки на аниме и мангу
     * @param show
     * @param title
     * @param pages
     */
    void showHistory(boolean show, View title, View pages){
        if(!show){
            h.setVisibleGone(title);
            h.setVisibleGone(pages);
        } else {
            h.setVisible(title, true);
            h.setVisible(pages, true);
        }
    }

    // инфа справа от фото
    protected void addInfo(int label,  String text) {
        CustomTextView row = new CustomTextView(activity);
        row.setLabel(label);
        row.setText(text);
        llInfo.addView(row);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivPoster){
            if(item.image!=null)
                activity.getAC().getThumbToImage().zoom(ivPoster, ProjectTool.fixUrl(item.image.original));
        }
    }
}
