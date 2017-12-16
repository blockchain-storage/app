package nl.tudelft.cs4160.trustchain_android.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.appToApp.PeerAppToApp;

/**
 * Created by Boning on 12/12/2017.
 */

public class ConnectionExplanationActivity extends AppCompatActivity {

    private ArrayList<String> symbolList;
    private String[] explanationText = {"Connected with peer", "Connecting with peer", "Cannot connect with peer", "Received a packet from peer", "Sent a packet to peer"};
    private int[] colorList = {R.color.colorStatusConnected, R.color.colorStatusConnecting, R.color.colorStatusCantConnect, R.color.colorReceived, R.color.colorSent};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createSymbolList();
        setContentView(R.layout.activity_connection_explanation);
        createConnectionExplanationList();
    }


    /**
     * Create the items that provides the explanation of the colors.
     */
    private void createConnectionExplanationList() {
        ListView connectionExplanationListView = (ListView) findViewById(R.id.connectionColorExplanationList);
        ConnectionExplanationListAdapter connectionExplanationListAdapter =
                new ConnectionExplanationListAdapter
                        (
                            getApplicationContext(),
                            R.layout.connection_explanation_list_item,
                            symbolList,
                            explanationText,
                            colorList
                        );

        connectionExplanationListView.setAdapter(connectionExplanationListAdapter);
    }

    /**
     * Create the list of symbols for the list view.
     */
    private void createSymbolList() {
        symbolList = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            String symbol = this.getString(R.string.circle_symbol);
            symbolList.add(symbol);
        }

        for (int i = 0; i < 2; i++) {
            String symbol = this.getString(R.string.indicator_symbol);
            symbolList.add(symbol);
        }
    }
}
