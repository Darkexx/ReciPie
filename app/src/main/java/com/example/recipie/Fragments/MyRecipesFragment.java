package com.example.recipie.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.Activities.MainActivity;
import com.example.recipie.Activities.RecipeEditorActivity;
import com.example.recipie.Adapters.MyRecipesAdapter;
import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Vector;

public class MyRecipesFragment extends Fragment {

    private static final String TAG = "EmailPassword";

    private MyRecipesAdapter adapter;
    Vector<Recipe> vector;
    RecyclerView recyclerView;

    TextView no_recipes_text;

    boolean shouldRefreshOnResume = false;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            ((MainActivity) getActivity()).startMyRecipies();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    public MyRecipesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vector = new Vector<>();
        adapter = new MyRecipesAdapter(vector);

        recyclerView = view.findViewById(R.id.my_recipes_container);
        //recyclerView.addItemDecoration (new DividerItemDecoration (this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        getRecipesData(view);

        no_recipes_text = view.findViewById(R.id.no_recipes_text);

        FloatingActionButton fab = view.findViewById(R.id.new_recipe_btn);
        fab.setOnClickListener(view1 -> {
            //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show();

            Intent intent = new Intent(getActivity(), RecipeEditorActivity.class);
            startActivity(intent);
        });

    }


    @SuppressLint("NotifyDataSetChanged")
    public void getRecipesData(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = null;
        if (user != null) {
            email = user.getEmail();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> docRef = db.collection("recipes")
                .whereEqualTo("owner", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Recipe recipee = document.toObject(Recipe.class);
                            vector.add(recipee);
                            adapter.notifyDataSetChanged();

                            if (vector.isEmpty()) {
                                no_recipes_text.setVisibility(view.VISIBLE);
                            } else {
                                no_recipes_text.setVisibility(view.GONE);
                            }

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}


