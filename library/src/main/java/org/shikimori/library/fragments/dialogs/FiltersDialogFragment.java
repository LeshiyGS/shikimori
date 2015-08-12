package org.shikimori.library.fragments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.custom.CustomCheckBoxFilter;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.hs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Феофилактов on 14.07.2015.
 */
public class FiltersDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    ViewGroup llContainer;
    private View bFilter,bClear;
    private FilterController controller;
    private OnFilterListener onFilterListener;
    private String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment_filters, null);
        llContainer = hs.get(v, R.id.llContainer);
        bFilter = hs.get(v, R.id.bFilter);
        bClear = hs.get(v, R.id.bClear);

        bFilter.setOnClickListener(this);
        bClear.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(controller == null)
            controller = new FilterController(getActivity(), type);
        fillViews();
    }

    void fillViews(){
        llContainer.removeAllViews();
        // status
        addViewList(R.string.status_title, controller.getStatusList());
        // type
        addViewList(R.string.type, controller.getTypeList());
        // my list
        addViewList(R.string.list, controller.getMyList());
        if(Constants.ANIME.equals(type)){
            // Duration
            addViewList(R.string.episod_title, controller.getDurationList());
            // rating
            addViewList(R.string.title_rating, controller.getRateList());
        }
        // genres
        addViewList(R.string.title_genres, controller.getGenreList());
        // order list
        CustomCheckBoxFilter box = addViewList(R.string.title_order, controller.getOrderList());
        box.setNagativeStatus(false);
        box.setMultyChoise(false);
    }

    CustomCheckBoxFilter addViewList(int title, List<CustomCheckBoxFilter.Box> list){
        CustomCheckBoxFilter box = new CustomCheckBoxFilter(getActivity());
        box.setTitle(title);
        box.setList(list);
        llContainer.addView(box);
        return box;
    }

    public FilterController getController() {
        return controller;
    }

    public void setFilterController(FilterController controller){
        this.controller = controller;
    }

    public void setOnFilterListener(OnFilterListener onFilterListener){
        this.onFilterListener = onFilterListener;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bFilter){
            if(onFilterListener!=null)
                onFilterListener.filtered(controller);
            dismiss();
        } else if (v.getId() == R.id.bClear){
            clear();
            if(onFilterListener!=null)
                onFilterListener.filtered(controller);
        }
    }

    private void clear() {
        clear(controller.getStatusList());
        clear(controller.getMyList());
        clear(controller.getDurationList());
        clear(controller.getTypeList());
        clear(controller.getRateList());
        clear(controller.getOrderList());
        clear(controller.getGenreList());
        fillViews();
    }
    private void clear(List<CustomCheckBoxFilter.Box> list) {
        if(list == null)
            return;
        for (CustomCheckBoxFilter.Box box  : list) {
            box.setStatus(0);
        }
    }

    public static FiltersDialogFragment newInstance() {
        return new FiltersDialogFragment();
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class FilterController{

        List<CustomCheckBoxFilter.Box> statusList, myList, durationList,typeList,rateList,orderList, genreList;
        private Context context;
        private String type;

        public FilterController(Context context, String type){
            this.context = context;
            this.type = type;
        }

        String getString(int title){
            return context.getString(title);
        }

        boolean isAnime(){
            return type.equals(Constants.ANIME);
        }

        public List<CustomCheckBoxFilter.Box> getStatusList() {
            if(statusList == null){
                statusList = new ArrayList<>();
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.annoning), "status", "anons"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.now_going), "status", "ongoing"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.done), "status", "released"));
                statusList.add(new CustomCheckBoxFilter.Box(getString(R.string.just_done), "status", "latest"));
            }
            return statusList;
        }
        public List<CustomCheckBoxFilter.Box> getTypeList() {
            if(typeList == null){
                typeList = new ArrayList<>();

                if(isAnime()){
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv), "type", "tv"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_short), "type", "tv_13"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_middle), "type", "tv_24"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.tv_long), "type", "tv_48"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.movies), "type", "movie"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.ova), "type", "ova"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.ona), "type", "ona"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.special), "type", "special"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.music), "type", "music"));
                } else {
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.doujin), "type", "doujin"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.manga), "type", "manga"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.manhua), "type", "manhua"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.manhwa), "type", "manhwa"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.novel), "type", "novel"));
                    typeList.add(new CustomCheckBoxFilter.Box(getString(R.string.one_shot), "type", "one_shot"));
                }

            }
            return typeList;
        }
        public List<CustomCheckBoxFilter.Box> getMyList() {
            if(myList == null){
                myList = new ArrayList<>();
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.planned), "mylist", "0"));
                myList.add(new CustomCheckBoxFilter.Box(getString(isAnime() ? R.string.watching : R.string.watchingmanga), "mylist", "1"));
                myList.add(new CustomCheckBoxFilter.Box(getString(isAnime() ? R.string.completed: R.string.completedmanga), "mylist", "2"));
                myList.add(new CustomCheckBoxFilter.Box(getString(isAnime() ? R.string.rewatching:R.string.rewatchingmanga ), "mylist", "9"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.on_hold), "mylist", "3"));
                myList.add(new CustomCheckBoxFilter.Box(getString(R.string.dropped), "mylist", "4"));
            }
            return myList;
        }
        public List<CustomCheckBoxFilter.Box> getDurationList() {
            if(durationList == null){
                durationList = new ArrayList<>();
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.minute_10), "duration", "S"));
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.minute_30), "duration", "D"));
                durationList.add(new CustomCheckBoxFilter.Box(getString(R.string.after_30), "duration", "F"));
            }
            return durationList;
        }
        public List<CustomCheckBoxFilter.Box> getRateList() {
            if(rateList == null){
                rateList = new ArrayList<>();
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_g), "rating", "g"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_pg), "rating", "pg"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_pg_13), "rating", "pg_13"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_r), "rating", "r"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_r_plus), "rating", "r_plus"));
                rateList.add(new CustomCheckBoxFilter.Box(getString(R.string.rating_rx), "rating", "rx"));
            }
            return rateList;
        }

        public List<CustomCheckBoxFilter.Box> getOrderList(){
            if(orderList == null){
                orderList = new ArrayList<>();
                orderList.add(new CustomCheckBoxFilter.Box(getString(R.string.sоrt_rating), "order-by", "ranked"));
                orderList.add(new CustomCheckBoxFilter.Box(getString(R.string.sоrt_populating), "order-by", "popularity"));
                orderList.add(new CustomCheckBoxFilter.Box(getString(R.string.sоrt_name), "order-by", "name"));
                orderList.add(new CustomCheckBoxFilter.Box(getString(R.string.sоrt_aired_on), "order-by", "aired_on"));
                orderList.add(new CustomCheckBoxFilter.Box(getString(R.string.sоrt_id), "order-by", "id"));
            }
            return orderList;
        }

        public List<CustomCheckBoxFilter.Box> getGenreList(){
            if(genreList == null){
                genreList = new ArrayList<>();
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_shounen), "genre", "27-Shounen"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_shounen_ai), "genre", "28-Shounen-Ai"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_seinen), "genre", "42-Seinen"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_shoujo), "genre", "25-Shoujo"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_shoujo_ai), "genre", "26-Shoujo-Ai"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_josei), "genre", "43-Josei"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_comedy), "genre", "4-Comedy"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_romance), "genre", "22-Romance"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_school), "genre", "23-School"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_dementia), "genre", "5-Dementia"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_martial_arts), "genre", "17-Martial-Arts"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_vampire), "genre", "32-Vampire"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_military), "genre", "38-Military"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_harem), "genre", "35-Harem"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_demons), "genre", "6-Demons"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_kids), "genre", "15-Kids"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_drama), "genre", "8-Drama"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_game), "genre", "11-Game"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_historical), "genre", "13-Historical"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_space), "genre", "29-Space"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_magic), "genre", "16-Magic"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_cars), "genre", "3-Cars"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_mecha), "genre", "18-Mecha"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_mystery), "genre", "7-Mystery"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_music), "genre", "19-Music"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_parody), "genre", "20-Parody"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_slice_of_life), "genre", "36-Slice-of-Life"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_police), "genre", "39-Police"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_adventure), "genre", "2-Adventure"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_psychological), "genre", "40-Psychological"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_samurai), "genre", "21-Samurai"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_supernatural), "genre", "37-Supernatural"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_gender_bender), "genre", "44-Gender-Bender"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_sports), "genre", "30-Sports"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_super_power), "genre", "31-Super-Power"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_horror), "genre", "14-Horror"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_sci_fi), "genre", "24-Sci-Fi"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_fantasy), "genre", "10-Fantasy"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_action), "genre", "1-Action"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_ecchi), "genre", "9-Ecchi"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_thriller), "genre", "41-Thriller"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_hentai), "genre", "12-Hentai"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_yaoi), "genre", "33-Yaoi"));
                genreList.add(new CustomCheckBoxFilter.Box(getString(R.string.sort_yuri), "genre", "34-Yuri"));
            }
            return genreList;
        }

        ///api/animes?duration=F&genre=3&limit=1&mylist=1&order=ranked
        // &page=1&rating=NC-17&search=Te&season=2014&studio=2&type=TV

        public RequestParams getRequestParams(){
            RequestParams params = new RequestParams();
            prepareParam(statusList, "status", params);
            prepareParam(myList, "mylist", params);
            prepareParam(durationList, "duration", params);
            prepareParam(rateList, "rating", params);
            prepareParam(typeList, "type", params);
            prepareParam(orderList, "order", params);
            prepareParam(genreList, "genre", params);
            return params;
        }

        private void prepareParam(List<CustomCheckBoxFilter.Box> list, String keyParsm, RequestParams params){
            if(list == null)
                return;
            StringBuilder builder = new StringBuilder();
            for (CustomCheckBoxFilter.Box box : list) {
                if(box.getStatus() == 1){
                    if(builder.length() > 0)
                        builder.append(",");
                    builder.append(box.getValue());
                } else if(box.getStatus() == 2){
                    if(builder.length() > 0)
                        builder.append(",");
                    builder.append("!").append(box.getValue());
                }
            }
            if(builder.length() > 0)
                params.put(keyParsm, builder.toString());
        }

    }

    public interface OnFilterListener{
        public void filtered(FilterController controller);
    }

}
