package com.example.learntogether.API;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpJsonRequest {


    public interface Callback {
        void onSuccess(JSONObject response);
        void onError(Exception e);
    }

    private static String serverURL;
    private static volatile JSONObject response = new JSONObject();


    public static boolean try_init(String serverIpPort) {

        HttpJsonRequest.serverURL = "http://" + serverIpPort + "/";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                response = JsonRequest("test/Test", new JSONObject(), "POST");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return response.has("Success");
    }


    private static JSONObject JsonRequest(String urlString, JSONObject json, String method) throws IOException, JSONException {

        URL url = new URL(serverURL + urlString);
        Log.d("API", "URL: " + url.toString());
        Log.d("API", "Send: " + json.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
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
        //Log.d("API", rs);

        // парсим строку JSON в объект JSONObject
        response = new JSONObject(rs);

        return response;
    }

    public static void JsonRequestAsync(String urlString, JSONObject json, String method, Callback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                response = JsonRequest(urlString, json, method);
                if (response != null) {
                    callback.onSuccess(response);
                } else {
                    callback.onError(new Exception("Response is null"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }


    public static void JsonRequestSync(String urlString, JSONObject json, String method, Callback callback) {
        Log.d("API", "JsonRequestSync");

        try {
            ExecutorService ex = Executors.newSingleThreadExecutor();
            ex.execute(() -> {
                Log.d("API", "NOT_ASYNC_REQUEST_START");
                try {
                    response = JsonRequest(urlString, json, method);

                    if (response != null) {
                        callback.onSuccess(response);
                    } else {
                        callback.onError(new Exception("Response is null"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
            });

            if (!ex.awaitTermination(5, TimeUnit.SECONDS))
                callback.onError(new Exception("Response timeout"));
            callback.onSuccess(response);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

}