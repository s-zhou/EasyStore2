package com.example.easystore2.Recipe.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.easystore2.R;
import com.example.easystore2.Recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecipe extends RecyclerView.Adapter<RecipeViewHolder> implements View.OnClickListener {
    LayoutInflater inflater;
    public ArrayList<Recipe> model;
    private View.OnClickListener listener;
    RequestQueue request;
    Context c;

    public AdapterRecipe(Context context, ArrayList<Recipe> model){
        this.inflater = LayoutInflater.from(context);
        this.model = model;
        c=context;
    }
    public AdapterRecipe(){};

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
        request = Volley.newRequestQueue(inflater.getContext());
        holder.name.setText(name);
        holder.image.setImageResource(R.drawable._642037847251);
        Glide.with(c)
                .load(model.get(position).getImage())
                .centerCrop()
                .into(holder.image);

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
