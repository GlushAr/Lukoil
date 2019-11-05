package com.example.lukoil;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zxing.R;

public class ResultActivity extends AppCompatActivity {

    private TextView txt;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txt = (TextView)findViewById(R.id.textView2);
        txt.setText(MainActivity.info);
        img = (ImageView)findViewById(R.id.imageView);
        img.setImageResource(R.drawable.logo_lukoil);
    }

    public void click(View v){
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    }
}
