/*
 * Copyright (c) 2019 Cisco and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package icn.forwarder.com.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import icn.forwarder.com.forwarderandroid.ForwarderAndroidActivity;
import icn.forwarder.com.supportlibrary.Forwarder;
import icn.forwarder.com.utility.Constants;
import icn.forwarder.com.utility.ResourcesEnumerator;

public class ForwarderAndroidService extends Service {
    private final static String TAG = "ForwarderService";

    private static Thread sForwarderThread = null;

    public ForwarderAndroidService() {
    }

    private String path;
    private int capacity;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Forwarder forwarder = Forwarder.getInstance();
        if (!forwarder.isRunning()) {
            Log.d(TAG, "Starting Forwarder");
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.FORWARDER_PREFERENCES, MODE_PRIVATE);
            String configuration = sharedPreferences.getString(ResourcesEnumerator.CONFIGURATION.key(), Constants.DEFAULT_CONFIGURATION);
            String sourceIp = sharedPreferences.getString(ResourcesEnumerator.SOURCE_IP.key(), null);
            String sourcePort = sharedPreferences.getString(ResourcesEnumerator.SOURCE_PORT.key(), null);
            String nextHopIp = sharedPreferences.getString(ResourcesEnumerator.NEXT_HOP_IP.key(), null);
            String nextHopPort = sharedPreferences.getString(ResourcesEnumerator.NEXT_HOP_PORT.key(), null);
            String prefix = sharedPreferences.getString(ResourcesEnumerator.PREFIX.key(), null);
            String netmask = sharedPreferences.getString(ResourcesEnumerator.NETMASK.key(), null);
            int capacity = Integer.parseInt(sharedPreferences.getString(ResourcesEnumerator.CAPACITY.key(), Constants.DEFAULT_CAPACITY));
            configuration = configuration.replace(Constants.SOURCE_IP, sourceIp);
            configuration = configuration.replace(Constants.SOURCE_PORT, sourcePort);
            configuration = configuration.replace(Constants.NEXT_HOP_IP, nextHopIp);
            configuration = configuration.replace(Constants.NEXT_HOP_PORT, nextHopPort);
            configuration = configuration.replace(Constants.PREFIX, prefix);
            configuration = configuration.replace(Constants.NETMASK, netmask);
            try {
                String configurationDir = getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.dataDir +
                        File.separator + Constants.CONFIGURATION_PATH;
                File folder = new File(configurationDir);
                if (!folder.exists()) {
                    folder.mkdirs();
                }

                writeToFile(configuration, configurationDir + File.separator + Constants.CONFIGURATION_FILE_NAME);
                Log.d(TAG, configurationDir + File.separator + Constants.CONFIGURATION_FILE_NAME);
                startForwarder(intent, configurationDir + File.separator + Constants.CONFIGURATION_FILE_NAME, capacity);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Error Package name not found ", e);
            }


        } else {
            Log.d(TAG, "Forwarder already running.");
        }
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        Forwarder forwarder = Forwarder.getInstance();
        Log.d(TAG, "Destroying Forwarder");
        if (forwarder.isRunning()) {
            forwarder.stop();
            stopForeground(true);
        }
        super.onDestroy();
    }

    protected Runnable mForwarderRunner = new Runnable() {

        //private String path;
        @Override
        public void run() {
            Forwarder forwarder = Forwarder.getInstance();
            forwarder.start(path, capacity);
        }


    };

    private boolean writeToFile(String data, String path) {
        Log.v(TAG, path + " " + data);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), "utf-8"))) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
            return false;
        }
    }


    private void startForwarder(Intent intent, String path, int capacity) {
        String NOTIFICATION_CHANNEL_ID = "12345";
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);

            Intent notificationIntent = new Intent(this, ForwarderAndroidActivity.class);
            PendingIntent activity = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentTitle("ForwarderAndroid").setContentText("ForwarderAndroid").setOngoing(true).setContentIntent(activity);
            notification = notificationBuilder.build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("ForwarderAndroid")
                    .setContentText("ForwarderAndroid")
                    .build();
        }

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "ForwarderAndroid", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("ForwarderAndroid");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

        }

        startForeground(Constants.FOREGROUND_SERVICE, notification);


        Forwarder forwarder = Forwarder.getInstance();
        if (!forwarder.isRunning()) {
            this.path = path;
            this.capacity = capacity;
            sForwarderThread = new Thread(mForwarderRunner, "ForwarderRunner");
            sForwarderThread.start();
        }


        Log.i(TAG, "ForwarderAndroid starterd");

    }
}