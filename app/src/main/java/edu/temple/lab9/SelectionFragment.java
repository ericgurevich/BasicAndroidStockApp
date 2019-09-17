package edu.temple.lab9;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SelectionFragment extends Fragment {

    ListView listView;
    Context parentcontext;
    ArrayList<Stock> stocks;
    StockAdapter adapter;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;


    private OnFragmentInteractionListener mListener;

    public SelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getActivity().getSharedPreferences("MYSTOCKS", Context.MODE_PRIVATE);
        editor = prefs.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getSP();

        View v = inflater.inflate(R.layout.fragment_selection, container, false);

        listView = v.findViewById(R.id.listView);
        FloatingActionButton fab = v.findViewById(R.id.fab);

        adapter = new StockAdapter(parentcontext, stocks);

        listView.setAdapter(adapter);

        if (adapter.getCount() == 0) {
            TextView empty = new TextView(getContext());
            empty.setText(R.string.empty);
            ((FrameLayout) v.findViewById(R.id.selectionframe)).addView(empty);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((OnFragmentInteractionListener) parentcontext).launchDetail(position);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    //floating action bar stuff

                final String[] newstock = {""};

                AlertDialog.Builder builder = new AlertDialog.Builder(parentcontext);
                builder.setMessage(R.string.dialogtitle);

                final EditText input = new EditText(parentcontext);

                builder.setView(input);

                builder.setPositiveButton(R.string.dialogok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newstock[0] = input.getText().toString();
                        //test stock symbol
                        final Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message msg) {
                                String json = (String) msg.obj;
                                String jsontest = null;
                                try {
                                    JSONObject j = new JSONObject(json);
                                    jsontest = j.getString("Status");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(jsontest != null) {
                                    stocks.add(new Stock(newstock[0]));
                                    setSP();
                                    ((OnFragmentInteractionListener) parentcontext).refreshService();

                                } else {
                                    Toast.makeText(parentcontext,R.string.nostock,
                                            Toast.LENGTH_LONG).show();
                                }
                                return false;
                            }


                        });

                        //download stock data
                        Thread t = new Thread() {
                            @Override
                            public void run(){
                                try {
                                    BufferedReader reader = new BufferedReader(
                                            new InputStreamReader(
                                                    new URL(("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + newstock[0])).openStream()));

                                    String response = "", tmpResponse;

                                    tmpResponse = reader.readLine();
                                    while (tmpResponse != null) {
                                        response = response + tmpResponse;
                                        tmpResponse = reader.readLine();
                                    }

                                    Log.e("hello3",response);


                                    Message msg = handler.obtainMessage();

                                    msg.obj = response;

                                    handler.sendMessage(msg);



                                } catch (MalformedURLException e){
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        t.start();
                    }
                });
                builder.setNegativeButton(R.string.dialogcancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

        });

        return v;
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parentcontext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void refreshService();
        void launchDetail(int i);
    }

    void getSP() {    //fetch list from sharedpreferences

        stocks = JSONStuff.toList(prefs.getString("stocks",""));
    }

    void setSP() {  //when new stock added
        editor.putString("stocks", JSONStuff.toJSON(stocks));
        editor.commit();
    }
}
