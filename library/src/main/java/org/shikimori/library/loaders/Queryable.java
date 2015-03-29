package org.shikimori.library.loaders;

import org.shikimori.library.loaders.httpquery.Query;

public interface Queryable {

    /**
     * @param separate - нужна ли в итоге выделенная очередь.*/
    Query prepareQuery(boolean separate);
}
