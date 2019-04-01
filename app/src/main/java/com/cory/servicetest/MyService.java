package com.cory.servicetest;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

import static com.cory.servicetest.SystemHelper.print;

public class MyService extends IntentService {
    private static final long POLL_INTERVAL_MS= TimeUnit.MINUTES.toMillis(1);
    private static final String CHANNEL_ID_AND_NAME="channel";

    public static Intent newIntent(Context context){
        return new Intent(context, MyService.class);
    }

    public MyService() {
        super("my service");
    }

    public static void setService(Context context,boolean isOn){
        Intent i=MyService.newIntent(context);
        PendingIntent pi=PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL_MS,pi);
        }else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent i=MyService.newIntent(context);
        PendingIntent pi= PendingIntent.getService(context,0,i,PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        print("on handle intent");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_AND_NAME, CHANNEL_ID_AND_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent i=new Intent(this,Main2Activity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,0);
        Notification notification=new NotificationCompat.Builder(this)
                .setContentTitle("title")
                .setContentText("content")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID_AND_NAME)
                .build();

        manager.notify(0,notification);
    }
}
