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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.easystore2.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RecipeFragment extends Fragment {
    TextView t;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_sub_activity,container, false);
        t = view.findViewById(R.id.textView4);
        readRecipeHTTP();
        return view;
    }

    private void readRecipeHTTP(){
        String q ="chicken";
        String YOUR_APP_ID ="15bb7a50";
        String YOUR_APP_KEY ="03700791f5b4cf31d2ffbd4cd98de498";

        String url = "https://api.edamam.com/search?app_id=15bb7a50&app_key=5a48c559463777d72f3873c346768262&q=eggs";//String url = "https://api.edamam.com/search?q=chicken&app_id=${15bb7a50}&app_key=${03700791f5b4cf31d2ffbd4cd98de498}&from=0&to=3&calories=591-722&health=alcohol-free";
        //String url="https://api.edamam.com/search?q="+q+"&app_id="+ YOUR_APP_ID +"&app_key="+YOUR_APP_KEY;
        //String url = "https://jsonplaceholder.typicode.com/posts/11";

        StringRequest postResquest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("mensaje", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Volley.newRequestQueue(getContext()).add(postResquest);
    }
}
