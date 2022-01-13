package com.example.easystore2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easystore2.Recipe.Recipe;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CreateRecipeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image;
    TextView compAddImageMsn;
    EditText name, description,ingredient,instruction;
    Button compDeleteBtn, cancelBtn, noBtn, saveBtn;
    String imageUri="";
    String nameRecipe,imageRecipe,descriptionRecipe,instructionRecipe;
    boolean favorite;
    int numIngredientStore;
    FirebaseUser user ;
    DatabaseReference databaseReference;
    ArrayList<String> ingredients=new ArrayList<>();
    Context context;
    StorageReference mStorage;
    private Uri resultUriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe_activity);
        linkComponents();
        context=this;
        resultUriImage = Uri.parse("android.resource://" + getPackageName() +"/"+R.drawable._642037847251);

        compDeleteBtn.setVisibility(View.GONE);
        compDeleteBtn.setOnClickListener(this);
        image.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
    }

    private void linkComponents() {
        image=findViewById(R.id.recipeImageView);
        name=findViewById(R.id.recipeName2);
        description=findViewById(R.id.descriptionName3);
        ingredient=findViewById(R.id.ingredientesTV);
        instruction=findViewById(R.id.ingredientesTV2);
        compAddImageMsn=findViewById(R.id.addImageTextView);
        compDeleteBtn=findViewById(R.id.deleteBtn);
        noBtn=findViewById(R.id.noBtn);
        saveBtn=findViewById(R.id.SaveBtn);
        cancelBtn = findViewById(R.id.createRecipeToolbar).findViewById(R.id.Cancel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP) {
            resultUriImage = UCrop.getOutput(data);
            compAddImageMsn.setVisibility(View.GONE);
            compDeleteBtn.setVisibility(View.VISIBLE);
            imageUri=resultUriImage.toString();
            image.setImageURI(resultUriImage);
        }
        else if(resultCode==RESULT_OK){
            Uri path =data.getData();
            UCrop.Options options = new UCrop.Options();

            String dest_uri = new StringBuffer(UUID.randomUUID().toString()).append(".jpg").toString();
            UCrop.of(path,Uri.fromFile(new File(getCacheDir(),dest_uri)))
                    .withOptions(options)
                    .withAspectRatio(3,2)
                    .withMaxResultSize(2000,2000)
                    .start(CreateRecipeActivity.this);
            }

    }

    @Override
    public void onClick(View v) {
        if(image==v) {
            loadImage();
        }
        else if(compDeleteBtn==v){
            image.setImageResource(R.drawable._642037847251);
            compDeleteBtn.setVisibility(View.GONE);
            compAddImageMsn.setVisibility(View.VISIBLE);
        }
        else if(cancelBtn==v||noBtn==v) finish();
        else if(saveBtn==v){
            try {
                setValues();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadFirebaseInfo();
            validation();

        }
    }

    private void loadFirebaseInfo() {
        mStorage= FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        FirebaseApp.initializeApp(this);
    }

    private void setValues() throws IOException {
         nameRecipe=name.getText().toString();
        imageRecipe=imageUri;
        descriptionRecipe=description.getText().toString();
         instructionRecipe=instruction.getText().toString();
         favorite=false;
         numIngredientStore=0;
         ingredients=StringToArray(ingredient.getText().toString());

    }

    private void validation() {
        if(nameRecipe.equals("")){
            name.setError("Campo obligatorio");
        }
        else {
            databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(nameRecipe).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        pushDB();

                        finish();
                    }
                    else{
                        name.setError("Ya existe este nombre");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void pushImage() {
        StorageReference filePath = mStorage.child("User").child(user.getUid()).child("RecipeImage").child(resultUriImage.getLastPathSegment());
        filePath.putFile(resultUriImage);
    }

    private void pushDB() {
        //public Recipe(

        Recipe recipe = new Recipe(nameRecipe,resultUriImage.getLastPathSegment(), descriptionRecipe,instructionRecipe,favorite,numIngredientStore,ingredients);
        databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(recipe.getName()).setValue(recipe);
        pushImage();
        Toast.makeText(this, R.string.created, Toast.LENGTH_LONG).show();
        //else Toast.makeText(this, "Modificado", Toast.LENGTH_LONG).show();
    }

    private ArrayList<String> StringToArray(String ingredientString) {
        String[] parts = ingredientString.split("\\n");
        return new ArrayList<>(Arrays.asList(parts));
    }

    public void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecione la aplicación"),10);
    }

}