package org.shikimori.library.custom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gars.emoji.library.PopupEmoji;
import com.gars.emoji.library.listeners.OnEmojiKeyboardListener;
import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.activity.AddItemActivity;
import org.shikimori.library.custom.emoji.EmojiView;
import org.shikimori.library.loaders.ShikiPath;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.Query;
import org.shikimori.library.loaders.httpquery.MyStatusResult;
import org.shikimori.library.tool.ImageCreator;
import org.shikimori.library.tool.UpdateApp;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.SendMessageController;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.permission.BasePermissionController;
import org.shikimori.library.tool.permission.PermissionSontroller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ru.altarix.basekit.library.activity.BaseKitActivity;
import ru.altarix.basekit.library.tools.DialogCompat;
import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 11.10.2015.
 */
public class EditTextSender extends FrameLayout implements View.OnClickListener, OnEmojiKeyboardListener {

    public static final int ADD_ELEMENT_CODE = 957;
    Query query;
    EditText etText;
    private View ivSend,ivAddAnime,ivAddImage;
    ImageCreator imageCreator;
    private SendMessageController.Type type;
    private PermissionSontroller permission;
    private ProgressBar pbLoaderImage;
    private View ivSmails;
    private PopupEmoji emoji;

    public EditTextSender(Context context) {
        this(context, null);
    }

    public EditTextSender(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextSender(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditTextSender(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setType(SendMessageController.Type type){
        this.type = type;
    }

    private void init() {

        imageCreator = new ImageCreator((Activity) getContext());
        permission = new PermissionSontroller((AppCompatActivity) getContext());
        query = new Query(getContext());

        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_text_sender, null);

        etText = h.find(v, R.id.etText);
        ivSend = h.find(v, R.id.ivSend);
        ivSmails = h.find(v, R.id.ivSmails);
        pbLoaderImage = h.find(v, R.id.pbLoaderImage);
        ivAddAnime = h.find(v, R.id.ivAddAnime);
        ivAddImage = h.find(v, R.id.ivAddImage);

        ivAddAnime.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);
        ivSmails.setOnClickListener(this);
        initEmoji();
        addView(v);
    }

    public EditText getEtText() {
        return etText;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivAddAnime){
//            i = new Intent(getContext(), )
//            ((Activity)getContext()).startActivityForResult();
            new DialogCompat((Activity) getContext())
                    .setTitle(R.string.add_title)
                    .getDialog()
                    .setItems(R.array.select_for_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getContext(), AddItemActivity.class);
                            switch (which) {
                                case 0:  i.putExtra(Constants.TYPE, Constants.ANIME); break;
                                case 1:  i.putExtra(Constants.TYPE, Constants.MANGA); break;
                                case 2:  i.putExtra(Constants.TYPE, Constants.CHARACTER); break;
                            }

                            ((Activity) getContext()).startActivityForResult(i, ADD_ELEMENT_CODE);
                        }
                    }).show();

        } else if (v.getId() == R.id.ivAddImage){
            imageCreator.addPhoto();
        } else if (v.getId() == R.id.ivSmails){
            if(Build.VERSION.SDK_INT > 10)
                emoji.show(!emoji.isShowing());
            else {
                h.showMsg(getContext(), R.string.not_for_this_sdk);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ADD_ELEMENT_CODE && resultCode == Activity.RESULT_OK){
            String itemId = data.getStringExtra(Constants.ITEM_ID);
            String type = data.getStringExtra(Constants.TYPE);
            StringBuilder str = new StringBuilder();
            str.append("[").append(type).append("=").append(itemId).append("]")
                    .append(data.getStringExtra(Constants.ITEM_NAME))
                    .append("[/").append(type).append("]");
            hs.insertTextEditText(etText, str.toString());
        }
        final String patch = imageCreator.getPathFromActivityResult(requestCode, resultCode, data);
        if(patch != null){

            query.in(ShikiPath.USER_IMAGES + "?linked_type=Comment")
                    .setMethod(BaseQuery.METHOD.POST);

//            if(type == SendMessageController.Type.COMMENT)
//                query.addParam("linked_type", "Comment");
//            else
//                query.addParam("linked_type", "message");

            if(!patch.startsWith("http")){
                uploadImage(patch);
            } else {
                permission.checkPermission(PermissionSontroller.WRITE_EXTERNAL_STORAGE, new BasePermissionController.OnRequestPermission() {
                    @Override
                    public void requestDone(boolean allow) {
                        if(allow){
                            String fileName = h.md5(patch);
                            new UpdateApp(getContext(), fileName)
                                    .setProgresListener(new UpdateApp.UpdateApkProgressListener() {
                                        @Override
                                        public void update(int progress) {

                                        }

                                        @Override
                                        public void finish(String url) {
                                            uploadImage(url);
                                        }
                                    })
                                    .startLoad(patch);
                        } else {
                            h.showMsg(getContext(), R.string.access_sd);
                        }
                    }
                });
            }
        }
    }

    private void initEmoji(){
        if(Build.VERSION.SDK_INT > 10){
            emoji = new PopupEmoji((FragmentActivity) getContext(), etText);
            emoji.setOnEmojiKeyboardListener(this);
            List<View> pages = new ArrayList<>();
            pages.add(new EmojiView(getContext(), query, etText, (ImageView) ivSmails));
            emoji.setPages(pages);
        }
    }

    private void uploadImage(String patch){
        try {
            Log.d("file" , patch);
            RequestParams params = query.getParams();
            params.put("image", new File(patch), "image/*");
            query.setErrorListener(new BaseQuery.OnQueryErrorListener<MyStatusResult>() {
                @Override
                public void onQueryError(MyStatusResult res) {
                    h.showMsg(getContext(), R.string.error_add_image);
                    h.setVisibleGone(pbLoaderImage);
                }
            });
            h.setVisible(pbLoaderImage);
            query.getResultObject(new BaseQuery.OnQuerySuccessListener<MyStatusResult>() {
                @Override
                public void onQuerySuccess(MyStatusResult res) {
                    hs.insertTextEditText(etText, res.getParameter("bbcode"));
                    h.setVisibleGone(pbLoaderImage);
                }
            }, progressListener);
        } catch (FileNotFoundException e) {
            h.showMsg(getContext(), R.string.error_add_image);
        }
    }

    BaseQuery.OnProgressListener progressListener = new BaseQuery.OnProgressListener() {
        @Override
        public void progress(int progress) {
            pbLoaderImage.setProgress(progress);
        }
    };

    public View getIvSend() {
        return ivSend;
    }

    enum TABS_STATE {
        NONE, VISIBLE, HIDDEN
    }
    TABS_STATE tabState;
    @Override
    public void onOpenKeyboard(boolean b) {
        if(getContext() instanceof BaseKitActivity){
            if(tabState == null){
                tabState = ((BaseKitActivity) getContext()).isTabsVisible()?
                        TABS_STATE.VISIBLE :
                        TABS_STATE.HIDDEN
                ;
            }
            if(tabState == TABS_STATE.VISIBLE){
                ((BaseKitActivity) getContext()).showTabs(b);
            }
        }
    }
}
