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

import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;

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
            KeyPair keyPair = Key.loadKeys(this);
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
