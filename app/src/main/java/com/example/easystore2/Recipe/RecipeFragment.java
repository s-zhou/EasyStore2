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
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {
    TextView t;
    Translator spanishEnglishTranslator,englishGermanTranslator;
    private RequestQueue mQueue;
    public List<String> productNameList= new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sub_activity,container, false);
        t = view.findViewById(R.id.textView4);
        mQueue = Volley.newRequestQueue(getContext());
        //readRecipeHTTP();
        prepareTranslateModel();
        productNameList.clear();
        return view;
    }

    private void readRecipeHTTP(){
        String q ="nlkj";
        final Boolean[] find = {false};
        String app_id ="15bb7a50";
        String app_key ="5a48c559463777d72f3873c346768262";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q=" + q;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("hits");
                            find[0] = true;
                            for(int i = 0; i< jsonArray.length();++i){
                                JSONObject recipe = jsonArray.getJSONObject(i).getJSONObject("recipe");
                                String name = recipe.getString("label");
                                t.setText(name);
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
        if(find[0])
            mQueue.add(request);
        else t.setText("Sin receta");
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
                translateLanguage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void translateLanguage() {
        spanishEnglishTranslator.translate("hola, como estas").addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
