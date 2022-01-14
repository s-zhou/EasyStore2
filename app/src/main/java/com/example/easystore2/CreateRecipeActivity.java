package com.example.easystore2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.easystore2.Recipe.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CreateRecipeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image;
    TextView compAddImageMsn;
    EditText nameComp, descriptionComp, ingredientComp, instructionComp;
    Button compDeleteBtn, cancelBtn, noBtn, saveBtn;
    String imageUri="",originalName;
    String nameRecipe,imageRecipe,descriptionRecipe,ingredientRecipe,instructionRecipe;
    boolean favorite,modification, imageChanced;
    int numIngredientStore;
    FirebaseUser user ;
    DatabaseReference databaseReference;
    ArrayList<String> ingredients=new ArrayList<>();
    Context context;
    Recipe recipe;
    String[] p;
    AlertDialog dialog;
    StorageReference mStorage;
    private Uri resultUriImage;
    private ConstraintLayout processBar, noneImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe_activity);
        linkComponents();
        modification=false;
        context=this;
        imageChanced=false;
        resultUriImage = Uri.parse("android.resource://" + getPackageName() +"/"+R.drawable._642037847251);
        imageUri = resultUriImage.getLastPathSegment().toString()+".jpg";
        compDeleteBtn.setVisibility(View.GONE);
        processBar.setVisibility(View.GONE);

        loadInfoCom();
        compDeleteBtn.setOnClickListener(this);
        image.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }

    private void linkComponents() {
        image=findViewById(R.id.recipeImageView);
        nameComp =findViewById(R.id.recipeName2);
        descriptionComp =findViewById(R.id.descriptionName3);
        ingredientComp =findViewById(R.id.ingredientesTV);
        instructionComp =findViewById(R.id.ingredientesTV2);
        compAddImageMsn=findViewById(R.id.addImageTextView);
        compDeleteBtn=findViewById(R.id.deleteBtn);
        noneImage=findViewById(R.id.noneImageLayout);
        processBar = findViewById(R.id.processLayout);
        noBtn=findViewById(R.id.noBtn);
        saveBtn=findViewById(R.id.SaveBtn);
        cancelBtn = findViewById(R.id.createRecipeToolbar).findViewById(R.id.Cancel);
    }

    private void loadInfoCom() {
        Bundle parameters = this.getIntent().getExtras();
        if(parameters!=null) {
            processBar.setVisibility(View.VISIBLE);
            compAddImageMsn.setVisibility(View.GONE);
            modification=true;
            nameRecipe = parameters.getString("name");
            originalName = nameRecipe;
            nameComp.setText(nameRecipe);
            descriptionRecipe = parameters.getString("description");
            descriptionComp.setText(descriptionRecipe);
            descriptionComp.setVisibility(View.VISIBLE);
            ingredientRecipe = parameters.getString("ingredients");
            ingredientComp.setText(ingredientRecipe);
            instructionComp.setVisibility(View.VISIBLE);
            instructionRecipe = parameters.getString("instruction");
            instructionComp.setText(instructionRecipe);
            imageRecipe = parameters.getString("image");
            imageUri=imageRecipe;
            String[] parts = imageUri.split("\\%2F");
            p= parts[3].split("\\?alt=");
            Glide.with(this)
                    .load(imageRecipe)
                    .centerCrop()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            noneImage.setVisibility(View.VISIBLE);
                            processBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            noneImage.setVisibility(View.VISIBLE);
                            processBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP) {
            imageChanced = true;
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
         nameRecipe= nameComp.getText().toString();
         imageRecipe=imageUri;
         descriptionRecipe= descriptionComp.getText().toString();
         instructionRecipe= instructionComp.getText().toString();
         favorite=false;
         numIngredientStore=0;
         ingredients=StringToArray(ingredientComp.getText().toString());

    }

    private void validation() {
        if(nameRecipe.equals("")){
            nameComp.setError("Campo obligatorio");
        }

        else {
            databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(originalName).removeValue();
            if (!nameRecipe.equals(originalName)) {
                databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(nameRecipe).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            dialog();
                            pushDB();
                        } else {
                            nameComp.setError("Ya existe este nombre");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                dialog();
                pushDB();
            }
        }
    }


    private void pushImage(){
        StorageReference filePath=mStorage.child("User").child(user.getUid()).child("RecipeImage");
        if(!imageChanced && modification)filePath= filePath.child(p[0]);
        else filePath= filePath.child(resultUriImage.getLastPathSegment());
        filePath.putFile(resultUriImage).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(recipe.getName()).removeValue();
                Toast.makeText(context, R.string.imposibleCreation, Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                finish();
                if(modification){
                    finish();
                    Toast.makeText(context, R.string.modifiedAndImageRetard, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, R.string.createdAndImageRetard, Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRecipeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.my_recipe_log_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    private void pushDB() {
        String imageURIDif;
        if(modification) imageURIDif=p[0];
        else imageURIDif=resultUriImage.getLastPathSegment();
        recipe = new Recipe(nameRecipe,imageURIDif, descriptionRecipe,instructionRecipe,true,favorite,numIngredientStore,ingredients);
        databaseReference.child("User").child(user.getUid()).child("MisRecetas").child(recipe.getName()).setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pushImage();
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(context, R.string.imposibleCreation, Toast.LENGTH_LONG).show();
            }
        });
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
