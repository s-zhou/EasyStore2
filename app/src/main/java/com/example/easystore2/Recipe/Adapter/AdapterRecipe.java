package com.example.easystore2.Recipe.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.R;
import com.example.easystore2.Recipe.Recipe;

import java.util.ArrayList;

public class AdapterRecipe extends RecyclerView.Adapter<RecipeViewHolder> implements View.OnClickListener {
    LayoutInflater inflater;
    public ArrayList<Recipe> model;
    private View.OnClickListener listener;

    public AdapterRecipe(Context context, ArrayList<Recipe> model){
        this.inflater = LayoutInflater.from(context);
        this.model = model;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        view.setOnClickListener(this);
        return new RecipeViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        String name = model.get(position).getName();
        String image = model.get(position).getImage();
        //String url = model.get(position).getUrl();
        //String ingredients = model.get(position).getIngredients().toString();

        holder.name.setText(name);

        //holder.image.setImageURI();
        //holder.url.setText(url);
        //holder.ingredients.setText(ingredients);
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


}
