package org.shikimori.client.loaders;

import org.shikimori.client.loaders.httpquery.Query;

public interface Queryable {

    /**
     * @param separate - нужна ли в итоге выделенная очередь.*/
    Query prepareQuery(boolean separate);
}
