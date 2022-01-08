package com.example.easystore2.Recipe;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Recipe {
    String name;
    String image;
    String url;
    int point;
    ArrayList<String> ingredients=new ArrayList<>();

    public Recipe(@NonNull String name, String image, String url, int point, ArrayList<String> ingredients) {
        this.name = name;
        this.image = image;
        this.url = url;
        this.point = point;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
