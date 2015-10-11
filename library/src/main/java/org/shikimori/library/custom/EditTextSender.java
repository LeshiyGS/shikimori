package org.shikimori.library.custom;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.loopj.android.http.RequestParams;

import org.shikimori.library.R;
import org.shikimori.library.activity.AddItemActivity;
import org.shikimori.library.activity.ShowPageActivity;
import org.shikimori.library.loaders.ShikiPath;
import org.shikimori.library.loaders.httpquery.BaseQuery;
import org.shikimori.library.loaders.httpquery.Query;
import org.shikimori.library.loaders.httpquery.StatusResult;
import org.shikimori.library.tool.ImageCreator;
import org.shikimori.library.tool.UpdateApp;
import org.shikimori.library.tool.constpack.Constants;
import org.shikimori.library.tool.controllers.SendMessageController;
import org.shikimori.library.tool.hs;
import org.shikimori.library.tool.permission.BasePermissionController;
import org.shikimori.library.tool.permission.PermissionSontroller;

import java.io.File;
import java.io.FileNotFoundException;

import ru.altarix.basekit.library.tools.DialogCompat;
import ru.altarix.basekit.library.tools.h;

/**
 * Created by Феофилактов on 11.10.2015.
 */
public class EditTextSender extends FrameLayout implements View.OnClickListener {

    public static final int ADD_ELEMENT_CODE = 957;
    Query query;
    EditText etText;
    private View ivSend,ivAddAnime,ivAddImage;
    ImageCreator imageCreator;
    private SendMessageController.Type type;
    private PermissionSontroller permission;
    private ProgressBar pbLoaderImage;

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

    public void setQuery(Query query){
        this.query = query;
    }

    public void setType(SendMessageController.Type type){
        this.type = type;
    }

    private void init() {

        imageCreator = new ImageCreator((Activity) getContext());
        permission = new PermissionSontroller((AppCompatActivity) getContext());


        View v = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_text_sender, null);

        etText = h.find(v, R.id.etText);
        ivSend = h.find(v, R.id.ivSend);
        pbLoaderImage = h.find(v, R.id.pbLoaderImage);
        ivAddAnime = h.find(v, R.id.ivAddAnime);
        ivAddImage = h.find(v, R.id.ivAddImage);

        ivAddAnime.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);

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

            query.in(ShikiPath.USER_IMAGES)
                 .setMethod(BaseQuery.METHOD.POST)
                 .addParam("linked_type", type);

            if(type == SendMessageController.Type.COMMENT)
                query.addParam("linked_type", "comment");
            else
                query.addParam("linked_type", "message");

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

    private void uploadImage(String patch){
        try {
            RequestParams params = query.getParams();
            params.put("image", new File(patch));
            query.addUpdateListener(progressListener);
            h.setVisible(pbLoaderImage);
            query.getResultObject(new BaseQuery.OnQuerySuccessListener() {
                @Override
                public void onQuerySuccess(StatusResult res) {
                    hs.insertTextEditText(etText, res.getParameter("bbcode"));
                    query.removeProgressListener(progressListener);
                    h.setVisibleGone(pbLoaderImage);
                }
            });
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
}