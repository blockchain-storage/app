package nl.tudelft.cs4160.trustchain_android.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.appToApp.PeerAppToApp;

/**
 * Adapter for creating the items in the color explanation screen.
 */
public class ConnectionExplanationListAdapter extends ArrayAdapter<String> {

    private int[] colorList;
    private String[] colorExplanationText;
    private LayoutInflater mInflater;

    public ConnectionExplanationListAdapter(Context context, int resource, int[] colorList, String[] colorExplanationText) {
        super(context, resource);
        this.colorList = colorList;
        this.colorExplanationText = colorExplanationText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.connection_explanation_list_item, parent, false);
        TextView symbol = (TextView) convertView.findViewById(R.id.colorSymbol);
        TextView symbolMeaning = (TextView) convertView.findViewById(R.id.symbolMeaning);
        symbol.setText(colorList[position]);
        symbolMeaning.setText(colorExplanationText[position]);
        return convertView;
    }

}
