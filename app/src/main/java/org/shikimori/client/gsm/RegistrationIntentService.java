/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.shikimori.client.gsm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.shikimori.client.R;
import org.shikimori.library.loaders.ShikiPath;
import com.gars.querybuilder.BaseQuery;
import org.shikimori.library.loaders.QueryShiki;
import org.shikimori.library.loaders.ShikiStatusResult;
import org.shikimori.library.tool.ShikiUser;

import java.io.IOException;

import ru.altarix.basekit.library.tools.h;
import ru.altarix.basekit.library.tools.objBuilder.HelperObj;

public class RegistrationIntentService extends IntentService implements BaseQuery.OnQueryErrorListener<ShikiStatusResult> {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private SharedPreferences sharedPreferences;
    private ShikiUser user;
    private QueryShiki query;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = new ShikiUser(RegistrationIntentService.this);
        query = new QueryShiki(this, false)
                .setErrorListener(this);
        if(ShikiUser.USER_ID!=null){
            try {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(token);

                // Subscribe to topic channels
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            } catch (Exception e) {
                Log.d(TAG, "Failed to complete token refresh", e);
                // If an exception happens while fetching the new token or updating our registration data
                // on a third-party server, this ensures that we'll attempt the update at a later time.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            }
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
//        String deviceId = user.getDeviceId();

        query.in(ShikiPath.DEVICES)
             .getResultArray(new BaseQuery.OnQuerySuccessListener<ShikiStatusResult>() {
                 @Override
                 public void onQuerySuccess(ShikiStatusResult res) {

                     // Check if token already exist on server
                     JSONArray devices = res.getResultArray();
                     if(devices!=null){
                         for (int i = 0; i < devices.length(); i++) {
                             JSONObject obj = devices.optJSONObject(i);
                             if(obj!=null){
                                 String servToken = HelperObj.getString(obj, "token");
                                 if(!TextUtils.isEmpty(servToken) && token.equals(servToken)){
                                     user.setDeviceId(HelperObj.getString(obj, "id"));
                                     return;
                                 }
                             }
                         }
                     }

                     // send token to server
                     query.in(ShikiPath.DEVICES)
                          .setMethod(BaseQuery.METHOD.POST)
                          .addParam("device[user_id]", ShikiUser.USER_ID)
                          .addParam("device[token]", token)
                          .addParam("device[platform]", "android")
                          .addParam("device[name]", h.getDeviceName())
                          .getResultObject(new BaseQuery.OnQuerySuccessListener<ShikiStatusResult>() {
                              @Override
                              public void onQuerySuccess(ShikiStatusResult res) {
                                  String tokenId = res.getParameter("id");
                                  user.setDeviceId(tokenId);
                              }
                          });
                 }
             });

    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
    @Override
    public void onQueryError(ShikiStatusResult res) {
        sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
    }

}
