package com.example.recipie.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.example.recipie.Fragments.RecipeViewerFragment;

public class RecipeViewerActivity extends AppCompatActivity {

    Recipe recipe;

    public Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        recipe = (Recipe) getIntent().getSerializableExtra("KEY_NAME");

        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_viewer_container,
                new RecipeViewerFragment()).commit();

    }


}
