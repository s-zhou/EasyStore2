package com.example.easystore2.data.model;

import java.util.ArrayList;

public class Products {
    private String productName,quantity, expiredDate, category, description, unit;

    public Products(String ProductName, String Quantity, String ExpiredDate, String Category, String Description, String unit) {
        this.productName = ProductName;
        this.quantity = Quantity;
        this.expiredDate = ExpiredDate;
        this.category= Category;
        this.description= Description;
        this.unit = unit;
    }

    public Products(){}

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
