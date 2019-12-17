package com.example.lukoil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zxing.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {

    private TextView txt;
    private ImageView img;
    private String name, produced, place, number, batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initUI();

        JsonParser(MainActivity.info);
    }

    public void click(View v) {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
    } //обработчик нажатия на кнопку again

    private void initUI() {
        txt = (TextView)findViewById(R.id.textView2);
        img = (ImageView)findViewById(R.id.imageView);
    } //инициализация пользовательского интерфейса

    private void JsonParser(String jsonInfo) {
        try {
            JSONObject json = new JSONObject(jsonInfo);
            name = json.getString("TypeName");
            produced = json.getString("Produced");
            place = json.getString("Place");
            number = json.getString("Number");
            batch = json.getString("Batch");
            byte[] decodedStr = Base64.decode(json.getString("Image"), Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(decodedStr, 0, decodedStr.length);
            img.setImageBitmap(bmp);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) img.getLayoutParams();

            params.width -= 70;
            params.height -= 70;
            params.bottomToBottom = 40;
            params.bottomToTop = 40;
            img.setLayoutParams(params);

            txt.setText("Название: "+ name + '\n' + "Дата выпуска: " + produced + '\n' + "Место производства: " + place + '\n' + "Серийный номер: " + number + '\n' + "Партия:" + batch);

        } catch (JSONException e){
            txt.setText(e.getMessage());
        }

    } //получение информации из JSON'а

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
    } //отвечает за полноэкранный режим
}
