package com.example.easystore2.Recipe;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easystore2.R;
import com.example.easystore2.Recipe.Adapter.AdapterRecipe;
import com.example.easystore2.Recipe.Adapter.AdapterRecipeFavorite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class RecipeFavoriteFragment extends Fragment {
    public boolean current;
    ConstraintLayout loadConstrait;
    RecyclerView recipeRecyclerView;
    AdapterRecipeFavorite adapterRecipe;
    ArrayList<Recipe> recipes = new ArrayList<>();
    public Context c;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_sub_activity, container, false);
        loadConstrait = view.findViewById(R.id.loadConstrant);
        loadConstrait.setVisibility(View.GONE);
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        loadFavoriteRecipeName();
        c=getContext();
        return view;
    }

    private void loadFavoriteRecipeName() {
        loadConstrait.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(uid).child("FavoriteRecipe").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("name").getValue().toString();
                        String image = prod.child("image").getValue().toString();
                        String url = prod.child("url").getValue().toString();
                        Iterable<DataSnapshot> ingredientsDS = prod.child("ingredients").getChildren();
                        ArrayList<String> ingredients = new ArrayList<>();
                        for (DataSnapshot i : ingredientsDS) ingredients.add(i.getValue().toString());
                        boolean fav = prod.child("favorite").getValue().toString().equals("true");
                        Recipe r = new Recipe(name, image, url, fav, 0, ingredients);
                        recipes.add(r);
                    }
                }
                showListItems(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showListItems(ArrayList<Recipe> list) {
        loadConstrait.setVisibility(View.GONE);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecipe = new AdapterRecipeFavorite(c, list);
        recipeRecyclerView.setAdapter(adapterRecipe);
        adapterRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe r = list.get(recipeRecyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra("name", r.getName());
                intent.putExtra("image", r.getImage());
                intent.putExtra("instructions", r.getIngredients());
                intent.putExtra("url", r.getUrl());
                intent.putExtra("like", r.isFavorite());//mirar
                startActivity(intent);
            }
        });
    }
}
