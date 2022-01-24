package com.example.easystore2.Recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.easystore2.CreateRecipeActivity;
import com.example.easystore2.MainActivityNavBar;
import com.example.easystore2.ProductList.CreateProduct;
import com.example.easystore2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class RecipeDetailActivity extends AppCompatActivity implements View.OnClickListener {
    TextView nameComp, ingredientsComp, ingredientTVcomp, instructionTVcomp,descriptionTVcomp;
    Button goUrlComp, backComp,favoriteComp,editBtn, docBtn;
    ImageView imageComp;
    LinearLayout docLayout;
    ConstraintLayout processBar;
    ArrayList<String> ingredientsLines = new ArrayList<>();
    String name, ingredients, instruction, image, description,doc;
    boolean mine, like;
    DatabaseReference ref;
    Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);
        associateComponents();
        docLayout.setVisibility(View.GONE);
        imageComp.setImageResource(R.drawable._642037847251);
        editBtn.setVisibility(View.GONE);
        loadInfo();
        backComp.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        favoriteComp.setOnClickListener(this);
        docBtn.setOnClickListener(this);
    }

    private void associateComponents() {
        nameComp = findViewById(R.id.recipeNameTV);
        ingredientTVcomp = findViewById(R.id.ingredientTV);
        instructionTVcomp = findViewById(R.id.instrucionTV);
        ingredientsComp = findViewById(R.id.ingredientListTextEditor);
        goUrlComp = findViewById(R.id.goInstruccionBtn);
        processBar = findViewById(R.id.processLayout);
        editBtn = findViewById(R.id.editBtn);
        imageComp = findViewById(R.id.recipeImageView);
        docLayout = findViewById(R.id.docLayout);
        backComp =findViewById(R.id.recipeBackBtn);
        favoriteComp =findViewById(R.id.recipeFavoriteBtn);
        docBtn =findViewById(R.id.showFile);
        descriptionTVcomp =findViewById(R.id.descriptionTV);
    }

    private void loadInfo() {
        processBar.setVisibility(View.VISIBLE);
        Bundle parameters = this.getIntent().getExtras();
        name = parameters.getString("name");
        nameComp.setText(name);
        description =  parameters.getString("description");
        if(description.equals("")) descriptionTVcomp.setVisibility(View.GONE);
        else {
            descriptionTVcomp.setVisibility(View.VISIBLE);
            descriptionTVcomp.setText(description);
        }
        ingredientsLines = parameters.getStringArrayList("ingredients");
        ingredients =  loadListIngredients(ingredientsLines);
        ingredientsComp.setText(ingredients);

        mine =parameters.getBoolean("mine");
        instruction = parameters.getString("instruction");

        if(mine) {
            editBtn.setVisibility(View.VISIBLE);
            goUrlComp.setVisibility(View.GONE);
            instructionTVcomp.setVisibility(View.VISIBLE);
            instructionTVcomp.setText(instruction);
        }
        else{
            instructionTVcomp.setVisibility(View.GONE);
            goUrlComp.setVisibility(View.VISIBLE);
            goUrlComp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(instruction));
                    startActivity(intent);
                }
            });
        }

        image = parameters.getString("image");
        Glide.with(this)
                .load(image)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        processBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        processBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageComp);

        like =parameters.getBoolean("like");
        if(like)favoriteComp.setBackgroundResource(R.drawable.favorite_select_24);
        else favoriteComp.setBackgroundResource(R.drawable.favorite_unselect_24);
        doc= parameters.getString("doc");
        if(!doc.equals("")) {
            docLayout.setVisibility(View.VISIBLE);
            docBtn.setText(doc);
        }
        recipe = new Recipe(name,image,"" ,instruction,doc,mine,like,0,ingredientsLines);
    }

    private String loadListIngredients(ArrayList<String> ingredientLines) {
        String ingredients="";
        int size=ingredientLines.size();
        for(int i=0; i<size;++i){
            ingredients += ingredientLines.get(i);
            if(i !=(size-1))ingredients+="\n";
        }
        return ingredients;

    }


    @Override
    public void onClick(View v) {
        if(v==backComp){
            finish();
        }
        else if(v==editBtn){
            loadEditRecipe();
        }
        else if(v==docBtn){
            showDoc();
        }
        else if(v==favoriteComp){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
            ref = databaseReference.child("User").child(user.getUid()).child("FavoriteRecipe").child(recipe.getName());
            if(recipe.isFavorite()){
                dialog();

            }
            else {
                favoriteComp.setBackgroundResource(R.drawable.favorite_select_24);
                recipe.setFavorite(true);
                ref.setValue(recipe);
                like=true;
            }
            if(mine) databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(recipe.getName()).child("favorite").setValue(recipe.isFavorite());

        }
    }

    private void showDoc() {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference();
        StorageReference filePath = mStorage.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("RecipeDoc").child(doc);
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

    }

    private void loadEditRecipe() {
        Intent intent=new Intent(this, CreateRecipeActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("image",image);
        intent.putExtra("ingredients",ingredients);
        intent.putExtra("instruction",instruction);
        intent.putExtra("description", description);
        intent.putExtra("doc", doc);
        intent.putExtra("mine",mine);
        intent.putExtra("like",like);//mirar
        startActivity(intent);
        finish();
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeDetailActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.recepe_favorite_confi_delete_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button siBtn = view.findViewById(R.id.recipeFilterBtn);
        siBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteComp.setBackgroundResource(R.drawable.favorite_unselect_24);
                recipe.setFavorite(false);
                ref.removeValue();
                dialog.dismiss();
                like=false;
            }
        });
        Button noBtn = view.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}