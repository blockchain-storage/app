package nl.tudelft.cs4160.trustchain_android.qr;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import nl.tudelft.cs4160.trustchain_android.Peer;
import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.connection.Communication;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;

import static nl.tudelft.cs4160.trustchain_android.block.TrustChainBlock.createBlock;
import static nl.tudelft.cs4160.trustchain_android.block.TrustChainBlock.sign;

public class ExportWalletQRActivity extends AppCompatActivity {
    final static String TAG = "ExportWalletQRActivity";
    ImageView imageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_wallet_qr);
        imageView = findViewById(R.id.qr_image);
        progressBar = findViewById(R.id.qr_progress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                exportQRCode();
            }
        }).start();
    }

    private void exportQRCode() {
        try {
            // Step 1: Create a temporary new identity (we call this C)
            KeyPair keyPairOfC = Key.createNewKeyPair();
            KeyPair keyPairOfA = Key.loadKeys(this);

            // Step 2: Transfer the funds from our current wallet (A) to (C).
            // - * Create Partially Signed Block from A
            // - * Create Partially Signed Block from C

            // Partly duplicated code from Communication.java
            byte[] transaction = "temp".getBytes();

            // TODO: This should be the dbHelper of the chain of A, not a temporary placeholder.
            TrustChainDBHelper dbHelper = new TrustChainDBHelper(this);
            MessageProto.TrustChainBlock blockAtoC =
                    createBlock(
                            transaction,
                            dbHelper,
                            keyPairOfA.getPublicKey().toBytes(),
                            null,
                            keyPairOfC.getPublicKey().toBytes()
                    );
            blockAtoC = sign(blockAtoC, keyPairOfA.getPrivateKey());

            TrustChainDBHelper dbHelperC = new TrustChainDBHelper(this);
            MessageProto.TrustChainBlock blockCtoA =
                    createBlock(
                            transaction, // TODO: Should be the inverse of transaction A->C
                            dbHelper,
                            keyPairOfC.getPublicKey().toBytes(),
                            blockAtoC,
                            keyPairOfA.getPublicKey().toBytes()
                    );
            blockCtoA = sign(blockCtoA, keyPairOfC.getPrivateKey());

            // Step 3: Construct data to put in QR code, so the receiver can construct
            // Both C -> B and B -> C Blocks
            // - * Create Partially Signed Block from C -> B

            // TODO: Move this into BootstrapBlock.java (cleanup)
            JSONObject transferBlock = new JSONObject();
            try {
                transferBlock.put("public_key", keyPairOfC.getPublicKey().toBytes());
                transferBlock.put("tx", transaction);
                transferBlock.put("link_public_key", ""); //TODO:
                transferBlock.put("signature", ""); //TODO:
                transferBlock.put("previous_hash", ""); //TODO:
                transferBlock.put("sequence_number", ""); //TODO:
                transferBlock.put("link_sequence_number", ""); //TODO:
            } catch (JSONException e) {
                // TODO: Error Handling
            }

            // Step 4: Display QR code with data
            System.out.println("Encoding " + transferBlock.toString() + " as QR code!");
            String keyString = Base64.encodeToString(transferBlock.toString().getBytes(), Base64.DEFAULT);
            MultiFormatWriter writer = new MultiFormatWriter();
            int size = 500;
            BitMatrix matrix = writer.encode(keyString, BarcodeFormat.QR_CODE, size, size);
            final Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < size; i++) {//width
                for (int j = 0; j < size; j++) {//height
                    image.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayQRCode(image);
                }
            });

            // Old Logic, Exporting the current private key (backup option, not export)
            /*KeyPair keyPair = Key.loadKeys(this);
            byte[] privateKey = keyPair.getPrivateKey().toBytes();
            byte[] seedKey = keyPair.getSeed();
            byte[] concatted = new byte[privateKey.length + seedKey.length];
            System.arraycopy(privateKey, 0, concatted, 0, privateKey.length);
            System.arraycopy(seedKey, 0, concatted, privateKey.length, seedKey.length);

            String keyString = Base64.encodeToString(concatted, Base64.DEFAULT);
            MultiFormatWriter writer = new MultiFormatWriter();
            int size = 500;
            BitMatrix matrix = writer.encode(keyString, BarcodeFormat.QR_CODE, size, size);
            final Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < size; i++) {//width
                for (int j = 0; j < size; j++) {//height
                    image.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayQRCode(image);
                }
            });
            */
        } catch (WriterException e) {
            Log.e(TAG, "Could not export QR code:", e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayError();
                }
            });
        }
    }

    private void displayQRCode(Bitmap bitmap) {
        progressBar.setVisibility(View.GONE);
        imageView.setImageBitmap(bitmap);
    }

    private void displayError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Could not export QR code.")
                .setNeutralButton(android.R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                })
                .show();
    }
}
