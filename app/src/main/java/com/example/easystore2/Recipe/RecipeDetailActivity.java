package com.example.easystore2.Recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
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
import com.example.easystore2.R;


public class RecipeDetailActivity extends AppCompatActivity {
    TextView nameComp, ingredientsComp;
    Button goUrlComp;
    ImageView imageComp;
    RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);
        associateComponents();
        loadInfo();
    }

    private void associateComponents() {
        nameComp = findViewById(R.id.recipeNameTV);
        ingredientsComp = findViewById(R.id.ingredientListTextView);
        goUrlComp = findViewById(R.id.goInstruccionBtn);
        imageComp = findViewById(R.id.recipeImageView);
    }

    private void loadInfo() {
        Bundle parameters = this.getIntent().getExtras();
       // String =parameters.getString("category");
        String name =parameters.getString("name");
        nameComp.setText(name);
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
}