package com.example.learntogether.API;

import com.example.learntogether.FromAPI.ForumAsk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForumLoader {

    final int MAX_REQUIRE = 15;

    public static ArrayList<ForumAsk> Asks = new ArrayList<>();

    public static void Reload(int ID_Max, int Group) {

        JSONObject json = new JSONObject();

        try {
            json.put("id_max", ID_Max);
            json.put("group", Group);
            json.put("search_string", "");
            json.put("number", 5);
            json.put("session_token", ConnectionManager.accessToken);
        }
        catch (Exception e) { e.printStackTrace(); }

        HttpJsonRequest.JsonRequestSync("get_asks", json, "POST", new HttpJsonRequest.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray asks = response.getJSONArray("Asks");

                    int i = 0;
                    while (i < asks.length()) {
                        JSONObject o = (JSONObject)asks.get(i);

                        int id = o.getInt("ID_ForumAsk");
                        Asks.removeIf(x -> x.ID_ForumAsk == id);

                        ForumAsk new_fa = new ForumAsk();

                        new_fa.ID_ForumAsk = id;
                        new_fa.Solved = o.getBoolean("Solved");
                        new_fa.CommentsFound = o.getInt("CommentsFound");
                        new_fa.AuthorTitle = o.getString("AuthorTitle");
                        new_fa.ID_Author = o.getInt("ID_Author");
                        new_fa.ID_InfoBase = o.getInt("ID_InfoBase");
                        new_fa.Rate = o.getDouble("Rate");
                        new_fa.Text = o.getString("Text");
                        new_fa.Title = o.getString("Title");
                        new_fa.Type = o.getString("Type");
                        new_fa.WhenAdd = o.getString("WhenAdd");

                        Asks.add(new_fa);

                        i++;
                    }
                }
                catch (JSONException ee) {
                    ee.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void Search(int ID_max, int Group, String search) {

    }


}
