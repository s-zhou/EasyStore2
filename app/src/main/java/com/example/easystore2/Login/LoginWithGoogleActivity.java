package com.example.easystore2.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.easystore2.MainActivityNavBar;
import com.example.easystore2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class LoginWithGoogleActivity extends AppCompatActivity {
    FirebaseAuth mfirebaseAutH;
    FirebaseAuth.AuthStateListener mAuthListener;
    public static final int REQUEST_CODE = 76986;
    AuthUI.IdpConfig provider = new AuthUI.IdpConfig.GoogleBuilder().build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1_activity);
        mfirebaseAutH = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                System.out.println("entro a user !null");

                if (user != null) {
                    Toast.makeText(LoginWithGoogleActivity.this, "Se ha iniciado sesion", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginWithGoogleActivity.this, MainActivityNavBar.class));
                    finish();
                }
                else{
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Collections.singletonList(provider))
                            .setIsSmartLockEnabled(false)
                            .build(), REQUEST_CODE);
                }
            }
        };
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