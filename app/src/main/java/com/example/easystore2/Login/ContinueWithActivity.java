package com.example.easystore2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easystore2.MainActivityNavBar;
import com.example.easystore2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContinueWithActivity extends AppCompatActivity {
    FirebaseAuth mfirebaseAutH;
    FirebaseAuth.AuthStateListener mAuthListener;
    public static final int REQUEST_CODE = 76986;
    AuthUI.IdpConfig provider = new AuthUI.IdpConfig.GoogleBuilder().build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mfirebaseAutH = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(ContinueWithActivity.this, "Se ha iniciado sesion", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ContinueWithActivity.this, MainActivityNavBar.class));
                    finish();

                }
            }
        };
    }

    public void loginBotton(View view){
        startActivity(new Intent(ContinueWithActivity.this, LoginWithGoogleActivity.class));
        finish();
    }
    @Override
    protected void onResume(){
        super.onResume();
        mfirebaseAutH.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mfirebaseAutH.removeAuthStateListener(mAuthListener);
    }
}