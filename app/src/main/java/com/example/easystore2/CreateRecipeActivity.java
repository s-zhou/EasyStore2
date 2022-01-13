package com.example.easystore2;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Toolbar;

import com.example.easystore2.Recipe.CropperActivity;

public class CreateRecipeActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image;
    TextView compAddImageMsn;
    Button compDeleteBtn;
    ActivityResultLauncher<String> mGetContent;
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
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent = new Intent(CreateRecipeActivity.this, CropperActivity.class);
                intent.putExtra("DATA",result.toString());
                startActivityForResult(intent,101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==-1 && requestCode==101){
           String result = data.getStringExtra("RESULT");
           Uri resultUri=null;

           if(result != null) {
               resultUri=Uri.parse(result);
               compAddImageMsn.setVisibility(View.GONE);
               compDeleteBtn.setVisibility(View.VISIBLE);
           }
            image.setImageURI(resultUri);
        }
    }

    @Override
    public void onClick(View v) {
        if(image==v) mGetContent.launch("image/*");
        else if(compDeleteBtn==v){
            image.setImageResource(R.drawable._642037847251);
            compDeleteBtn.setVisibility(View.GONE);
            compAddImageMsn.setVisibility(View.VISIBLE);
        }
    }
}