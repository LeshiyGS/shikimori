package org.shikimori.client.tool;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.shikimori.client.ShikiApplikation;
import org.shikimori.library.features.profile.model.ItemDialogs;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.api.ApiMessageController;
import org.shikimori.library.tool.push.PushHelperReceiver;

/**
 * Created by Владимир on 26.11.2015.
 */
public class GetMessageLastForPush {
    public static void notifyMessage(final Query query) {
        new ApiMessageController(query).getLastDialog(new BaseQuery.OnQuerySuccessListener() {
            @Override
            public void onQuerySuccess(StatusResult res) {

                JSONArray rezult = res.getResultArray();
                if(rezult!=null){
                    final ItemDialogs item = new ItemDialogs().create(rezult.optJSONObject(0));

//                     if message from self
                    if(item.message!=null && item.message.from!=null){
                        if(ShikiUser.USER_ID.equals(item.message.from.id))
                            return;
                    }
                    // load user avatar
                    if(item.user!=null && item.user.img148!=null){
                        ImageLoader.getInstance().loadImage(ProjectTool.fixUrl(item.user.img148),
                                new SimpleImageLoadingListener(){
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        // send notify

                                        Bundle b = new Bundle();
                                        b.putString(PushHelperReceiver.MSG_TITLE, item.user.nickname);
                                        b.putString(PushHelperReceiver.MSG_BODY, item.message.body);
                                        b.putParcelable(PushHelperReceiver.PushAction.LARGE_ICON, loadedImage);
                                        PushHelperReceiver.notifyUser(
                                                query.getContext(),
                                                PushHelperReceiver.getAction(ShikiApplikation.NEW_MESSAGES),
                                                b);

//                                        new PushHelperShiki(query.getContext())
//                                                .sendLastMessage(item.user.nickname, item.message.body, loadedImage);
                                    }
                                });
                    }
                }
            }
        });
    }
}
