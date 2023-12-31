package com.example.learntogether;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

///Хранит информацию о последнем аккаунте, в который заходил пользователь
///IP:PORT сервера, к которому подключались, также находится тут.
public class CurrentAccount {

    public static String username, password, server;


    public static boolean TryLoadAccountInfo(Context context) {
        try {
            username = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("username", null);
            password = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("password", null);
            server = context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("server", null);

            if (username != null && password != null && server != null) {

                try {
                    Connect(context);
                }
                catch (Exception ee) {
                    ee.printStackTrace();
                }
                return true;
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
        prefs.putString("server", ServiceSocket.hostname + ":" + String.valueOf(ServiceSocket.Port));
        prefs.apply();
    }


    private static void Connect(Context context) {

        ServiceSocket.hostname = server.split(":")[0];
        ServiceSocket.Port = Integer.valueOf(server.split(":")[1]);

        Intent i = new Intent(context, ServiceSocket.class);
        context.startForegroundService(i);
    }
}
