package com.example.lukoil;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zxing.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scan;
    public static String info;
    private Button btn, rescan;
    private ImageButton flash;
    private boolean flash_state = false;
    private String id1, id2, id3;
    private TextView txt;
    private ImageView ima0, ima1, ima2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initUI();

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
        String res = rawResult.getText();
        txt.setText(res);
        scan.resumeCameraPreview(MainActivity.this);
        if (res.length() == 36) {
            switch (res.charAt(0)) {
                case '0':                                   //код под крышкой
                    id1 = res;
                    ima0.setVisibility(View.GONE);
                    txt.setText("Под крышкой");
                    break;
                case '1':                                   //код на крышке
                    id2 = res;
                    ima1.setVisibility(View.GONE);
                    txt.setText("На крышке");
                    break;
                case '2':                                   //код на этикетке
                    id3 = res;
                    ima2.setVisibility(View.GONE);
                    txt.setText("На этикетке");
                    break;
                default:
                    txt.setText("Ошибка");
                    break;
            }
        } else
            txt.setText("Ошибка");

        if(id1 != null && id2 != null && id3 != null) {
            flash.setVisibility(View.GONE);
            rescan.setVisibility(View.VISIBLE);
            scan.resumeCameraPreview(MainActivity.this);
            scan.setFlash(false);
            scan.stopCamera();
            reqResult(id1 + '.' + id2 + '.' + id3);
        }
    }

    public void click(View view) {
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }

    public void rescan(View view) {
        rescan.setVisibility(View.GONE);
        flash.setVisibility(View.VISIBLE);
        btn.setClickable(false);
        btn.setText("Scanning...");

        scan.startCamera();

        if(flash_state)
            scan.setFlash(true);
        //
        ima0.setVisibility(View.VISIBLE);
        ima1.setVisibility(View.VISIBLE);
        ima2.setVisibility(View.VISIBLE);
        //
    }

    public void flash_btn(View view){
        if(flash_state){
            flash.setImageResource(R.drawable.ic_flash_off);
            scan.setFlash(false);
            flash_state = false;
        } else {
            flash.setImageResource(R.drawable.ic_flash_on);
            scan.setFlash(true);
            flash_state = true;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        this.finish();
    }

    private void initUI() {
        scan = (ZXingScannerView)findViewById(R.id.zxscan);
        btn = (Button)findViewById(R.id.button2);
        rescan = (Button)findViewById(R.id.btnRescan);
        flash = (ImageButton)findViewById(R.id.flash);

        //
        txt = (TextView)findViewById(R.id.test_info);
        ima0 = findViewById(R.id.ima0);
        ima1 = findViewById(R.id.ima1);
        ima2 = findViewById(R.id.ima2);
        //
        rescan.setVisibility(View.GONE);
        btn.setClickable(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void reqResult(String id){

        id1 = id2 = id3 = null;
        OkHttpClient client = new OkHttpClient();
        String url = "http://185.12.29.215/croc/DataBase/" + id;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                info = response.body().string();

                try {
                    JSONObject json = new JSONObject(info);
                    json.getString("TypeName");
                    //
                    btn.setClickable(true);
                    btn.setText("Tap to see the info");
                } catch (JSONException e){
                    btn.setText("Error");
                    btn.setClickable(false);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                txt.setText("Bad connection");
            }
        });
        /* error


         */
    }
}

/*

*/