package com.example.learntogether.API;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learntogether.ActivityCentral;
import com.example.learntogether.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

///Хранит информацию о последнем аккаунте, в который заходил пользователь
///IP:PORT сервера, к которому подключались, также находится тут.
public class ConnectionManager {

    public static String
            username = "tank",
            password,
            server_ip = "80.89.196.150",
            server_port = "8000",
            notification_port = "24999",
            accessToken;
    public static int ID_Account;
    public static ArrayList<String> permissions = new ArrayList<>();

    public static boolean AllowedModerateComments = false;


    public static boolean TryLoadAndConnect(Context context) {

        context.stopService(new Intent(context, NotificationService.class));

        try {
            username = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("username", null);
            password = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("password", null);
            server_ip = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("server_ip",  server_ip);
            server_port = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("server_port", server_port);
            notification_port = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("notification_port", notification_port);
            accessToken = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("accessToken", null);
            ID_Account = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getInt("ID_Account", -1);

            MainActivity.upd_loading_status("Connecting to server");
            if (!HttpJsonRequest.try_init(server_ip + ":" + server_port)) {
                if (MainActivity.THIS != null)
                    MainActivity.THIS.runOnUiThread(() ->
                            Toast.makeText(MainActivity.THIS, "No connection to server", Toast.LENGTH_SHORT).show());

                MainActivity.setIsLoading(false);
                return false;
            }
            MainActivity.upd_loading_status("Opening session");

            if (notification_port == null)
                notification_port = "24999";

            if (username != null && password != null && server_ip != null) {
                //...
                if (accessToken != null) {

                    context.startForegroundService(new Intent(context, NotificationService.class));
                    for (int k = 0; k < 20 && !NotificationService.ResultAwaited; k++) {
                        Thread.sleep(200);
                        MainActivity.upd_loading_status("Waiting: " + k);
                    }
                    if (NotificationService.TokenAccepted)
                        return true;
                }
                return TryLogin(context);
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
        prefs.putInt("ID_Account", ID_Account);

        prefs.apply();
    }

    private static volatile boolean LoginSuccess = false;
    public static boolean TryLogin(Context context) {

        context.stopService(new Intent(context, NotificationService.class));
        LoginSuccess = false;

        if (HttpJsonRequest.try_init(server_ip + ":" + server_port)) {

            JSONObject json = new JSONObject();

                try {
                    json.put("username",  ConnectionManager.username);
                    json.put("password",  ConnectionManager.password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            HttpJsonRequest.JsonRequestSync("login/login", json,
                    "POST", new HttpJsonRequest.Callback() {
                        @Override
                        public void onSuccess(JSONObject response) {

                            try {
                                Log.d("API", response.toString());
                                if (response.getString("Result").equals("Success")) {

                                    ConnectionManager.accessToken = response.getString("Token");

                                    context.startForegroundService(new Intent(context, NotificationService.class));
                                    SaveAccountInfo(context);

                                    try {
                                        ((AppCompatActivity) context).runOnUiThread(() -> {

                                            MainActivity.setIsLoading(false);

                                            Toast.makeText(context, "yes", Toast.LENGTH_SHORT).show();
                                            ((AppCompatActivity) context).runOnUiThread(() -> {
                                                context.startActivity(new Intent(context, ActivityCentral.class));
                                            });

                                            LoginSuccess = true;
                                        });
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    MainActivity.setIsLoading(false);
                                }
                            }
                            catch (Exception e) { e.printStackTrace(); }
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();

                            try {
                                ((AppCompatActivity) context).runOnUiThread(() -> {
                                    context.startForegroundService(new Intent(context, NotificationService.class));
                                    Toast.makeText(context, "no", Toast.LENGTH_SHORT).show();
                                });
                            }
                            catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
            });
        }
        return LoginSuccess;
    }
}
