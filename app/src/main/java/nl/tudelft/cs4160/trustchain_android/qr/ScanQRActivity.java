package nl.tudelft.cs4160.trustchain_android.qr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.jakewharton.processphoenix.ProcessPhoenix;

import org.libsodium.jni.Sodium;

import java.io.IOException;
import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import nl.tudelft.cs4160.trustchain_android.R;
import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.qr.exception.InvalidDualKeyException;
import nl.tudelft.cs4160.trustchain_android.qr.exception.QRWalletImportException;
import nl.tudelft.cs4160.trustchain_android.qr.exception.QRWalletParseException;
import nl.tudelft.cs4160.trustchain_android.qr.models.CoinTransfer;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRBlock;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRWallet;


public class ScanQRActivity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_CAMERA = 0;
    public static final String TAG = "ScanQRActivity";

    private Vibrator vibrator;
    private ZXingScannerView scannerView;

    private TrustChainBlockFactory trustChainBlockFactory = new TrustChainBlockFactory();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
            try {
                QRWallet wallet = processResult(result);
                QRBlock lastBlock = wallet.blocks.get(wallet.blocks.size()-1);
                CoinTransfer transaction = lastBlock.transaction;

                String message = "Successfully imported wallet\n New reputation : Up="
                        + transaction.totalUp + " Down=" + transaction.totalDown;
                new AlertDialog.Builder(ScanQRActivity.this)
                        .setTitle("Success")
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                ProcessPhoenix.triggerRebirth(ScanQRActivity.this);
                            }
                        }).show();
            } catch (QRWalletImportException exception) {
                Log.e(TAG, "Could not import QR Wallet", exception);
                new AlertDialog.Builder(ScanQRActivity.this)
                        .setTitle("Error")
                        .setMessage("Something went wrong processing the QR data")
                        .setNeutralButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                scannerView.resumeCameraPreview(scanResultHandler);
                            }
                        }).show();
            }
        }
    };

    private void startCamera() {
        scannerView.setResultHandler(scanResultHandler);
        scannerView.startCamera();
    }

    private QRWallet processResult(Result result) throws QRWalletImportException {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<QRWallet> walletAdapter = moshi.adapter(QRWallet.class);

        QRWallet wallet;
        try {
            wallet = walletAdapter.fromJson(result.getText());
        } catch (IOException e) {
            throw new QRWalletParseException(e);
        }
        if (wallet == null) {
            throw new QRWalletParseException("Null wallet");
        }

        byte[] keyBytes = Base64.decode(wallet.privateKeyBase64, Base64.DEFAULT);
        KeyPair keyPair = readKeyPair(keyBytes);
        Key.saveKeyPair(ScanQRActivity.this, keyPair);

        TrustChainDBHelper helper = new TrustChainDBHelper(this);
        for (QRBlock block : wallet.blocks) {
            // TODO: fix genesis block, clear database?
            helper.insertInDB(trustChainBlockFactory.createBlock(block));
        }
        return wallet;
    }

    private KeyPair readKeyPair(byte[] dualKey) throws InvalidDualKeyException{
        int pkLength = Sodium.crypto_box_secretkeybytes();
        int seedLength = Sodium.crypto_box_seedbytes();

        int expectedLength = pkLength + seedLength;
        if (dualKey.length != expectedLength) {
            throw new InvalidDualKeyException("Expected key length " + expectedLength + " but got " + dualKey.length);
        }

        byte[] pk = Arrays.copyOfRange(dualKey, 0, pkLength); // first group is pk
        byte[] seed = Arrays.copyOfRange(dualKey, pkLength, pkLength + seedLength); // second group is seed
        return new KeyPair(pk, seed);
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
