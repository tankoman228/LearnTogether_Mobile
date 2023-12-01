package com.example.learntogether;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ServiceSocket extends Service {

    //IP address
    public static String hostname = "192.168.3.73";

    //Server port
    public static int Port = 9540;

    //Register token we are trying to use
    public static String RegisterToken = "";


    //The only service we uss
    public static ServiceSocket THIS = null;

    //To send messages from service to current activity
    public static API_Connectable Activity = null;

    //Notification for foreground service
    private static final int NOTIFICATION_ID = 300;
    private static final String CHANNEL_ID = "TimeManager_channel";


    //Object managing connection and sending/getting data from socket
    private static ClientThread clientThread;


    public ServiceSocket() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        THIS = this;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "LearnTogether API Channel",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LearnTogether API")
                .setContentText("Service is running in foreground")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    //Opens connection or restarts the socket process
    public static void restart_process(boolean needReconnectInCaseOfError) {

        if (clientThread != null) {
            clientThread.close_all();
            clientThread.stop();
        }

        new Thread(() -> {
            int wait_time = 0;
            do {
                try {
                    Thread.currentThread().wait(wait_time);

                    if (!try_connect()) {
                        throw new Exception("retry connect!");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    wait_time += 1000;
                }
            }
            while (needReconnectInCaseOfError);
        });
    }


    //Returns false if can't open connection
    private static boolean try_connect() {
        try {
            clientThread = new ClientThread() {
                @Override
                protected void onGet_message_from_server(String msg) {
                    if (Activity != null)
                        Activity.onResponce(msg);
                }
            };
            clientThread.run();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //Sends message to serer using socket object
    public static void send_message(String json) {
        if (clientThread == null) return;
        clientThread.send_message(json);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public void onDestroy() {
        THIS = null;
    }
}