package com.example.easystore2.Recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.R;
import com.example.easystore2.data.model.ProductRV;
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
import java.util.Collection;
import java.util.List;

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
       readRecipeHTTP(nameListTranslate.get(0),0,10);
    }

    private void readRecipeHTTP(String q, int from, int to){
        final Boolean[] find = {false};
        String app_id ="a7a5da31";
        String app_key ="dda7a804c66c252d00d168e99aff33da";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q="+q +"&from=" + String.valueOf(from) +"&to="+ String.valueOf(to);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");
                            find[0] = true;
                            int size=10;
                            if(jsonArray.length()<10)size=jsonArray.length();
                            for(int i = 0; i< size;++i){
                                JSONObject r=jsonArray.getJSONObject(i).getJSONObject("recipe");
                                addRecepe(r);
                            }
                            if(to<200 && jsonArray.length()>0) {
                                if (recipes.size() < 1) {
                                    readRecipeHTTP(q, from + 10, to + 10);
                                } else {
                                    Toast.makeText(getContext(), "se ha encotrado las recetas", Toast.LENGTH_LONG).show();

                                }
                            }
                            else Toast.makeText(getContext(), "Sin recetas", Toast.LENGTH_LONG).show();


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
        if(find[0]) t.setText("Sin receta");
    }

    private void addRecepe(JSONObject recipe) throws JSONException {
        String ing=recipe.getJSONArray("ingredientLines").toString();
        ArrayList<Integer> numPoint= scoreRecipe(ing);
        int numP=numPoint.get(0);
        if(numPoint.get(0)>numIngredints){
            recipes.add(generateRecipe(recipe, numPoint.get(1)));
        }

    }

    private Recipe generateRecipe(JSONObject recipe, Integer p) throws JSONException {
        String name = recipe.getString("label");
        String image = recipe.getString("image");
        String url = recipe.getString("url");
        int point = p;
        ArrayList<String> ingredients = new ArrayList<>();
        JSONArray listIngJSON = recipe.getJSONArray("ingredientLines");
        for(int i =0; i<listIngJSON.length(); ++i){
            ingredients.add(listIngJSON.get(i).toString());
        }
        return new Recipe(name, image, url, point, ingredients);
    }



    private ArrayList<Integer> scoreRecipe(String ingredientList) {
        int size=nameListTranslate.size();
        ArrayList<Integer> numPoint= new ArrayList<Integer>();
        numPoint.add(0);//num ingredientes
        numPoint.add(0);//puntuacion receta
        for(int i=0; i < size; ++i){
            if(ingredientList.contains(nameListTranslate.get(i))){
                numPoint.set(1,numPoint.get(1)+size-i);
                numPoint.set(0,numPoint.get(0)+1);
            }
        }
        return numPoint;
    }


}
