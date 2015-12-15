package org.shikimori.library.fragments.base.abstracts.recycleview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import org.shikimori.library.fragments.base.abstracts.OnBaseListListener;

/**
 * Created by Владимир on 22.09.2015.
 */
public abstract class BaseRecycleViewGridFragment extends BaseRecycleViewFragment {

    public int getColumnCount(){
        return 2;
    }

//    GridLayoutManager.SpanSizeLookup lookupSize = new GridLayoutManager.SpanSizeLookup() {
//        public int getSpanSize(int position) {
//            if(BaseRecycleViewGridFragment.this.getAdapter() == null)
//                return -1;
//            switch(BaseRecycleViewGridFragment.this.getAdapter().getItemViewType(position)) {
//                case 111:
//                case 222:
//                    return 2;
//                case 333:
//                    return 1;
//                default:
//                    return -1;
//            }
//        }
//    };

    public LinearLayoutManager getLayoutManager() {
        GridLayoutManager ml = new GridLayoutManager(this.activity, this.getColumnCount());
        ml.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int position) {
                if(BaseRecycleViewGridFragment.this.getAdapter() == null)
                    return 1;
                switch(BaseRecycleViewGridFragment.this.getAdapter().getItemViewType(position)) {
                    case 111:
                    case 222:
                        return 2;
                    case 333:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        return ml;
    }
}
