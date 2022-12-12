package com.example.recipie.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.example.recipie.Activities.RecipeViewerActivity;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class OnlineRecipesAdapter extends RecyclerView.Adapter<OnlineRecipesAdapter.RecipesVH> {
    private final Vector<Recipe> recipes;

    public OnlineRecipesAdapter(Vector<Recipe> clases) {
        this.recipes = clases;
    }

    @NonNull
    @Override
    public RecipesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recipe_preview_extended, parent, false);
        return new RecipesVH(view);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


    static class RecipesVH extends RecyclerView.ViewHolder {
        private final ImageView recipe_picture;
        public TextView recipe_name, recipe_desc;
        public CardView recipe_card;

        public RecipesVH(@NonNull View view) {
            super(view);

            recipe_name = itemView.findViewById(R.id.recipe_name);
            recipe_picture = itemView.findViewById(R.id.recipe_picture);
            recipe_card = itemView.findViewById(R.id.recipe_card);

        }

        public void setPicture(Uri url) {
            Picasso.get()
                    .load(url)
                    .into(recipe_picture);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecipesVH holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.recipe_name.setText(String.valueOf(recipe.name));
        holder.setPicture(Uri.parse(recipe.foto));

        holder.recipe_card.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), RecipeViewerActivity.class);
            intent.putExtra("KEY_NAME", recipe);
            view.getContext().startActivity(intent);

        });


    }
}
