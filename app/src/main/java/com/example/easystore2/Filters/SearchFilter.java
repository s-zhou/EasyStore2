package com.example.easystore2.Filters;

import android.widget.Filter;

import com.example.easystore2.Adapter.AdapterProducts;
import com.example.easystore2.Entities.ProductRV;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFilter extends Filter {
    ArrayList<ProductRV> filterList;
    AdapterProducts adapter;
    public SearchFilter(ArrayList<ProductRV> filterList, AdapterProducts adapter) {
        this.filterList = filterList;
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ProductRV> modelFilter = new ArrayList<>();
            for(int i=0; i< filterList.size();++i){
                if(filterList.get(i).getProductName().toUpperCase().contains(constraint)){
                    modelFilter.add(filterList.get(i));
                }
            }
            results.count = modelFilter.size();
            results.values = modelFilter;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.model = (ArrayList<ProductRV>) results.values;
            adapter.notifyDataSetChanged();
    }
}
