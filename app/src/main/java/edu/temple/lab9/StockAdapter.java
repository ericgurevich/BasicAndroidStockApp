package edu.temple.lab9;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class StockAdapter extends BaseAdapter {
    Context context;
    Resources res;
    ArrayList<Stock> stocks;

    public StockAdapter(Context context, ArrayList<Stock> stocks) {
        this.context = context;
        this.stocks = stocks;
        res = context.getResources();

    }

    @Override
    public int getCount() {
        return stocks.size();

    }

    @Override
    public Object getItem(int position) {
        return stocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.adapter_stock, parent, false);
        }

        View v = convertView;

        double price = stocks.get(position).price;

        double change = stocks.get(position).change;

        String symb = stocks.get(position).symb;

        ((TextView) v.findViewById(R.id.price)).setText(String.valueOf(price));
        if(change >= 0) {
            v.findViewById(R.id.container).setBackgroundColor(Color.GREEN);
        } else {
            v.findViewById(R.id.container).setBackgroundColor(Color.RED);
        }

        ((TextView) v.findViewById(R.id.symbol)).setText(symb);

        return v;
    }




}
