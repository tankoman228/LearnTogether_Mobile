package com.example.learntogether.API;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learntogether.ActivityLogin;

import org.json.JSONException;
import org.json.JSONObject;

///Хранит информацию о последнем аккаунте, в который заходил пользователь
///IP:PORT сервера, к которому подключались, также находится тут.
public class ConnectionData {

    public static String
            username = "tank",
            password,
            server_ip = "80.89.196.150",
            server_port = "8000",
            notification_port = "24999",
            accessToken;


    public static boolean TryLoadAndConnect(Context context) {

        context.stopService(new Intent(context, NotificationService.class));

        try {
            username = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("username", null);
            password = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("password", null);
            server_ip = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("server_ip",  server_ip);
            server_port = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("server_port", server_port);
            notification_port = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("notification_port", notification_port);
            accessToken = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("accessToken", null);

            if (notification_port == null)
                notification_port = "24999";

            if (username != null && password != null && server_ip != null) {
                //...
                if (accessToken != null) {

                    context.startForegroundService(new Intent(context, NotificationService.class));
                    for (int k = 0; k < 20 && !NotificationService.ResultAwaited; k++) {
                        Thread.sleep(200);
                    }
                    if (NotificationService.ConnectionSuccess)
                        return true;

                    context.stopService(new Intent(context, NotificationService.class));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void SaveAccountInfo(Context context) {

        SharedPreferences prefss = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs = prefss.edit();

        prefs.putString("username", username);
        prefs.putString("password", password);
        prefs.putString("server_ip", server_ip);
        prefs.putString("server_port", server_port);
        prefs.putString("notification_port", notification_port);
        prefs.putString("accessToken", accessToken);

        prefs.apply();
    }

    public static void TryLogin(Context context) {

        context.stopService(new Intent(context, NotificationService.class));

        if (HttpJsonRequest.try_init(server_ip + ":" + server_port)) {

            JSONObject json = new JSONObject();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                try {
                    json.put("username",  ConnectionData.username);
                    json.put("password",  ConnectionData.password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            HttpJsonRequest.JsonRequestAsync("login/login", json,
                    "POST", new HttpJsonRequest.Callback() {
                        @Override
                        public void onSuccess(JSONObject response) {

                            try {
                                if (response.getString("Result").equals("Success")) {
                                    ConnectionData.accessToken = response.getString("Token");

                                    ((AppCompatActivity)context).runOnUiThread(() -> {
                                        context.startForegroundService(new Intent(context, NotificationService.class));
                                        Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                                        SaveAccountInfo(context);
                                    });
                                }
                            }
                            catch (Exception e) { e.printStackTrace(); }
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            ((AppCompatActivity)context).runOnUiThread(() -> {
                                context.startForegroundService(new Intent(context, NotificationService.class));
                                Toast.makeText(context, "no", Toast.LENGTH_SHORT).show();
                            });
                        }
            });
        }
    }
}