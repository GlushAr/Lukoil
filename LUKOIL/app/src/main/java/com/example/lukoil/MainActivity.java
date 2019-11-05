package com.example.lukoil;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.zxing.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scan;
    public static String info;
    private Switch _switch;
    private Button btn, rescan;
    private ImageView img;
    private boolean flash_state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = (ZXingScannerView)findViewById(R.id.zxscan);
        _switch = (Switch)findViewById(R.id.switch1);
        btn = (Button)findViewById(R.id.button2);
        rescan = (Button)findViewById(R.id.btnRescan);
        img = (ImageView)findViewById(R.id.imgLamp);

        rescan.setVisibility(View.GONE);
        btn.setClickable(false);
        btn.setBackgroundColor(Color.rgb(209, 194, 192));

        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    flash_state = false;
                    scan.setFlash(false);
                    img.setImageResource(R.drawable.light_off);
                } else {
                    flash_state = true;
                    scan.setFlash(true);
                    img.setImageResource(R.drawable.light_on);
                }
            }
        });

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scan.setResultHandler(MainActivity.this);
                        scan.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "accept this permission to continue", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        scan.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {

        info = rawResult.getText();
        btn.setClickable(true);
        btn.setText("Tap to see the info");
        btn.setBackgroundColor(Color.rgb(90, 145, 77));

        _switch.setVisibility(View.GONE);
        img.setVisibility(View.GONE);
        rescan.setVisibility(View.VISIBLE);
        scan.resumeCameraPreview(MainActivity.this);
        scan.setFlash(false);
        scan.stopCamera();
    }

    public void click(View view) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    public void rescan(View view) {
        rescan.setVisibility(View.GONE);
        _switch.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);
        btn.setClickable(false);
        btn.setText("Scanning...");
        btn.setBackgroundColor(Color.rgb(209, 194, 192));

        scan.startCamera();

        if(flash_state)
            scan.setFlash(true);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        this.finish();
    }
}
