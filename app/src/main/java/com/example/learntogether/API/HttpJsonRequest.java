package com.example.learntogether.API;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpJsonRequest {


    public interface Callback {
        void onSuccess(JSONObject response);
        void onError(Exception e);
    }

    private static String serverURL;


    private static volatile boolean Success = false;
    public static boolean try_init(String serverURL) {

        HttpJsonRequest.serverURL = serverURL + "/";

        AsyncTask<Void, Void, JSONObject> asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    JSONObject ans = JsonRequest("test/Test", new JSONObject(), "POST");
                    if (ans != null) {
                        return ans;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONObject();
            }
        };

        asyncTask.execute();

        try {
            JSONObject response = asyncTask.get(); // Блокирует текущий поток и ждет завершения AsyncTask
            return response.has("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static JSONObject JsonRequest(String urlString, JSONObject json, String method) throws IOException, JSONException {

        URL url = new URL("http://"+ serverURL + urlString);
        Log.d("API", "URL: " + url.toString());

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
        Log.d("API", rs);

        // парсим строку JSON в объект JSONObject
        JSONObject result = new JSONObject(rs);

        return result;
    }

    public static void JsonRequestAsync(String urlString, JSONObject json, String method, Callback callback) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    return JsonRequest(urlString, json, method);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject response) {
                if (response != null) {
                    callback.onSuccess(response);
                } else {
                    callback.onError(new Exception("Response is null"));
                }
            }
        }.execute();
    }

}