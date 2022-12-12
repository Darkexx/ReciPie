package com.example.recipie.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.Activities.RecipeViewerActivity;
import com.example.recipie.Adapters.CommentsAdapter;
import com.example.recipie.Adapters.DirectionsAdapter;
import com.example.recipie.Adapters.IngredientsAdapter;
import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.example.recipie.Clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class RecipeViewerFragment extends Fragment {
    private static final String TAG = "EmailPassword";

    IngredientsAdapter adapter;
    ArrayList<String> ingredients;
    RecyclerView recyclerView;

    DirectionsAdapter adapter2;
    ArrayList<String> directions;
    RecyclerView recyclerView2;

    CommentsAdapter adapter3;
    ArrayList<String> comments;
    RecyclerView recyclerView3;

    TextView recipe_name, recipe_desc, favorites_count;
    TextView user_name;
    ImageView recipe_picture;
    ImageView user_foto;
    ImageButton favorites_btn, addCommentBtn;
    EditText input;

    Recipe recipe;
    User owner;

    FirebaseUser current_user;
    FirebaseFirestore db;
    FirebaseUser user;

    String current_user_mail;

    NestedScrollView root;

    public RecipeViewerFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_viewer, container, false);
        return view;
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null) {
            current_user_mail = current_user.getEmail();
        }

        root = view.findViewById(R.id.recipe_viewer_fragment);
        recipe_name = view.findViewById(R.id.recipe_name);
        recipe_desc = view.findViewById(R.id.recipe_desc);
        recipe_picture = view.findViewById(R.id.recipe_picture);
        favorites_btn = view.findViewById(R.id.recipe_favorite_btn);
        user_name = view.findViewById(R.id.owner_name);
        user_foto = view.findViewById(R.id.owner_picture);

        recipe = ((RecipeViewerActivity) this.getActivity()).getRecipe();

        String name = recipe.name;
        recipe_name.setText(name);

        String desc = recipe.desc;
        recipe_desc.setText(desc);

        String pic = recipe.foto;
        Picasso.get().load(pic).into(recipe_picture);

        owner = new User();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference docRef = db.collection("users").document(recipe.owner);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            owner = documentSnapshot.toObject(User.class);

            String userp = requireContext().getResources().getString((R.string.posted_by));
            user_name.setText(userp + " " + owner.name);
            Picasso.get().load(owner.foto).transform(new RoundedCornersTransformation(50, 0)).into(user_foto);

        });

        //getOwnerData(view, owner);
        checkLiked(recipe.id);

        favorites_btn.setOnClickListener((View view1) -> {
            setLiked(recipe.id);
        });

        boolean edit = false;

        ingredients = recipe.ingredients;
        adapter = new IngredientsAdapter(ingredients, edit, this);

        recyclerView = view.findViewById(R.id.recipe_ingredients_container);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        directions = recipe.directions;
        adapter2 = new DirectionsAdapter(directions, edit, this);

        recyclerView2 = view.findViewById(R.id.recipe_directions_container);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView2.setAdapter(adapter2);

        adapter3 = new CommentsAdapter(recipe.comments, this);

        recyclerView3 = view.findViewById(R.id.recipe_coments_container);
        recyclerView3.setItemAnimator(new DefaultItemAnimator());
        recyclerView3.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true));
        recyclerView3.setAdapter(adapter3);
        adapter3.notifyDataSetChanged();

        //Boton para agregar un commentario al Arraylist y actualizar el recyclerview
        addCommentBtn = view.findViewById(R.id.addCommentBtn);
        input = view.findViewById(R.id.comment);

        addCommentBtn.setOnClickListener(view1 -> {
            String inputtext = String.valueOf(input.getText());

            if (TextUtils.isEmpty(inputtext)) {
                Snackbar.make(view1, R.string.empty, Snackbar.LENGTH_SHORT).show();
            } else {
                String email = null;
                if (user != null) {
                    email = user.getEmail();
                }
                String text = email + " " + inputtext;

                recipe.comments.add(text);
                input.setText("");
                //RecipeViewerFragment.this.hideKeyboard(view);

                db.collection("recipes").document(recipe.id)
                        .update("comments", FieldValue.arrayUnion(text))
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            adapter3.notifyDataSetChanged();
                            //Notificar al ususario
                            //sendNotificationToUser(inputtext, recipe.owner);

                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
            }
        });
    }

    public void checkLiked(String id){

        String email = null;
        if (user != null) {
            email = user.getEmail();
        }

        DocumentReference docRef = db.collection("users").document(email);
        String finalEmail = email;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> favs = (ArrayList<String>) document.get("favorites");
                        //System.out.println(favs);
                        String msg;

                        if (favs.contains(id)){
                            favorites_btn.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.button2));
                        }
                        else{
                            favorites_btn.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.containers));
                        }

                    } else {Log.d(TAG, "No such document"); }
                } else {Log.d(TAG, "get failed with ", task.getException()); }
            }
        });

    }


    public void setLiked(String id){

        String email = null;
        if (user != null) {
            email = user.getEmail();
        }

        DocumentReference docRef = db.collection("users").document(email);
        String finalEmail = email;
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> favs = (ArrayList<String>) document.get("favorites");
                        //System.out.println(favs);
                        String msg;

                        if (favs.contains(id)){
                            favs.remove(id);
                            msg = getActivity().getResources().getString(R.string.deleted_2_favs);
                            favorites_btn.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.containers));
                        }
                        else{
                            favs.add(id);
                            msg = getActivity().getResources().getString(R.string.added_2_favs);
                            favorites_btn.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.button2));
                        }
                        db.collection("users").document(finalEmail)
                                .update("favorites", favs)
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(root, msg, Snackbar.LENGTH_LONG).show();
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));


                    } else {Log.d(TAG, "No such document"); }
                } else {Log.d(TAG, "get failed with ", task.getException()); }
            }
        });

    }

}
