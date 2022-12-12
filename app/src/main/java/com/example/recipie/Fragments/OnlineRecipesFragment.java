package com.example.recipie.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.Activities.MainActivity;
import com.example.recipie.Adapters.OnlineRecipesAdapter;
import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Vector;

public class OnlineRecipesFragment extends Fragment {

    private static final String TAG = "EmailPassword";

    private OnlineRecipesAdapter adapter;
    Vector<Recipe> vector;
    RecyclerView recyclerView;

    LinearLayout root;

    boolean shouldRefreshOnResume = false;

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            ((MainActivity) getActivity()).startOnlineRecipes();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    public OnlineRecipesFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_recipes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vector = new Vector<>();
        adapter = new OnlineRecipesAdapter(vector);

        root = view.findViewById(R.id.online_recipes_root);

        recyclerView = view.findViewById(R.id.online_recipes_container);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        getRecipesData();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getRecipesData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> docRef = db.collection("recipes")
                .whereEqualTo("privacy", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipee = document.toObject(Recipe.class);

                            vector.add(recipee);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }
}


