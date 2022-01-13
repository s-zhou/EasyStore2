package com.example.easystore2.Recipe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.easystore2.CreateRecipeActivity;
import com.example.easystore2.R;
import com.example.easystore2.Recipe.RecipeDetailActivity;

import java.util.ArrayList;

public class MyRecipeFragment extends Fragment implements View.OnClickListener {
    Button createRecipeBtn;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_recipe_sub_activity,container, false);
        createRecipeBtn = view.findViewById(R.id.creatRecipeBtn);
        createRecipeBtn.setOnClickListener(this);
        return view;
    }

    private void createRecipe() {
        Intent intent = new Intent( getContext(), CreateRecipeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v==createRecipeBtn){
            createRecipe();
        }
    }
}