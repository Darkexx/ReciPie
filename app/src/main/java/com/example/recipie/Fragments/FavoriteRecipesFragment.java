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
import com.example.recipie.Adapters.OnlineRecipesAdapter;
import com.example.recipie.Clases.Recipe;
import com.example.recipie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Vector;

public class FavoriteRecipesFragment extends Fragment {
    private static final String TAG = "EmailPassword";

    private OnlineRecipesAdapter adapter;
    Vector<Recipe> vector;
    RecyclerView recyclerView;

    TextView no_recipes_text;

    FirebaseFirestore db;
    FirebaseUser user;

    boolean shouldRefreshOnResume = false;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            ((MainActivity) getActivity()).startFavoriteRecipes();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    public FavoriteRecipesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_recipes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vector = new Vector<>();
        adapter = new OnlineRecipesAdapter(vector);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        no_recipes_text = view.findViewById(R.id.no_recipes_text);

        recyclerView = view.findViewById(R.id.favorite_recipes_container);
        //recyclerView.addItemDecoration (new DividerItemDecoration (this, DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        getRecipesData(view);
    }


    @SuppressLint("NotifyDataSetChanged")
    public void getRecipesData(View view) {
        String email = null;
        if (user != null) {
            email = user.getEmail();
        }

        DocumentReference docRef = db.collection("users").document(email);
        String finalEmail1 = email;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> favs = (ArrayList<String>) document.get("favorites");

                        for(String fav: favs){
                            Task<QuerySnapshot> docRef = db.collection("recipes")
                                    .whereEqualTo("id", fav)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                Recipe recipee = document2.toObject(Recipe.class);
                                                vector.add(recipee);
                                                adapter.notifyDataSetChanged();

                                                if (vector.isEmpty()) {
                                                    no_recipes_text.setVisibility(view.VISIBLE);
                                                } else {
                                                    no_recipes_text.setVisibility(view.GONE);
                                                }

                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task2.getException());
                                        }
                                    });
                        }
                    }
                }

            }
        });
    }


}

