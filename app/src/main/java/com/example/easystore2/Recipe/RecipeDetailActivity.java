package com.example.easystore2.Recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.MainActivityNavBar;
import com.example.easystore2.ProductList.CreateProduct;
import com.example.easystore2.R;

import java.util.ArrayList;


public class RecipeDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView nameComp, ingredientsComp;
    Button goUrlComp, backComp,favoriteComp;
    ImageView imageComp;

    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);
        associateComponents();
        loadInfo();

        backComp.setOnClickListener(this);
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
        String name =parameters.getString("name");
        nameComp.setText(name);
        
        String ingredients =  loadListIngredients(parameters.getStringArrayList("instructions"));
        ingredientsComp.setText(ingredients);

        String url = parameters.getString("url");
        goUrlComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        String image = parameters.getString("image");
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
        else if(v==favoriteComp){}
    }
}