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
    private HashMap<Integer, String> colorExplanationText;

    public ConnectionExplanationListAdapter(Context context, int resource, int[] colorList, HashMap colorExplanationText) {
        super(context, resource);
        this.colorList = colorList;
        this.colorExplanationText = colorExplanationText;
    }

}
