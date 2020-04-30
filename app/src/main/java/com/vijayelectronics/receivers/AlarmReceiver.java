package com.vijayelectronics.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vijayelectronics.activities.SplashScreen;
import com.vijayelectronics.app.MyAppPrefsManager;
import com.vijayelectronics.utils.NotificationHelper;
import com.vijayelectronics.utils.NotificationScheduler;


/**
 * AlarmReceiver receives the Broadcast Intent
 */

public class AlarmReceiver extends BroadcastReceiver {
    
    
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                
                NotificationScheduler.setReminder(context, AlarmReceiver.class);
            }
        }
        
        
        Intent notificationIntent = new Intent(context, SplashScreen.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(context);
        
        
        //Trigger the notification
        NotificationHelper.showNewNotification
                (
                        context,
                        notificationIntent,
                        myAppPrefsManager.getLocalNotificationsTitle(),
                        myAppPrefsManager.getLocalNotificationsDescription()
                );
        
    }
    
}

