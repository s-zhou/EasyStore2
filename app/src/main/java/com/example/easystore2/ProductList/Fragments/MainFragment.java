package com.example.easystore2.ProductList.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.CalculateDate;
import com.example.easystore2.ProductList.Adapter.AdapterProducts;
import com.example.easystore2.ProductList.CreateProduct;
import com.example.easystore2.data.model.ProductRV;
import com.example.easystore2.R;
import com.example.easystore2.productListOperation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener{
    public AdapterProducts adapterProducts;
    RecyclerView productRecyclerView;

    ConstraintLayout load;
    ArrayList<ProductRV> listProductRV;
    productListOperation pLO = new productListOperation();
    ArrayList<ProductRV> tempList;
    ArrayList<String> allProductName;
    private String uid;
    private FirebaseUser user;
    private Button creatProductBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_sub_activity,container, false);
        load = view.findViewById(R.id.productLoadConstraint);
        load.setVisibility(View.GONE);
        creatProductBtn = view.findViewById(R.id.createProductBtn);
        creatProductBtn.setVisibility(View.GONE);
        creatProductBtn.setOnClickListener(this);
        productRecyclerView = view.findViewById(R.id.storeRecyclerView);
        listProductRV = new ArrayList<>();
        tempList = new ArrayList<>();
        allProductName = new ArrayList<>();
        //LOAD LIST
        loadList();
        tempList = listProductRV;

        return view;
    }

    public ArrayList<ProductRV> getListProductRV() {
        return listProductRV;
    }

    private void showListItems(ArrayList<ProductRV> list) {
        load.setVisibility(View.GONE);
        creatProductBtn.setVisibility(View.VISIBLE);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProducts = new AdapterProducts(getContext(), list);
        productRecyclerView.setAdapter(adapterProducts);
        adapterProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductRV p= list.get(productRecyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent( getActivity(), CreateProduct.class);
                intent.putExtra("name",p.getProductName());
                intent.putExtra("quantity",p.getProductQuantity());
                intent.putExtra("expiredDate",p.getProductExpiredDate());
                intent.putExtra("category",p.getProductCategory());
                intent.putExtra("description",p.getProductDescription());
                intent.putExtra("allProductName",allProductName);
                startActivity(intent);
            }
        });
    }

    public void loadList() {
        load.setVisibility(View.VISIBLE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
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
                        CalculateDate c = new CalculateDate();
                        String s = c.getState(expiredDate);
                        allProductName.add(name);
                        listProductRV.add(new ProductRV(name, quantity,expiredDate,category,description, unit, s));
                    }
                     listProductRV = pLO.orderByName(listProductRV);
                    showListItems(listProductRV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
            intent.putExtra("allProductName",allProductName);
            intent.putExtra("name","");

            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void orderBy(String orderBy){
        if(orderBy.equals("name"))
            listProductRV = pLO.orderByName(listProductRV);

        else if(orderBy.equals("data"))
            listProductRV = pLO.orderByData(listProductRV);
    }


    public void showCategory(String category) {
        tempList = pLO.showCategory(category,listProductRV);
        adapterProducts = new AdapterProducts(getContext(), tempList);
        productRecyclerView.setAdapter(adapterProducts);
        //para poder acceder a cada uno de los componentes
        showListItems(tempList);
    }


    public void search(String query) {
        ArrayList<ProductRV> tempAr = pLO.search(query,tempList);
        adapterProducts = new AdapterProducts(getContext(), tempAr);
        productRecyclerView.setAdapter(adapterProducts);
        showListItems(tempAr);
    }
}
