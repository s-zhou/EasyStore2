package com.example.easystore2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.Entities.Product;
import com.example.easystore2.R;

import java.util.ArrayList;

public class AdapterProducts extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener {
    LayoutInflater inflater;
    ArrayList<Product> model;
    private View.OnClickListener listener;
    public AdapterProducts(Context context, ArrayList<Product> model){
        this.inflater = LayoutInflater.from(context);
        this.model = model;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_list_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = model.get(position).getProductName();
        String quantity = model.get(position).getProductQuantity();
        holder.productName.setText(name);
        holder.productQuantity.setText(quantity);
    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

}
