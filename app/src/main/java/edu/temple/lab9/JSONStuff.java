package edu.temple.lab9;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JSONStuff {
    static Gson gson = new Gson();
    static Type type = new TypeToken<List<Stock>>(){}.getType();


    public static String toJSON(ArrayList<Stock> stocks) {
        return gson.toJson(stocks,type);
    }

    public static ArrayList<Stock> toList(String json) {
        ArrayList<Stock> hello = new Gson().fromJson(json,type);

        return gson.fromJson(json, type);
    }
}
