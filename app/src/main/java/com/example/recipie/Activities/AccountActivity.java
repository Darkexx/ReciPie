package com.example.recipie.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.recipie.Fragments.AccountFragment;
import com.example.recipie.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            openRecipesMenu();
        }
        else {
            setContentView(R.layout.activity_account);
            doMenu();
        }

    }

    public void doMenu() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.accountContainer, new AccountFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void openRecipesMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        doMenu();

    }
}