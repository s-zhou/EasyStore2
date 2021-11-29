package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easystore2.data.model.Products;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateProduct extends AppCompatActivity implements View.OnClickListener {
    private TextView compCreatProductHeaderText;
    private EditText compExpiredDate, compProductNameText, compQuantityText, compDescriptionText;
    private Typeface Ruloko;
    private Products product;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    private Button compSaveNewProduct, compPlusQuantity, compLessQuantity, compCancel;
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
        compCancel.setOnClickListener(this);
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
        compCancel = (Button) findViewById(R.id.Cancel);
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
        categoryList.add("No categorizar");
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
            if(this.validation()){
                pushDB();
                startActivity(new Intent(CreateProduct.this, HomeStore.class));
            }
        }else if(v == compCancel){
            startActivity(new Intent(CreateProduct.this, HomeStore.class));
        }
        else if(v == compPlusQuantity){
            plusLess(1);
        }
        else if(v == compLessQuantity){
            plusLess(-1);
        }

    }

    private void pushDB() {
        product = new Products();
        product.setProductName(compProductNameText.getText().toString());
        product.setQuantity(compQuantityText.getText().toString()+ compQuantitySpinner.getSelectedItem().toString());
        product.setExpiredDate(compExpiredDate.getText().toString());
        String category=compCategoriSelectorSpinner.getSelectedItem().toString();
        if(category == "Selecciona categoria") category = "No categorizar";
        product.setCategory(category);
        product.setDescription(compDescriptionText.getText().toString());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference.child("UserProducts").child(uid).child(product.getIdProduct()).setValue(product);
        Toast.makeText(this, "creado", Toast.LENGTH_LONG).show();
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
