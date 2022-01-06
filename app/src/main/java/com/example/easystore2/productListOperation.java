package com.example.easystore2;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.easystore2.data.model.ProductRV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class productListOperation {
    int repeatedData = 0;

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
    public ArrayList<ProductRV> search(String query, ArrayList<ProductRV> listProductRV) {
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
        return tempAr;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ProductRV> orderByPreference(ArrayList<ProductRV> listProductRV) {
        ArrayList<ProductRV> temp = orderByData(listProductRV);
        temp = deleteExpiredProduct(temp);
        listProductRV =temp;
        List<ProductRV> result;
        List<ProductRV> tempListP1, tempListP2;
        if(repeatedData>2) {
            tempListP1 = orderByQuantity(temp.subList(0, repeatedData));
            if((repeatedData+1)<=temp.size()) {
                tempListP2 = temp.subList(repeatedData, temp.size());
                listProductRV = (ArrayList<ProductRV>) Stream.concat(tempListP1.stream(), tempListP2.stream()).collect(Collectors.toList());
            }

        }
        return listProductRV;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<ProductRV> orderByQuantity(List<ProductRV> listProductRV) {
        ArrayList<ProductRV> temp= new ArrayList<>();
        boolean added=false;
        temp.add(listProductRV.get(0));
        for(int i=1; i<listProductRV.size(); ++i) {
            ProductRV newProduct=listProductRV.get(i);
            added=false;
            for(int j=0; j<temp.size() && !added; ++j) {
                ProductRV compProduct=temp.get(j);
                String n=newProduct.getUnit();
                double qn=conversion(newProduct.getProductQuantity(),newProduct.getUnit());
                double qc = conversion(compProduct.getProductQuantity(),compProduct.getUnit());
                if(qn >= qc){
                    temp.add(j,newProduct);
                    added=true;
                }
            }
            if(!added)
                temp.add(newProduct);
        }
        return  temp;
    }



    private Double conversion(String productQuantity, String unit) {
        double num = 1.0;
        if(unit.equals("L")|| unit.equals("kg")){
           num = 1000;
        }
        else if(unit.equals("unidad")){
            num= 250;
        }
        return (Double.parseDouble(productQuantity) *num);
    }


    private ArrayList<ProductRV> deleteExpiredProduct(ArrayList<ProductRV> productList) {
        ArrayList<ProductRV> tempList= new ArrayList<>();
        String data="";
        for(ProductRV p: productList){
            if(!p.getState().equals("expired")){
                tempList.add(p);
                //we can see how many products with the same about date
                if(data.equals("") || data.equals(p.getProductExpiredDate())){
                    ++repeatedData;
                    data=p.getProductExpiredDate();
                }
            }
        }
        return tempList;
    }

    public ArrayList<String> getOnlyListName(ArrayList<ProductRV> productListOrdered) {
        if(productListOrdered.size()>20) {
            productListOrdered = (ArrayList<ProductRV>) productListOrdered.subList(0, 20);
        }
        ArrayList<String> nameList = new ArrayList<>();
        for(ProductRV p : productListOrdered){
            nameList.add(p.getProductName());
        }
        return nameList;
    }
}
