package com.example.learntogether.API;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.learntogether.ActivityCentral;
import com.example.learntogether.MainActivity;
import com.example.learntogether.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NotificationService extends Service {
    private static Handler mHandler;
    private static Runnable mRunnable;


    public static volatile boolean ConnectionSuccess = false, ResultAwaited = false, TokenAccepted = false;


    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mRunnable = () -> new NetworkTask().execute();
    }

    private static final int FOREGROUND_NOTIFICATION_ID = 25;
    private static final String FOREGROUND_CHANNEL_ID = "Notification Channel Service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, "My Service Channel",
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Notification Service")
                .setContentText("Service is running in foreground")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        startForeground(FOREGROUND_NOTIFICATION_ID, notification);

        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);

        return START_STICKY; // Служба будет перезапущена после того, как была убита системой
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable); // Остановка выполнения процесса при уничтожении службы
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private Socket mSocket;
    private BufferedReader mIn;
    private PrintWriter mOut;


    public void socketCycle() {

        MainActivity.setIsLoading(true);

        for (int attempts = 1; attempts < 100; attempts++) {

            MainActivity.upd_loading_status("Connection trying: attempt " + attempts);

            ResultAwaited = false;
            ConnectionSuccess = false;
            TokenAccepted = false;
            Log.d("API", "socketCycle start");

            try {
                MainActivity.upd_loading_status("Trying to open socket");
                mSocket = new Socket(ConnectionManager.server_ip, Integer.parseInt(ConnectionManager.notification_port));

                mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOut = new PrintWriter(mSocket.getOutputStream(), true);

                mOut.println(ConnectionManager.accessToken.substring(0, 15));

                MainActivity.upd_loading_status("Connected. Waiting for answer");
                Log.d("API", "getting");

                String message = mIn.readLine();
                boolean Accepted = message.contains("Accepted");
                boolean Declined = message.contains("Declined");

                if (Accepted || Declined) {

                    ConnectionSuccess = true;
                    if (Declined) {

                        MainActivity.upd_loading_status("Session declined. Autologin");

                        if (!ConnectionManager.TryLogin(this))
                            break;

                        MainActivity.upd_loading_status("Session error");
                        ResultAwaited = true;
                        return;
                    }
                    TokenAccepted = true;
                }
                ResultAwaited = true;
                MainActivity.upd_loading_status("Successful. Socket opened");

                if (MainActivity.THIS != null) {
                    MainActivity.THIS.runOnUiThread(() -> {
                        MainActivity.THIS.startActivity(new Intent(MainActivity.THIS, ActivityCentral.class));
                    });
                }

                while (Accepted) {
                    message = mIn.readLine();
                    if (message != null && message.length() > 2) {
                        showNotification(message);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                MainActivity.upd_loading_status("Error, trying again");

                ConnectionSuccess = false;
                ResultAwaited = true;

                try {
                    if (mIn != null) {
                        mIn.close();
                    }
                    if (mOut != null) {
                        mOut.close();
                    }
                    if (mSocket != null) {
                        mSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("API", "finished socket attempt");
        }

        MainActivity.upd_loading_status("Cannot connect");
        MainActivity.setIsLoading(false);

        Log.d("API", "Notification service finished itself");

        stopForeground(true);
        mHandler.removeCallbacks(mRunnable);
        stopSelf();
    }


    private class  NetworkTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            socketCycle();
            return null;
        }
    }



    String CHANNEL_ID = "Learn Together";
    int notificationId = 235;

    private void showNotification(String message) {

        Log.d("API", message);

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyChannel", importance);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Learn Together")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }
}