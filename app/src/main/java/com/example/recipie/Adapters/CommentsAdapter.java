package com.example.recipie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.R;
import com.example.recipie.Clases.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.DirectionsVH> {
    private final ArrayList<String> comments;
    private final Fragment fragment;
    private Context context;

    public CommentsAdapter(ArrayList<String> comments, Fragment fragment) {
        this.comments = comments;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CommentsAdapter.DirectionsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_comment, parent, false);
        return new DirectionsVH(view);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    static class DirectionsVH extends RecyclerView.ViewHolder {
        public TextView comment, user_name;
        public ImageView user_picture;

        public DirectionsVH(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            comment = itemView.findViewById(R.id.user_comment);
            user_picture = itemView.findViewById(R.id.user_picture);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.DirectionsVH holder, int position) {
        String comment = comments.get(position);

        System.out.println(comment);
        String[] arrOfStr = comment.split(" ", 2);

        String username = arrOfStr[0];

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(username);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            User u = documentSnapshot.toObject(User.class);

            holder.user_name.setText(u.name);
            Picasso.get().load(u.foto).transform(new RoundedCornersTransformation(50, 0)).into(holder.user_picture);
        });
        String c = arrOfStr[1];
        holder.comment.setText(c);


    }


}

