package com.example.easystore2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CreateRecipeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image;
    TextView compAddImageMsn;
    Button compDeleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe_activity);
        image=findViewById(R.id.recipeImageIV);
        compAddImageMsn=findViewById(R.id.addImageTextView);
        compDeleteBtn=findViewById(R.id.deleteBtn);
        compDeleteBtn.setVisibility(View.GONE);
        compDeleteBtn.setOnClickListener(this);
        image.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            compAddImageMsn.setVisibility(View.GONE);
            compDeleteBtn.setVisibility(View.VISIBLE);
            image.setImageURI(resultUri);

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
    }

    public void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecione la aplicación"),10);
    }

}