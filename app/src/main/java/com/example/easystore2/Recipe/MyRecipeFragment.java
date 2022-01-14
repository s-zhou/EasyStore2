package com.example.easystore2.Recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.easystore2.CreateRecipeActivity;
import com.example.easystore2.R;
import com.example.easystore2.Recipe.Adapter.AdapterRecipe;
import com.example.easystore2.Recipe.RecipeDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MyRecipeFragment extends Fragment implements View.OnClickListener {
    Button createRecipeBtn;
    ConstraintLayout loadConstraint;
    RecyclerView recipeRecyclerView;
    AdapterRecipe adapterRecipe;
    public Context c;
    TextView noneRecipe;
    ArrayList<Recipe> recipes = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_sub_activity,container, false);
        createRecipeBtn = view.findViewById(R.id.creatRecipeBtn);
        createRecipeBtn.setOnClickListener(this);
        loadConstraint = view.findViewById(R.id.loadConstrant);
        loadConstraint.setVisibility(View.GONE);
        noneRecipe = view.findViewById(R.id.recipeNoneTV);
        noneRecipe.setVisibility(View.GONE);

        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        c=getContext();
        loadRecipe();
        return view;
    }

    private void loadRecipe() {
        loadConstraint.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(uid).child("MisRecetas").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipes.clear();
                boolean end=false;
                if (snapshot.exists()) {
                    noneRecipe.setVisibility(View.GONE);
                    for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("name").getValue().toString();
                        String instruction = prod.child("instruction").getValue().toString();
                        String description = prod.child("description").getValue().toString();
                        String image = prod.child("image").getValue().toString();
                        Iterable<DataSnapshot> ingredientsDS = prod.child("ingredients").getChildren();
                        ArrayList<String> ingredients = new ArrayList<>();
                        for (DataSnapshot i : ingredientsDS) ingredients.add(i.getValue().toString());
                        boolean fav = prod.child("favorite").getValue().toString().equals("true");
                        boolean mine = prod.child("mine").getValue().toString().equals("true");
                        Recipe r = new Recipe(name,image,description, instruction, mine,fav, 0, ingredients);
                        recipes.add(r);
                    }
                    loadImage();
                }
                else{
                    noneRecipe.setVisibility(View.VISIBLE);
                    loadConstraint.setVisibility(View.GONE);
                    showListItems(recipes);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("User").child(user.getUid()).child("RecipeImage");
        int i=0;

        for(Recipe r: recipes){
            ++i;
            StorageReference filePathImage= filePath.child(r.getImage());
            int finalI = i;
            filePathImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    r.setImage(uri.toString());
                    if(finalI ==recipes.size())  showListItems(recipes);
                }
            });
        }
    }

    private void createRecipe() {
        Intent intent = new Intent(getContext(), CreateRecipeActivity.class);
        startActivity(intent);
    }

    private void showListItems(ArrayList<Recipe> list) {
        loadConstraint.setVisibility(View.GONE);

        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecipe = new AdapterRecipe(c, list);
        recipeRecyclerView.setAdapter(adapterRecipe);
        adapterRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe r = list.get(recipeRecyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                intent.putExtra("name", r.getName());
                intent.putExtra("image", r.getImage());
                intent.putExtra("ingredients", r.getIngredients());
                intent.putExtra("description", r.getDescription());
                intent.putExtra("instruction", r.getInstruction());
                boolean mine= r.isMine();
                intent.putExtra("mine",mine);
                intent.putExtra("like", r.isFavorite());//mirar
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==createRecipeBtn){
            createRecipe();
        }
    }


}