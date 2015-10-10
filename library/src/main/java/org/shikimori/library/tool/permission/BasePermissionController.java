package org.shikimori.library.tool.permission;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Владимир on 09.10.2015.
 */
public class BasePermissionController {
    public static int REQUEST_PERMISSIONS = 777;
    public static int REQUEST_PERMISSION = 7777;
    private AppCompatActivity activity;
    protected static Set<String> permissions = new HashSet<>();
    protected static Set<String> permissionsAllow = new HashSet<>();
    private OnRequestAllPermissions allRequest;
    private OnRequestPermission onePermission;

    public interface OnRequestAllPermissions{
        void requestDone(Set<String> permissionsAllow);
    }

    public interface OnRequestPermission{
        void requestDone(boolean allow);
    }

    public BasePermissionController(AppCompatActivity activity){
        this.activity = activity;
    }

    public static void addPermission(String permission){
        permissions.add(permission);
    }

    /**
     * Request all permissions, after user done, system call method
     * Activity.onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
     * @param allRequest
     */
    public void requestPermissions(OnRequestAllPermissions allRequest){
        if(permissions.size() == 0 || Build.VERSION.SDK_INT < 23){
            allRequest.requestDone(null);
            return;
        }
        this.allRequest = allRequest;
        activity.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
    }

    /**
     * Set this method to Activity inside
     * Activity.onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_PERMISSIONS){
            for (int i = 0; i < permissions.length; i++) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    permissionsAllow.add(permissions[i]);
                else
                    permissionsAllow.remove(permissions[i]);
            }
            if(allRequest !=null)
                allRequest.requestDone(permissionsAllow);
            allRequest = null;
        } else if (requestCode == REQUEST_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                permissionsAllow.add(permissions[0]);
            else
                permissionsAllow.remove(permissions[0]);
            if(onePermission!=null)
                onePermission.requestDone(permissionsAllow.contains(permissions[0]));
            onePermission = null;
        }
    }

    /**
     * Check 1 permission
     * @param permission
     * @param onePermission
     */
    public void checkPermission(String permission, OnRequestPermission onePermission){
        if(Build.VERSION.SDK_INT >= 23){
            this.onePermission = onePermission;
            if(activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED){
                activity.requestPermissions(new String[]{permission}, REQUEST_PERMISSION);
                return;
            }
        }
        onePermission.requestDone(true);

    }

}
