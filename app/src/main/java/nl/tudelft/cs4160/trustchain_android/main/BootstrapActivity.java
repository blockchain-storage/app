package nl.tudelft.cs4160.trustchain_android.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import nl.tudelft.cs4160.trustchain_android.R;

public class BootstrapActivity extends AppCompatActivity {
    private EditText bootstrapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootstrap);
    }

    public void onClickConnect(View view) {
        bootstrapView = (EditText) findViewById(R.id.bootstrap_IP);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ConnectableAddress",bootstrapView.getText().toString());
        setResult(OverviewConnectionsActivity.RESULT_OK,returnIntent);
        finish();
    }
}
