package com.example.easystore2.Recipe;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Recipe {
    String name;
    String image;
    String description;
    String instruction;
    boolean favorite = false;
    int numIngredientStore;
    ArrayList<String> ingredients=new ArrayList<>();


    public Recipe(String name, String image, String description, String instruction, boolean favorite, int numIngredientStore, ArrayList<String> ingredients) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.instruction = instruction;
        this.favorite = favorite;
        this.numIngredientStore = numIngredientStore;
        this.ingredients = ingredients;
    }


    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getNumIngredientStore() {
        return numIngredientStore;
    }

    public void setNumIngredientStore(int numIngredientStore) {
        this.numIngredientStore = numIngredientStore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
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


    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }
}
