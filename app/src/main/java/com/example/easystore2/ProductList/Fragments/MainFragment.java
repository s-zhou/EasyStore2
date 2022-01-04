package com.example.easystore2.ProductList.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.ProductList.Adapter.AdapterProducts;
import com.example.easystore2.CreateProduct;
import com.example.easystore2.ProductList.Entities.ProductRV;
import com.example.easystore2.R;
import com.example.easystore2.productList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment implements View.OnClickListener{
    public AdapterProducts adapterProducts;
    RecyclerView productRecyclerView;
    ArrayList<ProductRV> listProductRV;
    ArrayList<ProductRV> listProductRVFix;

    private String uid;

    SimpleDateFormat dateFormat = new SimpleDateFormat ("dd/MM/yyyy");

    private FirebaseUser user;
    private Button creatProductBtn;
    FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_sub_activity,container, false);
        creatProductBtn = view.findViewById(R.id.createProductBtn);
        creatProductBtn.setOnClickListener(this);
        productRecyclerView = view.findViewById(R.id.storeRecyclerView);
        listProductRV = new ArrayList<>();
        //LOAD LIST
        loadList();


        return view;
    }

    private void showListItems(ArrayList<ProductRV> tempList) {
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProducts = new AdapterProducts(getContext(), tempList);
        productRecyclerView.setAdapter(adapterProducts);
        adapterProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductRV p= tempList.get(productRecyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent( getActivity(), CreateProduct.class);
                intent.putExtra("name",p.getProductName());
                intent.putExtra("quantity",p.getProductQuantity());
                intent.putExtra("expiredDate",p.getProductExpiredDate());
                intent.putExtra("category",p.getProductCategory());
                intent.putExtra("description",p.getProductDescription());
                startActivity(intent);
            }
        });
    }

    private void loadList() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        databaseReference.child("User").child(uid).child("Products").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("productName").getValue().toString();
                        String quantity = prod.child("quantity").getValue().toString();
                        String expiredDate = prod.child("expiredDate").getValue().toString();
                        String category = prod.child("category").getValue().toString();
                        String unit = prod.child("unit").getValue().toString();
                        String description = prod.child("description").getValue().toString();
                        String s = getState(expiredDate);
                        listProductRV.add(new ProductRV(name, quantity,expiredDate,category,description, unit, s));
                    }
                     productList p =new productList();
                     listProductRV = p.orderBy("name",listProductRV);
                     listProductRVFix =listProductRV;
                    showListItems(listProductRV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static String getState(String dataExpired) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
            Date expiredDate = dateFormat.parse(dataExpired);
            final Calendar c2 = Calendar.getInstance();
            c2.setTime(expiredDate);
            c2.add(Calendar.DAY_OF_YEAR, -1);

            Date currentDate = dateFormat.parse(setDataFormat(Calendar.getInstance()));

            Date aboutToExpiredData2 = dateFormat.parse(setDataFormat(c2));
            if((currentDate.before(expiredDate) && currentDate.after(aboutToExpiredData2))||(currentDate.equals(expiredDate))||(currentDate.equals(aboutToExpiredData2))){
                return "about";
            }
            else if(expiredDate.before(currentDate)){
                return "expired";
            }
            else return "ok";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "ok";
    }


    private static String setDataFormat(Calendar c) {
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String d = String.valueOf(day);
        String m = String.valueOf(month);
        String y = String.valueOf(c.get(Calendar.YEAR));
        if(day<10) d ="0" + d;
        if(month<10) y = "0" + y;
        return (y + "-" + m + "-" + d);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(v ==creatProductBtn){
            Intent intent = new Intent(getActivity(), CreateProduct.class);
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void orderBy(String orderBy){
        productList  p = new productList();
        listProductRV = p.orderBy(orderBy,listProductRV);
    }


    public void showCategory(String category) {
        productList  p = new productList();
        ArrayList<ProductRV> tempList = p.showCategory(category,listProductRV);
        adapterProducts = new AdapterProducts(getContext(), tempList);
        productRecyclerView.setAdapter(adapterProducts);
        showListItems(tempList);
    }


    public void search(String query) {
        ArrayList<ProductRV> tempAr = new ArrayList<>();
        if(query.length()==0){
            tempAr = listProductRV;
        }
        else{
            for (ProductRV c : listProductRV) {
                if (c.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    tempAr.add(c);
                }
            }
        }
        adapterProducts = new AdapterProducts(getContext(), tempAr);
        productRecyclerView.setAdapter(adapterProducts);
        showListItems(tempAr);
    }
}
