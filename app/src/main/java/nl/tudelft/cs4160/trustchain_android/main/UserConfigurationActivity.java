package nl.tudelft.cs4160.trustchain_android.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nl.tudelft.cs4160.trustchain_android.R;

/**
 * Created by Boning on 12/3/2017.
 */

public class UserConfigurationActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_configuration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText userNameInput = (EditText) findViewById(R.id.username);
        userNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    setNewUserName();
                }
            }
        });

        Button confirmBtn = (Button) findViewById(R.id.confirm_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText userNameInput = (EditText) findViewById(R.id.username);
                if(!userNameInput.getText().toString().matches("")) {
                    Intent myIntent = new Intent(UserConfigurationActivity.this, OverviewConnectionsActivity.class);
                    EditText mEdit = (EditText)findViewById(R.id.username);
                    myIntent.putExtra("userName", mEdit.getText().toString());
                    // TODO: Change hard coded icon index passing
                    myIntent.putExtra("icon", 0);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UserConfigurationActivity.this.startActivity(myIntent);
                } else {
                    TextView userNot = (TextView) findViewById(R.id.user_notification);
                    userNot.setText("Please fill in a username!");
                }
            }
        });
    }

    private void setNewUserName() {

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
