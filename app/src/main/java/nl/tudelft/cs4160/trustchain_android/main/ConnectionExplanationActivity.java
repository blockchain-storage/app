package nl.tudelft.cs4160.trustchain_android.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.appToApp.PeerAppToApp;

/**
 * Created by Boning on 12/12/2017.
 */

public class ConnectionExplanationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_explanation);
        createConnectionExplanationList();
    }


    /**
     * Create the items that provides the explanation of the colors.
     */
    private void createConnectionExplanationList() {
        ListView connectionExplanationListView = (ListView) findViewById(R.id.connectionColorExplanationList);
        int[] colorsList = {R.color.colorStatusConnected, R.color.colorStatusConnecting, R.color.colorStatusCantConnect, R.color.colorSent, R.color.colorReceived};
        HashMap<Integer, String> colorExplanationText = createExplanationTextMap();

        ConnectionExplanationListAdapter connectionExplanationListAdapter =
                new ConnectionExplanationListAdapter
                        (
                            getApplicationContext(),
                            R.layout.connection_explanation_list_item,
                            colorsList,
                            colorExplanationText
                        );

        connectionExplanationListView.setAdapter(connectionExplanationListAdapter);
    }

    private HashMap createExplanationTextMap() {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        return result;
    }

}
