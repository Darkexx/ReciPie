package com.example.recipie.Adapters;

import android.content.Context;
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

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.DirectionsVH> {
    private final ArrayList<String> directions;
    private final boolean update;
    private final Fragment fragment;
    private Context context;

    public DirectionsAdapter(ArrayList<String> directions, boolean update, Fragment fragment) {
        this.directions = directions;
        this.update = update;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DirectionsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_direction, parent, false);
        return new DirectionsVH(view);
    }

    @Override
    public int getItemCount() {
        return directions.size();
    }


    class DirectionsVH extends RecyclerView.ViewHolder {
        public TextView direction, num_direction;
        public ImageButton removeButton;

        public DirectionsVH(@NonNull View itemView) {
            super(itemView);
            num_direction = itemView.findViewById(R.id.num_direction);
            direction = itemView.findViewById(R.id.direction);

            removeButton = (ImageButton) itemView.findViewById(R.id.removeDirectionBtn);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DirectionsVH holder, int position) {
        String direction = directions.get(position);

        //Ocupo un contexto para poder obtener un valor de String.xml
        context = holder.num_direction.getContext();
        String pos = context.getResources().getString(R.string.num_direction);

        //Concatenamos el texto con el numero de pasos correspondiente
        holder.num_direction.setText(String.format("%s %s", pos, position + 1));
        holder.direction.setText(direction);

        if (!update) {
            holder.removeButton.setVisibility(View.GONE);
        }

        holder.removeButton.setOnClickListener(view -> {
            System.out.println("Borrar " + direction);
            if (update) {
                ((RecipeEditorFragment) fragment).removeDirection(direction);
            }
        });

    }
}
