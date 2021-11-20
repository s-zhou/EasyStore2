package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProduct extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView Text1;
    private Typeface Ruloko;
    private Spinner selectUnit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        String font1= "font/Ruluko-Regular.ttf";
        this.Ruloko= Typeface.createFromAsset(getAssets(),font1);
        Text1 = (TextView) findViewById(R.id.CreateProductText);
        Text1.setTypeface(Ruloko);

        Spinner colorSpinner = findViewById(R.id.selectUnitSpinner);
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(
                this,
                R.array.SprintItemsUnit,
                R.layout.color_spinner_layout
        );
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_unit_layout);
        colorSpinner.setAdapter(adapterColor);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, adapterView.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){

    }

}
