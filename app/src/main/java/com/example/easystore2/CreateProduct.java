package com.example.easystore2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class CreateProduct extends AppCompatActivity implements View.OnClickListener {
    private EditText compExpiredDate, compProductNameText, compQuantityText, compDescriptionText;
    private Products product;
    private ArrayList<String> newCategories = new ArrayList<String>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    private Button compSaveNewProduct, compPlusQuantity, compLessQuantity, compCancel, addCategory;
    Spinner compQuantitySpinner, compCategoriSelectorSpinner;
    private boolean first = true;
    private int dayExpired, monthExpired, yearExpired;
    public ArrayList<String> categoryList= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        associateComponents();
        unitSelectorSpinner();
        expiredCalendar();
        categorySelectorSpinner();
        initializeComponentValues();
        compSaveNewProduct.setOnClickListener(this);
        compPlusQuantity.setOnClickListener(this);
        compLessQuantity.setOnClickListener(this);
        compCancel.setOnClickListener(this);
        addCategory.setOnClickListener(this);
    }

    private void initializeComponentValues() {
        Bundle parameters = this.getIntent().getExtras();

        if(parameters != null){
            compProductNameText.setText(parameters.getString("name"));
            compQuantityText.setText(parameters.getString("quantity"));
            compExpiredDate.setText(parameters.getString("expiredDate"));
            compCategoriSelectorSpinner.setSelection(parameters.getInt("category"));
            compDescriptionText.setText(parameters.getString("description"));
        }
    }

    private boolean validation() {
        String productName = compProductNameText.getText().toString();
        int quantity = Integer.parseInt(compQuantityText.getText().toString());

        if(productName.equals("")){
            compProductNameText.setError("Campo obligatorio");
            return false;
        }
        if(quantity<=0){
            compQuantityText.setError("Cantidad no puede ser negativo o 0");
            return false;
        }
        return true;
    }
    private void associateComponents() {
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
        addCategory= (Button) findViewById(R.id.addCategoryBtn);
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
        categoryList.add("Sin categorizar");
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
            if(this.validation()){
                pushDB();
                startActivity(new Intent(CreateProduct.this, MainActivityNavBar.class));
            }
        }else if(v == compCancel){
            startActivity(new Intent(CreateProduct.this, MainActivityNavBar.class));
        }
        else if(v == compPlusQuantity){
            plusLess(1);
        }
        else if(v == compLessQuantity){
            plusLess(-1);
        }
        else if(v == addCategory) {
            mostrarDialogoPersonalizado();
        }
    }

    private void pushDB() {
        product = new Products();
        product.setProductName(compProductNameText.getText().toString());
        product.setQuantity(compQuantityText.getText().toString());
        product.setUnit(compQuantitySpinner.getSelectedItem().toString());
        product.setExpiredDate(compExpiredDate.getText().toString());
        String category=String.valueOf(compCategoriSelectorSpinner.getSelectedItemPosition());
        product.setCategory(category);
        product.setDescription(compDescriptionText.getText().toString());
        product.setCategoryAdded(newCategories);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference.child("UserProducts").child(uid).child(product.getProductName()).setValue(product);
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
    private void mostrarDialogoBasico(){
        final EditText edittext = new EditText(getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(edittext);
        builder.setTitle("Introduce el nombre de la nueva categoria")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Eliminamos datos...",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Cancel...",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    //TODO set btn Cancel onClickListener
    private void mostrarDialogoPersonalizado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProduct.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog, null);

        builder.setView(view);

        //TODO BOTONES POR DEFECTO
        /**
         builder.setView(inflater.inflate(R.layout.dialog_personalizado,null))
         .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getApplicationContext(),"Conectando...",Toast.LENGTH_SHORT).show();
        }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
        }
        });
         */

        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText txt = view.findViewById(R.id.newCategoryEditTxt);

        Button btnSave = view.findViewById(R.id.SaveBtn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCategories.add(txt.getText().toString());
                Toast.makeText(getApplicationContext(), "Guardado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Button Cancel = view.findViewById(R.id.CancelBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }



    }
