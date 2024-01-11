package com.example.learntogether.API;

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


    public static boolean try_init(String serverURL) {

        HttpJsonRequest.serverURL = serverURL + "/";

        try {
            String ans = JsonRequest("test/Test", "{}", "GET");
            if (ans != null) {
                return ans.contains("Success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String JsonRequest(String urlString, String json, String method) throws IOException {

        URL url = new URL("http://"+ serverURL + urlString);
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

        Log.d("API", responseBuilder.toString());
        return responseBuilder.toString();
    }

    public static void JsonRequestAsync(String urlString, String json, String method, Callback callback) {
        new Thread(() -> {
            try {
                String response = JsonRequest(urlString, json, method);
                if (response != null) {
                    callback.onSuccess(response);
                } else {
                    callback.onError(new Exception("Response is null"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }
}