package org.shikimori.library.features.comminity;

import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.tool.controllers.BaseApiController;

/**
 * Created by Владимир on 19.10.2015.
 */
public class ApiClubs extends BaseApiController<ApiClubs> {
    public ApiClubs(Query query) {
        super(query);
    }

    public void join(String idClub){
        jv(idClub, ShikiPath.Prefix.JOIN);
    }

    public void leave(String idClub){
        jv(idClub, ShikiPath.Prefix.LEAVE);
    }

    private void jv(String id, String join){
        query.in(ShikiPath.CLUB, id, join)
                .setMethod(BaseQuery.METHOD.POST);

        send(null);
    }
}
