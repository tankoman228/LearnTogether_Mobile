package com.example.learntogether.API;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.learntogether.MainActivity;
import com.example.learntogether.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NotificationService extends Service {
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mRunnable = () -> new NetworkTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
        return START_STICKY; // Служба будет перезапущена после того, как была убита системой
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable); // Остановка выполнения процесса при уничтожении службы
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    private Socket mSocket;
    private BufferedReader mIn;
    private PrintWriter mOut;


    private void socketCycle() {

        for (;;) {

            Log.d("API", "socketCycle start");

            try {

                mSocket = new Socket("80.89.196.150", 24999);

                mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOut = new PrintWriter(mSocket.getOutputStream(), true);

                mOut.println(CurrentAccount.AccessToken.substring(0, 15));

                Log.d("API", "getting");

                String message;
                while (true) {
                    message = mIn.readLine();
                    if (message != null && message.length() > 2) {
                        showNotification(message);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

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
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("API", "finished");
        }
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