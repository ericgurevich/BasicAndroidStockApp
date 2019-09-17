//Eric Gurevich. Lab 9
package edu.temple.lab9;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SelectionFragment.OnFragmentInteractionListener {
    SelectionFragment selectionFragment;
    DetailFragment newdetail;
    FragmentManager fragmentManager;
    int orientation;
    ArrayList<Stock> stocks;
    Handler mHandler;
    Runnable mHandlerTask;
    Type type;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    SharedPreferences.OnSharedPreferenceChangeListener spChanged;
    int currentstock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        prefs = getSharedPreferences("MYSTOCKS", Context.MODE_PRIVATE);
        editor = prefs.edit();

        stocks = JSONStuff.toList(prefs.getString("stocks",""));
        if (stocks == null) {
            stocks = new ArrayList<>();
            setSP();
        }

        if(getResources().getConfiguration().smallestScreenWidthDp >= 600) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        orientation = getResources().getConfiguration().orientation;

        selectionFragment = new SelectionFragment();

        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container_1, selectionFragment)
                .commit();

        /*if(orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

            Bundle bundle = new Bundle();
            bundle.putInt("color",Color.TRANSPARENT);
            detailFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .replace(R.id.container_2, detailFragment)
                    .commit();
        }*/

        mHandler = new Handler();
        mHandlerTask = new Runnable()
        {
            @Override
            public void run() {     //update stocks every 30 seconds
                Intent stockQuoteIntent = new Intent(MainActivity.this, StockService.class);
                startService(stockQuoteIntent);
                Toast.makeText(MainActivity.this, R.string.refresh, Toast.LENGTH_SHORT).show();

                mHandler.postDelayed(mHandlerTask, 30000);
            }
        };

        mHandlerTask.run();

        spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {  //listens for changes in sp
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs,
                                                  String key) {

                Log.e("hello4","test");
                stocks = JSONStuff.toList(prefs.getString("stocks",""));

                selectionFragment.adapter.notifyDataSetChanged();   //updates selectionfragment

                fragmentManager.beginTransaction().replace(R.id.container_1,new SelectionFragment()).commit();

                if(newdetail != null) {
                    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", stocks.get(currentstock).name);
                        bundle.putString("charturl", stocks.get(currentstock).charturl);
                        bundle.putDouble("price", stocks.get(currentstock).price);
                        bundle.putDouble("opening", stocks.get(currentstock).opening);

                        newdetail = new DetailFragment();
                        newdetail.setArguments(bundle);

                        fragmentManager.beginTransaction()
                                .replace(R.id.container_2,newdetail)
                                .commit();
                    }
                }

            }
        };

        prefs.registerOnSharedPreferenceChangeListener(spChanged);

    }

    void setSP() {  //when new stock added
        editor.putString("stocks", JSONStuff.toJSON(stocks));
        editor.commit();
    }


    @Override
    public void refreshService() {      //callback from selectionfragment
        Intent stockQuoteIntent = new Intent(MainActivity.this, StockService.class);
        startService(stockQuoteIntent);

    }

    @Override
    public void launchDetail(int i) {
        Bundle bundle = new Bundle();
        bundle.putString("name",stocks.get(i).name);
        bundle.putString("charturl",stocks.get(i).charturl);
        bundle.putDouble("price",stocks.get(i).price);
        bundle.putDouble("opening",stocks.get(i).opening);

        newdetail = new DetailFragment();
        newdetail.setArguments(bundle);

        if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_1, newdetail)
                    .addToBackStack(null)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container_2,newdetail)
                    .commit();
        }
        currentstock = i;
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mHandlerTask);     //stop handler loop
        super.onStop();
    }
}
