package com.example.easystore2.Recipe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeFragment extends Fragment {
    TextView t;
    private RequestQueue mQueue;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sub_activity,container, false);
        t = view.findViewById(R.id.textView4);
        mQueue = Volley.newRequestQueue(getContext());
        readRecipeHTTP();
        return view;
    }

    private void readRecipeHTTP(){
        String q ="chicken";
        String app_id ="15bb7a50";
        String app_key ="5a48c559463777d72f3873c346768262";
        String url = "https://api.edamam.com/search?app_id=" + app_id + "&app_key=" + app_key + "&q=" + q;//String url = "https://api.edamam.com/search?q=chicken&app_id=${15bb7a50}&app_key=${03700791f5b4cf31d2ffbd4cd98de498}&from=0&to=3&calories=591-722&health=alcohol-free";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null,
            new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("hits");
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
        mQueue.add(request);
    }
}
