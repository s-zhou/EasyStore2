package com.example.easystore2.Recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class RecipeDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView nameComp, ingredientsComp;
    Button goUrlComp, backComp,favoriteComp;
    ImageView imageComp;
    ArrayList<String> ingredientsLines = new ArrayList<>();
    String name, ingredients, url, image;
    RequestQueue request;
    Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);
        associateComponents();
        loadInfo();

        backComp.setOnClickListener(this);
        favoriteComp.setOnClickListener(this);
    }

    private void associateComponents() {
        nameComp = findViewById(R.id.recipeNameTV);
        ingredientsComp = findViewById(R.id.ingredientListTextView);
        goUrlComp = findViewById(R.id.goInstruccionBtn);
        imageComp = findViewById(R.id.recipeImageView);
        backComp =findViewById(R.id.recipeBackBtn);
        favoriteComp =findViewById(R.id.recipeFavoriteBtn);

    }

    private void loadInfo() {
        Bundle parameters = this.getIntent().getExtras();
        name =parameters.getString("name");
        nameComp.setText(name);
        ingredientsLines =parameters.getStringArrayList("instructions");
        ingredients =  loadListIngredients(ingredientsLines);
        ingredientsComp.setText(ingredients);

        url = parameters.getString("url");
        goUrlComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        image = parameters.getString("image");
        request = Volley.newRequestQueue(this);
        ImageRequest imageRequest = new ImageRequest(image, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageComp.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        request.add(imageRequest);

        boolean like =parameters.getBoolean("like");
        if(like)favoriteComp.setBackgroundResource(R.drawable.favorite_select_24);
        else favoriteComp.setBackgroundResource(R.drawable.favorite_unselect_24);

        recipe = new Recipe(name,image,url,like,0,ingredientsLines);
    }

    private String loadListIngredients(ArrayList<String> ingredientLines) {
        String ingredients="";
        for(String i: ingredientLines){
            ingredients += i+"\n";
        }
        return ingredients;

    }


    @Override
    public void onClick(View v) {
        if(v==backComp){
            finish();
        }
        else if(v==favoriteComp){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
            DatabaseReference ref = databaseReference.child("User").child(user.getUid()).child("FavoriteRecipe").child(recipe.getName());
            if(recipe.isFavorite()){
                favoriteComp.setBackgroundResource(R.drawable.favorite_unselect_24);
                recipe.setFavorite(false);
                ref.removeValue();
            }
            else {
                favoriteComp.setBackgroundResource(R.drawable.favorite_select_24);
                recipe.setFavorite(true);
                ref.setValue(recipe);
            }
        }
    }

}