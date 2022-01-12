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
import android.widget.Toolbar;

public class CreateRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    ImageView image;
    Button loadImageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_recipe_activity);
        image=findViewById(R.id.imageView);
        loadImageBtn = findViewById(R.id.loadRecipeImageBtn);
        loadImageBtn.setOnClickListener(this);
    }

    public void loadImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecione la aplicación"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri path =data.getData();
            image.setImageURI(path);
        }
    }

    @Override
    public void onClick(View v) {
        if(loadImageBtn==v) loadImage();
    }
}