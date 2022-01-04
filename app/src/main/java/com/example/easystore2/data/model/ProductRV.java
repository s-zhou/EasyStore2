package com.example.easystore2.data.model;

import java.io.Serializable;

public class ProductRV implements Serializable {
    private String productName;
    private String productQuantity;
    private String productExpiredDate;
    private String productCategory;
    private String productDescription;
    private String unit;
    private String state;
    public ProductRV() {
    }
    public ProductRV(String productName, String productQuantity, String productExpiredDate, String productCategory, String productDescription, String unit, String state) {
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productExpiredDate = productExpiredDate;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.unit = unit;

        this.state = state;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductExpiredDate() {
        return productExpiredDate;
    }

    public void setProductExpiredDate(String productExpiredDate) {
        this.productExpiredDate = productExpiredDate;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}