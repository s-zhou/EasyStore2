package com.example.easystore2.Recipe;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.CalculateDate;
import com.example.easystore2.R;
import com.example.easystore2.Recipe.Adapter.AdapterRecipe;
import com.example.easystore2.data.model.ProductRV;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeFragment extends Fragment {
    Translator spanishEnglishTranslator;
    private RequestQueue mQueue;
    String translateWord;
    ConstraintLayout loadConstrait;
    RecyclerView recipeRecyclerView;
    AdapterRecipe adapterRecipe;

    public List<String> productNameList= new ArrayList<>();
    public ArrayList<String> nameListTranslate= new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();
    String allFavoriteName="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_sub_activity,container, false);
        loadConstrait = view.findViewById(R.id.loadConstrant);
        loadConstrait.setVisibility(View.VISIBLE);
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);

        mQueue = Volley.newRequestQueue(getContext());
        loadFavoriteRecipeName();
        return view;
    }
    private void loadFavoriteRecipeName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(uid).child("FavoriteRecipe").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("name").getValue().toString();
                        allFavoriteName+= " "+name;
                    }
                }
                prepareTranslateModel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void prepareTranslateModel() {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        spanishEnglishTranslator = Translation.getClient(options);
        spanishEnglishTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateLanguageList();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void translateLanguageList() {
        nameListTranslate.clear();
        for(String word: productNameList){
            translateLanguage(word);
        }
    }

    private void translateLanguage(String word) {
        spanishEnglishTranslator.translate(word).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                nameListTranslate.add(s);
                if(nameListTranslate.size()==productNameList.size())
                    translateReady();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                translateWord = "error translation";
            }
        });
    }

    private void translateReady() {
       ArrayList<String> p= nameListTranslate;
       int size = nameListTranslate.size();
       if(size>1) {
           int sizeNum=7;
           boolean end=false;
           if(size<6) sizeNum =size;
           for (int i = 0; i < 2; ++i) {
               for (int j = i+1; j < sizeNum; ++j) {
                   if(i == 1 && j == (sizeNum-1)) end=true;
                   readRecipeHTTP(nameListTranslate.get(i),nameListTranslate.get(j), end);
               }
           }
       }
       else if(nameListTranslate.size()==1) readRecipeHTTP(nameListTranslate.get(0), nameListTranslate.get(0), true);
       else{
           //si no ha encontrado ninguna receta
       }

    }
    private void showListItems(ArrayList<Recipe> list) {
        loadConstrait.setVisibility(View.GONE);

        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterRecipe = new AdapterRecipe(getContext(), list);
        recipeRecyclerView.setAdapter(adapterRecipe);

        adapterRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe r= list.get(recipeRecyclerView.getChildAdapterPosition(v));
                Intent intent = new Intent( getActivity(), RecipeDetailActivity.class);
                intent.putExtra("name",r.getName());
                intent.putExtra("image",r.getImage());
                intent.putExtra("instructions",r.getIngredients());
                intent.putExtra("url",r.getUrl());
                intent.putExtra("favorite",r.isFavorite());//mirar
                startActivity(intent);
            }
        });
    }

    private void readRecipeHTTP(String s, String q, boolean end){
        String app_id ="a7a5da31";
        String app_key ="dda7a804c66c252d00d168e99aff33da";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q="+q +" "+ s +"&from=0&to=5";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean lastOne=true;
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");
                            for(int i = 0; i< jsonArray.length();++i){
                                lastOne= false;
                                JSONObject r=jsonArray.getJSONObject(i).getJSONObject("recipe");
                                if(i == (jsonArray.length()-1) && end) lastOne= true;
                                addRecepe(r);
                            }
                        } catch (JSONException e) {
                            //si no ha encontrado ninguna receta
                        }
                        if(end && lastOne)loadRV();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //si no ha encontrado ninguna receta
            }
        });
        mQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadRV() {
        recipes.sort((d1, d2) -> (new Integer(d2.getNumIngredientStore())).compareTo(new Integer(d1.getNumIngredientStore())));
        showListItems(recipes);
    }



    private void addRecepe(JSONObject recipe) throws JSONException {
        String ing=recipe.getJSONArray("ingredientLines").toString();
        recipes.add(generateRecipe(recipe, scoreRecipe(ing)));
    }


    private Recipe generateRecipe(JSONObject recipe, Integer n) throws JSONException {
        String name = recipe.getString("label");
        String image = recipe.getString("image");
        String url = recipe.getString("url");
        ArrayList<String> ingredients = new ArrayList<>();
        JSONArray listIngJSON = recipe.getJSONArray("ingredientLines");
        for(int i =0; i<listIngJSON.length(); ++i){
            ingredients.add(listIngJSON.get(i).toString());
        }
        Boolean favorite= false;
        if(allFavoriteName.toUpperCase(Locale.ROOT).contains(name.toUpperCase(Locale.ROOT)))
            favorite=true;
        return new Recipe(name, image, url, favorite,n, ingredients);
    }



    private int scoreRecipe(String ingredientList) {
        int size=nameListTranslate.size();
        int numIngredients=0;
        for(int i=0; i < size; ++i){
            if(ingredientList.toUpperCase(Locale.ROOT).contains(nameListTranslate.get(i).toUpperCase(Locale.ROOT))){
                ++numIngredients;
            }
        }
        return numIngredients;
    }



}
