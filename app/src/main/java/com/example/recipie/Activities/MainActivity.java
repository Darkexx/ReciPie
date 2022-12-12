package com.example.recipie.Activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.recipie.Fragments.FavoriteRecipesFragment;
import com.example.recipie.Fragments.MyRecipesFragment;
import com.example.recipie.Fragments.OnlineRecipesFragment;
import com.example.recipie.R;
import com.example.recipie.Clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public FirebaseAuth auth;

    private DrawerLayout drawer;
    private User u;

    public ImageView foto;
    public TextView name, mail;

    String correo;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth = FirebaseAuth.getInstance();

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState==null){
            getSupportActionBar().setTitle(R.string.my_recipes);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MyRecipesFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);

            //getUserData();
        }

    }

    public void getUserData () {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        System.out.println(user);
        if (user != null) {
            correo = user.getEmail();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(correo);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            u = documentSnapshot.toObject(User.class);

            name = findViewById (R.id.name_navbar);
            mail = findViewById (R.id.email_navbar);
            foto = findViewById (R.id.foto_navbar);

            name.setText(u.name);
            mail.setText(correo);
            Picasso.get().load (u.foto).transform(new RoundedCornersTransformation(60, 10)).into (foto);
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportActionBar().setTitle(R.string.my_recipes);
                startMyRecipies();
                break;
            case R.id.nav_gallery:
                getSupportActionBar().setTitle(R.string.online_recipes);
                startOnlineRecipes();
                break;
            case R.id.nav_favs:
                getSupportActionBar().setTitle(R.string.favorite_recipes);
                startFavoriteRecipes();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, AccountActivity.class);
                                startActivity(intent);
                                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startFavoriteRecipes() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FavoriteRecipesFragment()).commit();
    }

    public void startMyRecipies(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MyRecipesFragment()).commit();
    }

    public void startOnlineRecipes(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new OnlineRecipesFragment()).commit();
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }
}

