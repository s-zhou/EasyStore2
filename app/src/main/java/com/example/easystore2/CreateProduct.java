package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateProduct extends AppCompatActivity implements View.OnClickListener {
    private TextView compCreatProductHeaderText;
    private EditText compExpiredDate, compProductNameText, compQuantityText, compDescriptionText;
    private Typeface Ruloko;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Button compSaveNewProduct, compPlusQuantity, compLessQuantity;
    Spinner compQuantitySpinner, compCategoriSelectorSpinner;
    private boolean first = true;
    private int dayExpired, monthExpired, yearExpired;
    public ArrayList<String> categoryList= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        inicialiceComponents();
        textStyle();
        unitSelectorSpinner();
        expiredCalendar();
        categorySelectorSpinner();
        compSaveNewProduct.setOnClickListener(this);
        compPlusQuantity.setOnClickListener(this);
        compLessQuantity.setOnClickListener(this);
    }

    private boolean validation() {
        String productName = compProductNameText.getText().toString();
        if(productName.equals("")){
            compProductNameText.setError("Campo obligatorio");
            return false;
        }
        return true;
    }

    private void textStyle() {
        String font1= "font/Ruluko-Regular.ttf";
        this.Ruloko= Typeface.createFromAsset(getAssets(),font1);
        compCreatProductHeaderText.setTypeface(Ruloko);
    }

    private void inicialiceComponents() {
        compCreatProductHeaderText = (TextView) findViewById(R.id.CreateProductHeaderText);
        compProductNameText = (EditText) findViewById(R.id.productName);
        compQuantityText =(EditText) findViewById(R.id.quantityEditText);
        compQuantityText.setText("0");
        compQuantitySpinner = (Spinner) findViewById(R.id.selectUnitSpinner);
        compExpiredDate =(EditText) findViewById(R.id.ExpiredTextDate);
        compCategoriSelectorSpinner = (Spinner) findViewById(R.id.categorySelectorSpinner);
        compSaveNewProduct =(Button) findViewById(R.id.SaveNewProductBotton);
        compDescriptionText = (EditText) findViewById(R.id.descriptionTextMultiLine);
        compPlusQuantity = (Button) findViewById(R.id.plusButton);
        compLessQuantity = (Button) findViewById(R.id.lessButton);
    }

    private void expiredCalendar() {
        final Calendar c = Calendar.getInstance();
        dayExpired = c.get(Calendar.DAY_OF_MONTH);
        monthExpired = c.get(Calendar.MONTH);
        yearExpired = c.get(Calendar.YEAR);
        if(first){
            compExpiredDate.setText((dayExpired+1) + "/" + (monthExpired + 1) + "/" + yearExpired);
            first=false;
        }
        compExpiredDate.setOnClickListener(this);
    }

    private void unitSelectorSpinner(){
        ArrayAdapter adapterColor = ArrayAdapter.createFromResource(
                this,
                R.array.SprintItemsUnit,
                R.layout.color_spinner_layout
        );
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_unit_layout);
        compQuantitySpinner.setAdapter(adapterColor);

    }

    private void categorySelectorSpinner(){
        categoryList.add("Selecciona categoria");
        categoryList.add("Nevera");
        categoryList.add("Armario");
        ArrayAdapter adapterColor = new ArrayAdapter(
                this,
                R.layout.color_spinner_layout,
                categoryList
        );
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_unit_layout);
        compCategoriSelectorSpinner.setAdapter(adapterColor);

    }


    @Override
    public void onClick(View v) {
        if(v == compExpiredDate){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    compExpiredDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    dayExpired=dayOfMonth;
                    monthExpired = month+1;
                    yearExpired = year;
                }
            },yearExpired,monthExpired,dayExpired);
            datePickerDialog.show();
        }
        else if(v == compSaveNewProduct){
            if(this.validation()) pushDB();
        }
        else if(v == compPlusQuantity){
            plusLess(1);
        }
        else if(v == compLessQuantity){
            plusLess(-1);
        }

    }

    private void pushDB() {
        String productName = compProductNameText.getText().toString();
        String quantity = compQuantityText.getText().toString();
        String unit = compQuantitySpinner.getSelectedItem().toString();
        String dataExpired = compExpiredDate.getText().toString();
        String category = compCategoriSelectorSpinner.getSelectedItem().toString();
        String description = compDescriptionText.getText().toString();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void plusLess(int num) {
        String quantity = compQuantityText.getText().toString();
        if(quantity.contains(".")) {
            String[] quantityParts = quantity.split("\\.");
            int quantityInt = Integer.parseInt(quantityParts[0]) + num;
            quantity = String.valueOf(quantityInt)+"."+quantityParts[1];
        }
        else quantity = String.valueOf(Integer.parseInt(quantity) + num);
        compQuantityText.setText(quantity);
    }
}
