package edu.temple.lab9;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class StockService extends IntentService {
    ArrayList<Stock> stocks;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public StockService() {
        super("StockService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            prefs = getSharedPreferences("MYSTOCKS", Context.MODE_PRIVATE);
            editor = prefs.edit();

            //fetch list from sharedpreferences
            stocks = JSONStuff.toList(prefs.getString("stocks",""));

            for(int i = 0; i < stocks.size(); i++) {    //for every stock in my arraylist, download json, update fields
                updateStocks(i);
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            editor.putString("stocks",JSONStuff.toJSON(stocks));
            editor.commit();

        }
    }

    protected void setStocks(int i, String response) {
        try {
            JSONObject j = new JSONObject(response);
            stocks.get(i).name = j.getString("Name");
            stocks.get(i).price = j.getDouble("LastPrice");
            stocks.get(i).change = j.getDouble("Change");
            stocks.get(i).opening = j.getDouble("Open");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //fetch and update stocks
    protected void updateStocks(final int i) {
            Thread t = new Thread() {
                @Override
                public void run(){
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        new URL(stocks.get(i).url).openStream()));

                        String response = "", tmpResponse;

                        tmpResponse = reader.readLine();
                        while (tmpResponse != null) {
                            response = response + tmpResponse;
                            tmpResponse = reader.readLine();
                        }
                        setStocks(i, response);
                    } catch (MalformedURLException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
    }
}
