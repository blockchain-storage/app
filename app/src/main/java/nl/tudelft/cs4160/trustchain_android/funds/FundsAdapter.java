package nl.tudelft.cs4160.trustchain_android.funds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;

import static nl.tudelft.cs4160.trustchain_android.Util.Util.readableSize;

public class FundsAdapter extends ArrayAdapter<MessageProto.TrustChainBlock> {
    public FundsAdapter(@NonNull Context context) {
        super(context, R.layout.item_transaction);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            View view;
            if (convertView == null) view = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
            else view = convertView;

            MessageProto.TrustChainBlock block = this.getItem(position);
            String transactionString = block.getTransaction().toStringUtf8();
            System.out.println("Found " + transactionString);
            JSONObject object = new JSONObject(transactionString); // TODO refactor to some kind of factory

            long up = object.getLong("up");

            TextView upAmount = view.findViewById(R.id.funds_item_up_amount);
            upAmount.setText(readableSize(up));

            long down = object.getLong("down");
            TextView downAmount = view.findViewById(R.id.funds_item_down_amount);
            downAmount.setText(readableSize(down));

            return view;
        } catch (JSONException e) {
            Log.e("FundsAdapter", "Could not read block", e);
            return new TextView(getContext());
        }
    }
}
