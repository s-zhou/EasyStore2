package com.example.easystore2.Recipe.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.R;

import java.util.ArrayList;

public class RecipeViewHolder extends RecyclerView.ViewHolder {
    TextView name;//,url, ingredients;
    ImageView image;

    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.recipeName);
        image = itemView.findViewById(R.id.recipeImage);
        //url = itemView.findViewById(R.id.expiredDate);
        //ingredients = itemView.findViewById(R.id.description);
    }
}
