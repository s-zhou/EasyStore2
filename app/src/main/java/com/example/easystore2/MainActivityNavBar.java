package com.example.easystore2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easystore2.ProductList.Adapter.AdapterProducts;
import com.example.easystore2.Recipe.RecipeFragment;
import com.example.easystore2.ProductList.Fragments.MainFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityNavBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    MainFragment mainFragment= new MainFragment();
    private TextView emailTextView;
    private ArrayList<String> categoryList = new ArrayList<String>();
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    String orderBy="";
    String categorSelected;
    NavigationView navigationView;
    AdapterProducts adapter;
    MenuItem item;
    MenuItem filterItem ;


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    private Button searchBtn;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.nav_bar_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentFirebaseUser.getEmail();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = (TextView) headerView.findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        navigationView = findViewById(R.id.navigationView);
        // click event on navigatioView
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        //load main fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, mainFragment);
        fragmentTransaction.commit();

    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(item.getItemId() == R.id.home){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, mainFragment);
            toolbar.setTitle("Inventario");
            ActionMenuItemView i1 = toolbar.findViewById(R.id.search);
            ActionMenuItemView i2 = toolbar.findViewById(R.id.filterItem);
            i1.setVisibility(View.VISIBLE);
            i2.setVisibility(View.VISIBLE);
            fragmentTransaction.commit();
        }else if(item.getItemId() == R.id.list){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new RecipeFragment());
            toolbar.setTitle("Recetas");
            ActionMenuItemView i1 = toolbar.findViewById(R.id.search);
            ActionMenuItemView i2 = toolbar.findViewById(R.id.filterItem);
            i1.setVisibility(View.GONE);
            i2.setVisibility(View.GONE);
            fragmentTransaction.commit();

        }else if(item.getItemId() == R.id.close){
            //logout
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>(){
                @Override
                public void onComplete(@NonNull Task<Void> task){
                    Toast.makeText(MainActivityNavBar.this, "Sesion cerrada", Toast.LENGTH_SHORT).show();
// set the new task and clear flags
                    Intent intent = new Intent(getApplicationContext(), ContinueWithActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish();
                }
            });
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        item = menu.findItem(R.id.search);
        filterItem = menu.findItem(R.id.filterItem);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mostrarDialogoPersonalizado();
                return false;
            }
        });
        SearchView searchView =(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mainFragment.adapterProducts.getFilter().filter(query);
                mainFragment.search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainFragment.adapterProducts.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void mostrarDialogoPersonalizado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityNavBar.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.store_filter_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        RadioButton nameRB = view.findViewById(R.id.nameRadioButton);
        RadioButton expiredDataRB = view.findViewById(R.id.expiredDataRB);
        categorySelectorSpinner(categorySpinner);
        nameRB.setChecked(true);
        expiredDataRB.setChecked(false);

        if(orderBy.equals("data")){
            expiredDataRB.setChecked(true);
            nameRB.setChecked(false);
        }
        Button btnFilter = view.findViewById(R.id.siBtn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(nameRB.isChecked()){
                    orderBy ="name";
                }
                else if(expiredDataRB.isChecked()){
                    orderBy="data";
                }
                mainFragment.orderBy(orderBy);
                categorSelected = categorySpinner.getSelectedItem().toString();
                mainFragment.showCategory(categorSelected);
                dialog.dismiss();
            }
        });
        Button btnCancel = view.findViewById(R.id.noBtn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void categorySelectorSpinner(Spinner categorySpinner){
        setCategoriesSpinner(categorySpinner);
        ArrayAdapter adapterColor = new ArrayAdapter(
                this,
                R.layout.category_spinner_style,
                categoryList
        );
        adapterColor.setDropDownViewResource(R.layout.create_product_unit_spinner_style);
        categorySpinner.setAdapter(adapterColor);

    }

    private void setCategoriesSpinner(Spinner categorySpinner) {
        categoryList.clear();
        categoryList.add("Todo");
        categoryList.add("Sin categorizar");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://easystore-beb89-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        databaseReference.child("User").child(user.getUid()).child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot cat : snapshot.getChildren()) {
                        categoryList.add(cat.getValue().toString());
                    }
                    int pos = categoryList.indexOf(categorSelected);
                    if(pos != -1)categorySpinner.setSelection(pos);
                    else categorySpinner.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}