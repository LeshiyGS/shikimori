package org.shikimori.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.shikimori.client.activity.DrawerActivity;
import org.shikimori.client.adapters.DrawerAdapter;
import org.shikimori.client.gsm.RegistrationIntentService;
import org.shikimori.library.features.profile.InboxFragment2;
import org.shikimori.library.fragments.UserNewsFragment;
import org.shikimori.library.tool.constpack.Constants;

import java.util.List;


/**
 * Created by Владимир on 20.06.2014.
 */
public class MainActivity extends DrawerActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment anotherFrag = getExtraPage();
        if(anotherFrag !=null){
            loadPage(anotherFrag);
            drawerTool.setSelected(DrawerAdapter.NON_SELECTED);
            return;
        }
        loadPage(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        drawerTool.setSelected(DrawerAdapter.DRAWER_MENU_PROFILE_ID);
        setDowbleBackPressetToExit(true);

//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
    }

    public Fragment getExtraPage() {
        Bundle params = getIntent().getExtras();
        if(params != null){
            int idExtraPge = params.getInt(ShikiApplikation.OPEN_PAGE);
            if(idExtraPge == 0)
                return null;
            switch (idExtraPge){
                case ShikiApplikation.MESSAGES_ID: return InboxFragment2.newInstance();
                case ShikiApplikation.NEWS_ID: return UserNewsFragment.newInstance(Constants.NEWS);
                case ShikiApplikation.NOTIFY_ID: return UserNewsFragment.newInstance(Constants.NOTIFYING);
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List fragments = this.getSupportFragmentManager().getFragments();
        if(fragments != null) {
            for(int i = fragments.size() - 1; i >= 0; --i) {
                Fragment fragment = (Fragment)fragments.get(i);
                if(fragment != null && !fragment.isDetached()) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(getClass().getSimpleName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
