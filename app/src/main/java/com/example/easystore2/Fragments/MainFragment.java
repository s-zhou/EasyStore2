package com.example.easystore2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.Adapter.AdapterProducts;
import com.example.easystore2.ContinueWithActivity;
import com.example.easystore2.CreateProduct;
import com.example.easystore2.Entities.Product;
import com.example.easystore2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainFragment extends Fragment implements View.OnClickListener{
    AdapterProducts adapterProducts;
    RecyclerView productRecyclerView;
    ArrayList<Product> listProduct;
    private String uid;

    private FirebaseUser user;
    private Button creatProductBtn;
    FirebaseDatabase firebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_product_list,container, false);
        creatProductBtn = view.findViewById(R.id.createProductBtn);
        creatProductBtn.setOnClickListener(this);
        productRecyclerView = view.findViewById(R.id.storeRecyclerView);
        listProduct = new ArrayList<>();
        //LOAD LIST
        loadList();

        //SHOW LIST
        showListItems();
        return view;
    }

    private void showListItems() {
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterProducts = new AdapterProducts(getContext(),listProduct);
        productRecyclerView.setAdapter(adapterProducts);
    }

    private void loadList() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        databaseReference.child("UserProducts").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                     for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("productName").getValue().toString();
                        String quantity = prod.child("quantity").getValue().toString();
                        listProduct.add(new Product(name, quantity));
                    }
                    showListItems();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void cerrarSesionHome(View view) {
        AuthUI.getInstance().signOut( getActivity()).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task){
                Toast.makeText( getActivity(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                Intent intent = new Intent( getActivity(), ContinueWithActivity.class);
                startActivity(intent);
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
            startActivity(new Intent( getActivity(), CreateProduct.class));
        }
    }
}
