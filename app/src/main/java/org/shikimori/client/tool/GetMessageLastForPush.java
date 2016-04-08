package org.shikimori.client.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.shikimori.client.ShikiApplikation;
import org.shikimori.library.features.profile.model.ItemDialogs;
import org.shikimori.library.loaders.QueryShiki;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ProjectTool;
import org.shikimori.library.tool.ShikiUser;
import org.shikimori.library.tool.controllers.api.ApiMessageController;
import org.shikimori.library.tool.push.PushHelperReceiver;

/**
 * Created by Владимир on 26.11.2015.
 */
public class GetMessageLastForPush {
    public static void notifyMessage(final QueryShiki query) {
        new ApiMessageController(query).getLastDialog(new BaseQuery.OnQuerySuccessListener<ShikiStatusResult>() {
            @Override
            public void onQuerySuccess(ShikiStatusResult res) {

                JSONArray rezult = res.getResultArray();
                if(rezult!=null){
                    final ItemDialogs item = new ItemDialogs().create(rezult.optJSONObject(0));
                    notifyFrom(query.getContext(), item);
                }
            }
        });
    }

    public static void notifyFrom(final Context context, final ItemDialogs item){
        if(item.message!=null && item.message.from!=null){
            if(ShikiUser.USER_ID.equals(item.message.from.id))
                return;
        }
        // load user avatar
        if(item.user!=null){
            notifyFrom(context, item.user.nickname, item.message.body, item.user.img148);
        }
    }

    public static void notifyFrom(final Context context, final String nick, final String text, String imgUrl){
        if(!TextUtils.isEmpty(imgUrl)) {
            ImageLoader.getInstance().loadImage(ProjectTool.fixUrl(imgUrl),
                    new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            // send notify
                            sendMessage(context, nick, text, loadedImage);
                        }

                    });
        } else {
            sendMessage(context, nick, text, null);
        }
    }

    private static void sendMessage(final Context context, String nick, String text, Bitmap bitmap){
        Bundle b = new Bundle();
        b.putString(PushHelperReceiver.MSG_TITLE, nick);
        b.putString(PushHelperReceiver.MSG_BODY, text);
        b.putParcelable(PushHelperReceiver.PushAction.LARGE_ICON, bitmap);
        PushHelperReceiver.notifyUser(
                context,
                PushHelperReceiver.getAction(ShikiApplikation.PRIVATE),
                b);
    }
}
