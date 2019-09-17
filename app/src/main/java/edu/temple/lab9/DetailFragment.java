package edu.temple.lab9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment {
    String name;
    String charturl;
    double price;
    double opening;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        this.name = bundle.getString("name");
        this.charturl = bundle.getString("charturl");
        this.price = bundle.getDouble("price");
        this.opening = bundle.getDouble("opening");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        ((TextView) v.findViewById(R.id.detailname)).setText(name);
        ((TextView) v.findViewById(R.id.detailprice)).setText(String.valueOf(price));
        ((TextView) v.findViewById(R.id.detailopening)).setText(String.valueOf(opening));

        WebView w = v.findViewById(R.id.webview);
        w.getSettings().setJavaScriptEnabled(true);
        w.loadUrl(charturl);

        return v;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
