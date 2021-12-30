package com.example.easystore2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easystore2.data.model.Products;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateProduct extends AppCompatActivity implements View.OnClickListener {
    private EditText compExpiredDate, compProductNameText, compQuantityText, compDescriptionText;
    private Products product;
    private Context context;
    private LinearLayout comDeleteEditBtn;
    private ArrayList<String> newCategories = new ArrayList<String>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    private Button compSaveNewProduct, compPlusQuantity, compLessQuantity, compCancel, addCategory, comSaveProduct, comDeleteProduct;
    Spinner compQuantitySpinner, compCategoriSelectorSpinner;
    private boolean first = true;
    private int dayExpired, monthExpired, yearExpired;
    public ArrayList<String> categoryList= new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_create_product);
        associateComponents();
        unitSelectorSpinner();
        expiredCalendar();
        initializeComponentValues();
        compSaveNewProduct.setOnClickListener(this);
        compPlusQuantity.setOnClickListener(this);
        compLessQuantity.setOnClickListener(this);
        compCancel.setOnClickListener(this);
        addCategory.setOnClickListener(this);
        comSaveProduct.setOnClickListener(this);
        comDeleteProduct.setOnClickListener(this);
    }

    private void initializeComponentValues() {
        Bundle parameters = this.getIntent().getExtras();
        String productCat = "Sin categorizar";
        if(parameters != null){
            productCat =parameters.getString("category");
            compProductNameText.setText(parameters.getString("name"));
            compQuantityText.setText(parameters.getString("quantity"));
            compExpiredDate.setText(parameters.getString("expiredDate"));
            compDescriptionText.setText(parameters.getString("description"));
            compSaveNewProduct.setVisibility(View.GONE);
            comDeleteEditBtn.setVisibility(View.VISIBLE);

        }
        else{
            compSaveNewProduct.setVisibility(View.VISIBLE);
            comDeleteEditBtn.setVisibility(View.GONE);
        }
        setCategoriesSpinner(productCat);
    }

    private void setCategoriesSpinner(String category) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(user.getUid()).child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ArrayList<String> newCategori = new ArrayList<String>();
                    for (DataSnapshot cat : snapshot.getChildren()) {
                        String catName = cat.getValue().toString();
                        newCategori.add(catName);
                    }
                    categoryList = newCategori;
                }
                int catPos = categoryList.indexOf(category);

                if(catPos==-1){
                    categoryList.add("Sin categorizar");
                    catPos = categoryList.indexOf(category);
                }
                categorySelectorSpinner(catPos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
        compQuantityText.setText("1");
        compQuantitySpinner = (Spinner) findViewById(R.id.selectUnitSpinner);
        compExpiredDate =(EditText) findViewById(R.id.ExpiredTextDate);
        compCategoriSelectorSpinner = (Spinner) findViewById(R.id.categorySelectorSpinner);
        compSaveNewProduct =(Button) findViewById(R.id.SaveNewProductBotton);
        compDescriptionText = (EditText) findViewById(R.id.descriptionTextMultiLine);
        compPlusQuantity = (Button) findViewById(R.id.plusButton);
        compLessQuantity = (Button) findViewById(R.id.lessButton);
        compCancel = (Button) findViewById(R.id.Cancel);
        addCategory= (Button) findViewById(R.id.addCategoryBtn);
        comSaveProduct= (Button) findViewById(R.id.SaveBtn);
        comDeleteProduct= (Button) findViewById(R.id.noBtn);
        comDeleteEditBtn = (LinearLayout) findViewById(R.id.deleteEditBtn);
    }

    private void expiredCalendar() {
        final Calendar c = Calendar.getInstance();
        dayExpired = c.get(Calendar.DAY_OF_MONTH);
        monthExpired = c.get(Calendar.MONTH);
        yearExpired = c.get(Calendar.YEAR);
        String data =yearExpired+ "-";
        if(first){
            if(monthExpired<10) data = data + "0" + (monthExpired + 1) + "-";
            else data = data + (monthExpired + 1) + "-";
            if(dayExpired<10) data = data+ "0"+(dayExpired+1);
            else  data = data + (dayExpired+1);

            compExpiredDate.setText(data);
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

    private void categorySelectorSpinner(int catPos){
        ArrayAdapter adapterColor = new ArrayAdapter(
                this,
                R.layout.color_spinner_layout,
                categoryList
        );
        adapterColor.setDropDownViewResource(R.layout.spinner_dropdown_unit_layout);
        compCategoriSelectorSpinner.setAdapter(adapterColor);
        compCategoriSelectorSpinner.setSelection(catPos);

    }


    @Override
    public void onClick(View v) {
        if(v == compExpiredDate){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    //compExpiredDate.setText(year+ "-" + (month+1) + "-" + dayOfMonth );
                    String data = year +"-";
                    if(month<10) data = data + "0" + (month + 1) + "-" ;
                    else data = data + (month + 1) + "-" ;
                    if(dayOfMonth<10) data = data+ "0"+(dayOfMonth);
                    else  data = data + (dayOfMonth);

                    compExpiredDate.setText(data);
                    dayExpired=dayOfMonth;
                    monthExpired = month+1;
                    yearExpired = year;
                }
            },yearExpired,monthExpired,dayExpired);
            datePickerDialog.show();
        }
        else if(v == compSaveNewProduct || comSaveProduct ==v){
            if(this.validation()){
                pushDB();
                startActivity(new Intent(CreateProduct.this, MainActivityNavBar.class));
                finish();
            }
        }else if(v == compCancel){
            startActivity(new Intent(CreateProduct.this, MainActivityNavBar.class));
            finish();

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
        else if(comDeleteProduct == v){
            confiMenssage();

        }
    }

    private void confiMenssage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProduct.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_confirmation, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button siBtn = view.findViewById(R.id.siBtn);
        siBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
                Toast.makeText(getApplicationContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateProduct.this, MainActivityNavBar.class));
                finish();
            }
        });
        Button noBtn = view.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void deleteProduct() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(user.getUid()).child("Products").child(compProductNameText.getText().toString()).removeValue();


    }

    private void pushDB() {
        product = new Products();
        product.setProductName(compProductNameText.getText().toString());
        product.setQuantity(compQuantityText.getText().toString());
        product.setUnit(compQuantitySpinner.getSelectedItem().toString());
        product.setExpiredDate(compExpiredDate.getText().toString());
        String category=compCategoriSelectorSpinner.getSelectedItem().toString();
        product.setCategory(category);
        product.setDescription(compDescriptionText.getText().toString());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference.child("User").child(uid).child("Products").child(product.getProductName()).setValue(product);

        if(this.getIntent().getExtras()==null)Toast.makeText(this, "Creado", Toast.LENGTH_LONG).show();
        else Toast.makeText(this, "Modificado", Toast.LENGTH_LONG).show();
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
    private void mostrarDialogoPersonalizado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProduct.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        EditText txt = view.findViewById(R.id.newCategoryEditTxt);

        Button btnSave = view.findViewById(R.id.SaveBtn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                FirebaseApp.initializeApp(context);
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference.child("User").child(uid).child("Categories").child(txt.getText().toString()).setValue(txt.getText().toString());
                setCategoriesSpinner(txt.getText().toString());
                Toast.makeText(getApplicationContext(), "Nueva categoria guardado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Button Cancel = view.findViewById(R.id.noBtn);
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }



    }
