package com.example.easystore2.Adapter;


import android.content.Context;
import android.content.Intent;
import android.text.Layout;
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

import com.example.easystore2.ContinueWithActivity;
import com.example.easystore2.CreateProduct;
import com.example.easystore2.Entities.ProductRV;
import com.example.easystore2.Filters.SearchFilter;
import com.example.easystore2.HomeStore;
import com.example.easystore2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        View view = inflater.inflate(R.layout.product_list_item, parent, false);

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
        setExpiredProductColour(dataExpired);
        setAboutToExpiredProductColour(dataExpired);
        String category = model.get(position).getProductCategory();
        String description = model.get(position).getProductDescription();
        holder.productName.setText(name);
        holder.productQuantity.setText(quantity);
        holder.productExpiredDate.setText(dataExpired);
        holder.productCategory.setText(category);
        holder.unit.setText(unit);
        if(description.equals("")) description =" -";
        holder.productDescrition.setText(description);
    }

    private void setAboutToExpiredProductColour(String dataExpired) {
        try {
            Date expiredDate = dateFormat.parse(dataExpired);
            final Calendar c2 = Calendar.getInstance();
            final Calendar c1 = Calendar.getInstance();
            c2.setTime(expiredDate);
            c2.add(Calendar.DAY_OF_YEAR, -1);

            Date aboutToExpiredData2 = dateFormat.parse(setDataFormat(c2));
            Date currentDate = dateFormat.parse(setDataFormat(c1));


            if((currentDate.before(expiredDate) && currentDate.after(aboutToExpiredData2))||(currentDate.equals(expiredDate))||(currentDate.equals(aboutToExpiredData2))){
                comItem.setBackgroundColor(0xFFF6B95E);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String setDataFormat(Calendar c) {
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String d = String.valueOf(day);
        String m = String.valueOf(month);
        String y = String.valueOf(c.get(Calendar.YEAR));
        if(day<10) d ="0" + d;
        if(month<10) y = "0" + y;
        return (y + "-" + m + "-" + d);
    }

    private void setExpiredProductColour(String dataExpired) {
        try {
            final Calendar c = Calendar.getInstance();
            Date curretData = dateFormat.parse(setDataFormat(c));
            Date expiredData = dateFormat.parse(dataExpired);

            if(expiredData.before(curretData)){
                comItem.setBackgroundColor(0xE4FA8C84);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
    public Filter getFilter() {
        if(searchFilter == null) {
            searchFilter = new SearchFilter(filterList, this);
        }
        return  searchFilter;
    }
}
