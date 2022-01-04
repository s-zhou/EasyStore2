package com.example.easystore2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.easystore2.data.model.ProductRV;

import java.util.ArrayList;
import java.util.Locale;

public class productListOperation {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ProductRV> orderByName(ArrayList<ProductRV> listProductRV){
        listProductRV.sort((d1, d2) -> (d1.getProductName().toUpperCase(Locale.ROOT)).compareTo(d2.getProductName().toUpperCase(Locale.ROOT)));
        return listProductRV;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ProductRV> orderByData(ArrayList<ProductRV> listProductRV){
        listProductRV.sort((d1, d2) -> (d1.getProductExpiredDate()).compareTo(d2.getProductExpiredDate()));
        return listProductRV;
    }

    public ArrayList<ProductRV> showCategory(String category,ArrayList<ProductRV> listProductRV) {
        ArrayList<ProductRV> tempList = new ArrayList<>();
        if(category.equals("") || category.equals("Todo")) tempList = listProductRV;
        else{
            for(int i= 0; i < listProductRV.size();++i){
                if (listProductRV.get(i).getProductCategory().equals(category)) {
                    tempList.add(listProductRV.get(i));
                }
            }
        }
        return tempList;
    }

}
