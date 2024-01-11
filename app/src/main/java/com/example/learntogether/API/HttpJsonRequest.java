package com.example.learntogether.API;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpJsonRequest {


    public interface Callback {
        void onSuccess(String response);
        void onError(Exception e);
    }

    private static String serverURL;


    private static volatile boolean Success = false;
    public static boolean try_init(String serverURL) {

        HttpJsonRequest.serverURL = serverURL + "/";

        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String ans = JsonRequest("test/Test", "{}", "POST");
                    if (ans != null) {
                        return ans;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
        };

        asyncTask.execute();

        try {
            String response = asyncTask.get(); // Блокирует текущий поток и ждет завершения AsyncTask
            return response.contains("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String JsonRequest(String urlString, String json, String method) throws IOException {

        URL url = new URL("http://"+ serverURL + urlString);
        Log.d("API", "URL: " + url.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int resp_code = conn.getResponseCode();
        if (resp_code != HttpURLConnection.HTTP_OK) {
            Log.d("API", String.valueOf(resp_code));
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        StringBuilder responseBuilder = new StringBuilder();
        String output;
        while ((output = reader.readLine()) != null) {
            responseBuilder.append(output);
        }

        conn.disconnect();

        String rs = responseBuilder.toString();
        Log.d("API", rs);
        return rs;
    }

    public static void JsonRequestAsync(String urlString, String json, String method, Callback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    return JsonRequest(urlString, json, method);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    callback.onSuccess(response);
                } else {
                    callback.onError(new Exception("Response is null"));
                }
            }
        }.execute();
    }

}