package org.shikimori.library.tool.permission;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Владимир on 09.10.2015.
 */
public class PermissionSontroller extends BasePermissionController {
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    public PermissionSontroller(AppCompatActivity activity) {
        super(activity);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
    }
}
