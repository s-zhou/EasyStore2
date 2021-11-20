package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeStore extends AppCompatActivity {
    private TextView Text1;
    private Typeface Ruloko;
    TextView tvBoton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_store);
        String font1= "font/Ruluko-Regular.ttf";
        this.Ruloko= Typeface.createFromAsset(getAssets(),font1);
        Text1 = (TextView) findViewById(R.id.textView);
        Text1.setTypeface(Ruloko);
    }

    public void creatProduct(View view){
        startActivity(new Intent(HomeStore.this, CreateProduct.class));
    }
}