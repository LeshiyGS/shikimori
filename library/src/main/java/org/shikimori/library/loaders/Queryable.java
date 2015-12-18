package org.shikimori.library.loaders;

public interface Queryable {

    /**
     * @param separate - нужна ли в итоге выделенная очередь.*/
    QueryShiki prepareQuery(boolean separate);
}
