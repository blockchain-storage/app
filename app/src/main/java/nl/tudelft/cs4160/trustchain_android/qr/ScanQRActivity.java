package nl.tudelft.cs4160.trustchain_android.qr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;


public class ScanQRActivity extends AppCompatActivity {
    private Vibrator vibrator;

    private ZXingScannerView scannerView;

    public static final int PERMISSIONS_REQUEST_CAMERA = 0;

    public static final String TAG = "ScanQRActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        scannerView = findViewById(R.id.scanner_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Camera request permission handling
        if (hasCameraPermission()) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestCameraPermission();
                    }
                };

                new AlertDialog.Builder(this).setTitle(R.string.camera_permissions_required)
                        .setMessage(R.string.camera_permisions_required_long)
                        .setNeutralButton(android.R.string.ok, null)
                        .setOnDismissListener(dismissListener)
                        .show();
            } else {
                requestCameraPermission();
            }
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                finish();
            }
        }
    }

    private ZXingScannerView.ResultHandler scanResultHandler = new ZXingScannerView.ResultHandler() {
        public void handleResult(Result result) {
            vibrator.vibrate(100);
            processResult(result);
        }
    };

    private void startCamera() {
        scannerView.setResultHandler(scanResultHandler);
        scannerView.startCamera();
    }

    private void processResult(Result result) {
        try {
            JSONObject json = new JSONObject(result.getText());
            BootstrapBlock bootstrap = new BootstrapBlock(json);

            KeyPair pair = bootstrap.getKeyPair();
            Key.saveKeyPair(ScanQRActivity.this, pair);

            TrustChainDBHelper helper = new TrustChainDBHelper(this);
            for (MessageProto.TrustChainBlock block : bootstrap.blocks) {
                helper.insertInDB(block);
            }

            String message = "Successfully imported wallet\n New reputation : Up="
                    + bootstrap.total_up + " Down=" + bootstrap.total_down;
            new AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage(message)
                    .setNeutralButton(android.R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            ScanQRActivity.this.finish();
                        }
                    }).show();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Something whent wrong processing the QR data")
                    .setNeutralButton(android.R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            scannerView.resumeCameraPreview(scanResultHandler);
                        }
                    }).show();
            return;

        }

    }

    private boolean hasCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}

