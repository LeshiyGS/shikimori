package org.shikimori.library.fragments.base.abstracts;

import java.util.List;

public interface OnBaseListListener {
    void prepareData(List<?> list, boolean removeLastItem, boolean limitOver);

    void stopRefresh();
}