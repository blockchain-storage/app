package nl.tudelft.cs4160.trustchain_android.qr;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRBlock;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRTransaction;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRWallet;

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
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<QRWallet> walletAdapter = moshi.adapter(QRWallet.class);
            TrustChainDBHelper dbHelper = new TrustChainDBHelper(this);

            // Step 1: Create a temporary new identity (we call this C)
            KeyPair keyPairOfC = Key.createNewKeyPair();
            KeyPair keyPairOfA = Key.loadKeys(this);

            // Step 2: Transfer the funds from our current wallet (A) to (C).
            // - * Create Partially Signed Block from A
            // - * Create Partially Signed Block from C

            // Partly duplicated code from Communication.java
            QRTransaction transaction = new QRTransaction();
            try {
                MessageProto.TrustChainBlock lastBlock = dbHelper.getAllBlocks().get(0); //(keyPairOfA.getPublicKey().toBytes());
                JSONObject object = new JSONObject(lastBlock.getTransaction().toStringUtf8());
                transaction.up = object.getInt("up");
                transaction.down = object.getInt("down");
                transaction.totalUp = object.getInt("totalUp");
                transaction.totalDown = object.getInt("totalDown");
            } catch (Exception e) {
                Log.e(TAG, "Could not export QR code, chain data might be corrupted: ", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayError();
                    }
                });
            }

            // TODO: This should be the dbHelper of the chain of A, not a temporary placeholder.
            // TODO: Append this block to the local chain.
            MessageProto.TrustChainBlock blockAtoC =
                    createBlock(
                            transaction.toString().getBytes(),
                            dbHelper,
                            keyPairOfA.getPublicKey().toBytes(),
                            null,
                            keyPairOfC.getPublicKey().toBytes()
                    );
            blockAtoC = sign(blockAtoC, keyPairOfA.getPrivateKey());

            TrustChainDBHelper dbHelperC = new TrustChainDBHelper(this);
            MessageProto.TrustChainBlock blockCtoA =
                    createBlock(
                            transaction.toString().getBytes(),
                            dbHelper,
                            keyPairOfC.getPublicKey().toBytes(),
                            blockAtoC,
                            keyPairOfA.getPublicKey().toBytes()
                    );
            blockCtoA = sign(blockCtoA, keyPairOfC.getPrivateKey());

            // Step 3: Construct data to put in QR code, so the receiver can construct
            // Both C -> B and B -> C Blocks
            // - * Create Partially Signed Block from C -> B

            QRBlock block = new QRBlock();
            block.blockHashBase64 = Integer.toHexString(blockCtoA.hashCode());
            block.sequenceNumber = blockCtoA.getSequenceNumber();

            // Put everything in a wallet
            QRWallet wallet = new QRWallet();
            wallet.privateKeyBase64 = keyPairOfC.getPrivateKey().toString();
            wallet.block = block;
            wallet.transaction = transaction;

            String jsonEncoded = walletAdapter.toJson(wallet);

            // Step 4: Display QR code with data
            System.out.println("Encoding " + jsonEncoded + " as QR code!");
            String keyString = Base64.encodeToString(jsonEncoded.getBytes(), Base64.DEFAULT);
            MultiFormatWriter writer = new MultiFormatWriter();
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int size = metrics.widthPixels;
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
