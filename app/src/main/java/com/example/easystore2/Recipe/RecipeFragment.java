package com.example.easystore2.Recipe;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecipeFragment extends Fragment {
    Translator spanishEnglishTranslator;
    private RequestQueue mQueue;
    String translateWord;
    ConstraintLayout loadConstrait;
    RecyclerView recipeRecyclerView;
    AdapterRecipe adapterRecipe;
    View view;
    public List<String> productNameList= new ArrayList<>();
    public ArrayList<String> nameListTranslate= new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();
    private Button createRecipeBtn;
    private TextView noneRecipe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recipe_sub_activity,container, false);
        loadConstrait = view.findViewById(R.id.loadConstrant);
        loadConstrait.setVisibility(View.GONE);
        noneRecipe = view.findViewById(R.id.recipeNoneTV);
        noneRecipe.setVisibility(View.GONE);
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        createRecipeBtn = view.findViewById(R.id.creatRecipeBtn);
        createRecipeBtn.setVisibility(View.GONE);
        mQueue = Volley.newRequestQueue(getContext());
        loadDBRecipe();


        return view;
    }

    private void loadDBRecipe() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference ref = databaseReference.child("User").child(user.getUid()).child("DayRecipe");
        ref.child(new SimpleDateFormat("dd-MM-yyyy").format(new Date())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot prod : snapshot.getChildren()) {
                        String name = prod.child("name").getValue().toString();
                        String image = prod.child("image").getValue().toString();
                        String instruction = prod.child("instruction").getValue().toString();
                        Iterable<DataSnapshot> ingredientsDS = prod.child("ingredients").getChildren();
                        ArrayList<String> ingredients = new ArrayList<>();
                        for (DataSnapshot i : ingredientsDS) ingredients.add(i.getValue().toString());
                        boolean fav = prod.child("favorite").getValue().toString().equals("true");
                        boolean mine = prod.child("mine").getValue().toString().equals("true");
                        Recipe r = new Recipe(name, image, "" ,instruction, mine,fav, 0, ingredients);
                        recipes.add(r);
                    }
                    showListItems(recipes);
                    noneRecipe.setVisibility(View.GONE);

                }
                else {
                    noneRecipe.setVisibility(View.VISIBLE);

                    ref.removeValue();
                    prepareTranslateModel("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void prepareTranslateModel(String filter) {
        loadConstrait.setVisibility(View.VISIBLE);
        TextView noneRecipe = view.findViewById(R.id.recipeNoneTV);
        noneRecipe.setVisibility(View.GONE);
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.SPANISH)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        spanishEnglishTranslator = Translation.getClient(options);
        spanishEnglishTranslator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translateLanguageList(filter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void translateLanguageList(String filter) {
        nameListTranslate.clear();
        for(String word: productNameList){
            translateLanguage(word,filter);
        }
    }

    private void translateLanguage(String word,String filter) {
        spanishEnglishTranslator.translate(word).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                nameListTranslate.add(s);
                if(nameListTranslate.size()==productNameList.size())
                    translateReady(filter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                translateWord = "error translation";
            }
        });
    }

    private void translateReady(String filter) {
       ArrayList<String> p= nameListTranslate;
       recipes.clear();
       int size = nameListTranslate.size();
       if(size>1) {
           int sizeNum=4;
           boolean end=false;
           if(size<=3) sizeNum =size;
           for (int i = 0; i < 2; ++i) {
               for (int j = i+1; j < sizeNum; ++j) {
                   if(i == 1 && j == (sizeNum-1)) end=true;
                   readRecipeHTTP(nameListTranslate.get(i),nameListTranslate.get(j), end,filter);
               }
           }
       }
       else if(nameListTranslate.size()==1) readRecipeHTTP(nameListTranslate.get(0), nameListTranslate.get(0), true,filter);
       else{
           TextView noneRecipe = view.findViewById(R.id.recipeNoneTV);
           noneRecipe.setVisibility(View.VISIBLE);
           loadConstrait.setVisibility(View.GONE);

           //si no ha encontrado ninguna receta, mostrar un mensaje x pantalla
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
                if(!list.isEmpty()) {
                    Recipe r = list.get(recipeRecyclerView.getChildAdapterPosition(v));
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
                    databaseReference.child("User").child(uid).child("FavoriteRecipe").child(r.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            startNewActivity(r, snapshot.exists());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    private void startNewActivity(Recipe r, boolean exists){
        Intent intent = new Intent( getActivity(), RecipeDetailActivity.class);
        intent.putExtra("name",r.getName());
        intent.putExtra("image",r.getImage());
        intent.putExtra("ingredients",r.getIngredients());
        intent.putExtra("instruction",r.getInstruction());
        intent.putExtra("description", r.getDescription());
        boolean mine= r.isMine();
        intent.putExtra("mine",mine);
        intent.putExtra("like",exists);//mirar
        startActivity(intent);
    }

    private void readRecipeHTTP(String s, String q, boolean end, String filter){
        String app_id ="03a82397";
        String app_key ="f68990b12dd09471ab9754d0b4f40bbb";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q="+q +" "+ s +"&from=0&to=3"+filter;
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
                TextView noneRecipe = view.findViewById(R.id.recipeNoneTV);
                noneRecipe.setVisibility(View.VISIBLE);
                loadConstrait.setVisibility(View.GONE);
            }
        });
        mQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadRV() {
        recipes.sort((d1, d2) -> (new Integer(d2.getNumIngredientStore())).compareTo(new Integer(d1.getNumIngredientStore())));
        saveDB(recipes);
        showListItems(recipes);
    }

    private void saveDB(ArrayList<Recipe> recipes) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        DatabaseReference ref = databaseReference.child("User").child(user.getUid()).child("DayRecipe").child(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        for(Recipe r: this.recipes){

            ref.child(r.getName()).setValue(r);
        }
    }


    private void addRecepe(JSONObject recipe) throws JSONException {
        String ing=recipe.getJSONArray("ingredientLines").toString();
        recipes.add(generateRecipe(recipe, scoreRecipe(ing)));
        noneRecipe.setVisibility(View.GONE);
    }


    private Recipe generateRecipe(JSONObject recipe, Integer n) throws JSONException {
        String name = recipe.getString("label");
        String image = recipe.getString("image");
        String instruction = recipe.getString("url");
        ArrayList<String> ingredients = new ArrayList<>();
        JSONArray listIngJSON = recipe.getJSONArray("ingredientLines");
        for(int i =0; i<listIngJSON.length(); ++i){
            ingredients.add(listIngJSON.get(i).toString());
        }
        return new Recipe(name, image, "",instruction, false, false,n, ingredients);
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


    public void filter(String diet, String health, String cuisineType, String mealType, String min, String max) {
        String filter ="";
        recipes.clear();
        //cadapterRecipe.notifyDataSetChanged();

        if(!diet.equals("None")) filter += "&diet=" + diet;
        if(!health.equals("None")) filter += "&health=" + health;
        if(!cuisineType.equals("None")) filter += "&cuisineType=" + cuisineType;
        if(!mealType.equals("None")) filter += "&mealType=" + mealType;
        if(!max.equals("99") || !min.equals("01")) filter += "&time=" + min+"-"+max;
        prepareTranslateModel(filter);
    }
}
