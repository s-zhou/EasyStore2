package com.example.easystore2.Recipe;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RecipeFragment extends Fragment {
    TextView t;
    Translator spanishEnglishTranslator;
    private RequestQueue mQueue;
    String translateWord;

    public List<String> productNameList= new ArrayList<>();
    public ArrayList<String> nameListTranslate= new ArrayList<>();
    ArrayList<Recipe> recipes = new ArrayList<>();
    int numIngredints=1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sub_activity,container, false);
        t = view.findViewById(R.id.textView4);
        mQueue = Volley.newRequestQueue(getContext());
        prepareTranslateModel();
        return view;
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
                   readRecipeHTTP(nameListTranslate.get(i),nameListTranslate.get(j),0,5, end);
               }
           }
       }
       else if(nameListTranslate.size()==1) readRecipeHTTP(nameListTranslate.get(0), nameListTranslate.get(0),0,5, true);
       else{
          // t.setText("Sin receta");
       }

    }

    private void readRecipeHTTP(String s, String q, int from, int to, boolean end){
        String app_id ="a7a5da31";
        String app_key ="dda7a804c66c252d00d168e99aff33da";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q="+q +" "+ s +"&from=" + String.valueOf(from) +"&to="+ String.valueOf(to);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");
                            for(int i = 0; i< jsonArray.length();++i){
                                JSONObject r=jsonArray.getJSONObject(i).getJSONObject("recipe");
                                if(i == (jsonArray.length()-1) && end) {addRecepe(r,true);}
                                else addRecepe(r,false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addRecepe(JSONObject recipe, boolean b) throws JSONException {
        String ing=recipe.getJSONArray("ingredientLines").toString();
        recipes.add(generateRecipe(recipe, scoreRecipe(ing)));
        if(b){
            recipes.sort((d1, d2) -> (new Integer(d2.getNumIngredientStore())).compareTo(new Integer(d1.getNumIngredientStore())));
        }

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
        return new Recipe(name, image, url, n, ingredients);
    }



    private int scoreRecipe(String ingredientList) {
        int size=nameListTranslate.size();
        int numIngredients=0;
        for(int i=0; i < size; ++i){
            if(ingredientList.contains(nameListTranslate.get(i))){
                ++numIngredients;
            }
        }
        return numIngredients;
    }



}
