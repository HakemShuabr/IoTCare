package com.example.admin.firstversionapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Admin on 04/10/2017.
 */

public class RestartService extends BroadcastReceiver {
    public static String systemCode;
    @Override
    public void onReceive(Context context, Intent intent) {
        systemCode = intent.getExtras().getString("systemCode");
        Intent intentService = new Intent(context, BackgroundService.class);
        BackgroundService.systemCode = systemCode;
        context.startService(intentService);
    }

}
