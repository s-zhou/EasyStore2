package com.example.easystore2.ProductList.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.ProductList.Entities.ProductRV;
import com.example.easystore2.ProductList.Filters.SearchFilter;
import com.example.easystore2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterProducts extends RecyclerView.Adapter<ViewHolder> implements View.OnClickListener, Filterable {
    LayoutInflater inflater;
    public ArrayList<ProductRV> model, filterList;
    SearchFilter searchFilter;
    RelativeLayout comItem;
    SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
    private Button dropdownBtn;
    private ConstraintLayout productListItemLayout;
    private TextView expiredDateTV, categoryTV, descriptionTV;
    private View.OnClickListener listener;
    private LinearLayout modifyProductText;
    private Context context;
    private ProductRV p;
    public AdapterProducts(Context context, ArrayList<ProductRV> model){
        this.inflater = LayoutInflater.from(context);
        this.model = model;
        this.context = context;
        this.filterList = model;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.store_product_list_item, parent, false);

        productListItemLayout = view.findViewById(R.id.productListItemLayout);
        comItem = view.findViewById(R.id.productListItemRL);
        comItem.setBackgroundColor(0xffffffff);
        dropdownBtn = view.findViewById(R.id.productDropdown);
        dropdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expiredDateTV = view.findViewById(R.id.expiredDate);
                categoryTV = view.findViewById(R.id.category);
                descriptionTV = view.findViewById(R.id.description);
                if(expiredDateTV.getVisibility() == View.GONE){
                    expiredDateTV.setVisibility(View.VISIBLE);
                }
                else{
                    expiredDateTV.setVisibility(View.GONE);
                }if(categoryTV.getVisibility() == View.GONE){
                    categoryTV.setVisibility(View.VISIBLE);
                }
                else{
                    categoryTV.setVisibility(View.GONE);
                }if(descriptionTV.getVisibility() == View.GONE){
                    descriptionTV.setVisibility(View.VISIBLE);
                }
                else{
                    descriptionTV.setVisibility(View.GONE);
                }
            }
        });
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
        String unit = model.get(position).getUnit();
        String dataExpired = model.get(position).getProductExpiredDate();
        String category = model.get(position).getProductCategory();
        String description = model.get(position).getProductDescription();
        holder.productName.setText(name);
        holder.productQuantity.setText(quantity);
        holder.productExpiredDate.setText(dataExpired);
        holder.productCategory.setText(category);
        holder.unit.setText(unit);
        if(description.equals("")) description =" -";
        holder.productDescrition.setText(description);
        if(model.get(position).getState().equals("expired")) comItem.setBackgroundColor(0xE4FA8C84);
        else if(model.get(position).getState().equals("about")) comItem.setBackgroundColor(0xFFF6B95E);
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

    @Override
    public SearchFilter getFilter() {
        if(searchFilter == null) {
            searchFilter = new SearchFilter(filterList, this);
        }
        return  searchFilter;
    }
}
