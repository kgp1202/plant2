package com.plant;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Kim on 2016-08-25.
 */
public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if(!checkApp())
            sendNotification(data);
        else {
            Log.d("a", "app is executing");
        }
    }

    private void sendNotification(Bundle data){
        String title = data.getString("title");
        String message = data.getString("message");
        String roomDataStr = data.getString("RoomData");

        Intent intent = new Intent(this, Splash_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isPushAlarm", true);
        intent.putExtra("RoomData", roomDataStr);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.push_alarm_img)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public boolean checkApp() {
        //현재 실행중인 프로세스
        ActivityManager actMng = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = actMng.getRunningAppProcesses();
        String packageName = "";
        for (ActivityManager.RunningAppProcessInfo rap : list) {
            System.out.println("packageName = "+packageName+", importance = "+rap.importance );
            if(rap.importance == rap.IMPORTANCE_FOREGROUND){
                if(rap.processName.equals("com.plant")){
                    return true;
                }
            }
        }
        return false;
    }
}
