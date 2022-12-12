package com.example.recipie.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipie.Fragments.RecipeEditorFragment;
import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;

public class RecipeEditorActivity extends AppCompatActivity {

    Recipe recipe;

    public Recipe getRecipe() {
        return this.recipe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_editor);

        recipe = (Recipe) getIntent().getSerializableExtra("KEY_NAME");

        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_editor_container,
                new RecipeEditorFragment()).commit();

    }

}