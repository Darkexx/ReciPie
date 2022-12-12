package com.example.recipie.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.Fragments.RecipeEditorFragment;
import com.example.recipie.R;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.RecipesVH> {
    private final ArrayList<String> ingredients;
    private final boolean update;
    private Fragment fragment;

    public IngredientsAdapter(ArrayList<String> ingredients, boolean update, Fragment fragment) {
        this.ingredients = ingredients;
        this.update = update;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecipesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ingredient, parent, false);
        return new RecipesVH(view);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }


    static class RecipesVH extends RecyclerView.ViewHolder {
        public TextView ingredient;
        public ImageButton removeButton;

        public RecipesVH(@NonNull View itemView) {
            super(itemView);
            ingredient = itemView.findViewById(R.id.ingredient);
            removeButton = itemView.findViewById(R.id.removeIngredientBtn);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesVH holder, int position) {
        String ingredient = ingredients.get(position);
        holder.ingredient.setText(ingredient);

        if (!update) {
            holder.removeButton.setVisibility(View.GONE);
        }

        holder.removeButton.setOnClickListener(view -> {
            if (update) {
                ((RecipeEditorFragment) fragment).removeIngredient(ingredient);
            }
        });
    }
}
