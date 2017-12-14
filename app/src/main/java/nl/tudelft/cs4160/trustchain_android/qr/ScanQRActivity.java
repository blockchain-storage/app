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
import android.util.Base64;
import android.util.Log;

import com.google.zxing.Result;

import org.libsodium.jni.Sodium;

import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;

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
        byte[] decoded = Base64.decode(result.getText(), Base64.DEFAULT);
        int pkLength = Sodium.crypto_box_secretkeybytes();
        int vkLength = Sodium.crypto_box_seedbytes();

        int expectedLength = pkLength + vkLength;
        if (decoded.length != expectedLength) {
            Log.i(TAG, "QR data " + result.getText() + " doesn't match expected key length of " + expectedLength);
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("The scanned QR code doesn't seem to be a wallet key.")
                    .setNeutralButton(android.R.string.ok, null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            scannerView.resumeCameraPreview(scanResultHandler);
                        }
                    }).show();
            return;
        }

        byte[] pk = Arrays.copyOfRange(decoded, 0, pkLength); // first group is pk
        byte[] vk = Arrays.copyOfRange(decoded, pkLength, pkLength + vkLength); // second group is seedkey // TODO: find out what this maps to in the jni bindings of libsodium

        KeyPair pair = new KeyPair(pk);
        Key.saveKeyPair(ScanQRActivity.this, pair);

        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("Successfully imported wallet.")
                .setNeutralButton(android.R.string.ok, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ScanQRActivity.this.finish();
                    }
                }).show();
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
