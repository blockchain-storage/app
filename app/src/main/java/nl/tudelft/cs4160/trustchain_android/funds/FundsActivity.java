package nl.tudelft.cs4160.trustchain_android.funds;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;

public class FundsActivity extends AppCompatActivity {

    ListView transactionListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funds);

        transactionListView = findViewById(R.id.transaction_listview);
        FundsAdapter adapter = new FundsAdapter(this);
        TrustChainDBHelper helper = new TrustChainDBHelper(this);
        List<MessageProto.TrustChainBlock> blocks = helper.getAllBlocks();
        adapter.addAll(blocks);
        transactionListView.setAdapter(adapter);

        try {
            MessageProto.TrustChainBlock firstBlock = blocks.get(0);
            String transactionString = firstBlock.getTransaction().toStringUtf8();
            Log.i("FundsActivity", transactionString);
            JSONObject object = new JSONObject(transactionString); // TODO refactor to some kind of factory

            double up = object.getDouble("up");

            TextView upAmount = findViewById(R.id.current_funds_up_amount);
            upAmount.setText(Double.toString(up));

            double down = object.getDouble("down");
            TextView downAmount = findViewById(R.id.current_funds_down_amount);
            downAmount.setText(Double.toString(down));
        } catch (Exception e) {
            Log.e("FundsActivity", "Could not read current funds", e);
        }
    }
}
