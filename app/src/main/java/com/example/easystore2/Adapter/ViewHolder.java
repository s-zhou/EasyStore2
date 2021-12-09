package com.example.easystore2.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView productName, productQuantity,productExpiredDate, productCategory, productDescrition;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productNameText);
        productQuantity = itemView.findViewById(R.id.productQuantityText);
        productExpiredDate = itemView.findViewById(R.id.expiredDate);
        productCategory = itemView.findViewById(R.id.category);
        productDescrition = itemView.findViewById(R.id.description);
    }
}
