package com.example.recipie.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipie.Activities.RecipeEditorActivity;
import com.example.recipie.Adapters.DirectionsAdapter;
import com.example.recipie.Adapters.IngredientsAdapter;
import com.example.recipie.R;
import com.example.recipie.Clases.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecipeEditorFragment extends Fragment {
    private static final int SELECT_IMAGE_REQUEST_CODE = 2001;

    private FirebaseStorage storage;
    private Snackbar snackbar;
    private NestedScrollView root;

    ImageButton addIngredientBtn, addDirectionBtn;
    Button saveRecipeBtn, deleteRecipeBtn;
    FloatingActionButton setRecipePic;

    private EditText input, input2, recipe_name, recipe_desc;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch recipe_privacy;
    private ImageView recipe_picture;

    IngredientsAdapter adapter;
    ArrayList<String> ingredients;
    RecyclerView recyclerView;

    DirectionsAdapter adapter2;
    ArrayList<String> directions;
    RecyclerView recyclerView2;

    ArrayList<String> comments;

    Recipe recipe;
    String pic;
    boolean update;
    String id;


    public RecipeEditorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_editor, container, false);
        recipe = ((RecipeEditorActivity) this.getActivity()).getRecipe();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view.findViewById(R.id.recipe_editor_fragment);

        storage = FirebaseStorage.getInstance();

        //Recyvlerview para la lista de ingredientes
        ingredients = new ArrayList<>();
        if (recipe != null) {
            ingredients = ((RecipeEditorActivity) this.getActivity()).getRecipe().ingredients;
        }
        boolean edit = true;
        adapter = new IngredientsAdapter(ingredients, edit, RecipeEditorFragment.this);

        recipe_name = view.findViewById(R.id.recipe_name);
        recipe_desc = view.findViewById(R.id.recipe_desc);
        recipe_privacy = view.findViewById(R.id.recipe_privacy_switch);

        setRecipePic = view.findViewById(R.id.setRecipePicBtn);
        recipe_picture = view.findViewById(R.id.recipe_picture);
        setRecipePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeEditorFragment.this.selectImage();
            }
        });

        recyclerView = view.findViewById(R.id.ingredients_container);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        //Boton para agregar un ingrediente al Arraylist y actualizar el recyclerview
        addIngredientBtn = view.findViewById(R.id.addIngredientBtn);
        input = view.findViewById(R.id.ingredient);

        addIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputtext = String.valueOf(input.getText());
                if (TextUtils.isEmpty(inputtext)) {
                    Snackbar.make(view, "Empty", Snackbar.LENGTH_SHORT).show();
                } else {
                    ingredients.add(inputtext);
                    input.setText("");
                    RecipeEditorFragment.this.hideKeyboard(view);
                    adapter.notifyDataSetChanged();
                }

            }
        });

        //Recyclerview para la lista de los pasos (procedimiento)
        directions = new ArrayList<>();
        if (recipe != null) {
            directions = ((RecipeEditorActivity) this.getActivity()).getRecipe().directions;
        }
        edit = true;
        adapter2 = new DirectionsAdapter(directions, edit, RecipeEditorFragment.this);

        recyclerView2 = view.findViewById(R.id.directions_container);
        //recyclerView.addItemDecoration (new DividerItemDecoration (this, DividerItemDecoration.VERTICAL));
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView2.setAdapter(adapter2);

        //Boton para agregar un procedimiento al Arraylist y actualizar el recyclerview
        addDirectionBtn = view.findViewById(R.id.addDirectionBtn);
        input2 = view.findViewById(R.id.direction);

        addDirectionBtn.setOnClickListener(view13 -> {
            String inputtext = String.valueOf(input2.getText());
            if (TextUtils.isEmpty(inputtext)) {
                Snackbar.make(view13, "Empty", Snackbar.LENGTH_SHORT).show();
            } else {
                directions.add(inputtext);
                input2.setText("");
                RecipeEditorFragment.this.hideKeyboard(view13);
                adapter2.notifyDataSetChanged();
            }

        });

        comments = new ArrayList<>();

        if (recipe != null) {
            //Si entramos en modo edicion, entonces se cargan los datos de la receta que recibimos en forma de objeto.
            String name = ((RecipeEditorActivity) this.getActivity()).getRecipe().name;
            recipe_name.setText(name);

            String desc = ((RecipeEditorActivity) this.getActivity()).getRecipe().desc;
            recipe_desc.setText(desc);

            boolean privacy = ((RecipeEditorActivity) this.getActivity()).getRecipe().privacy;
            recipe_privacy.setChecked(privacy);

            String pic = ((RecipeEditorActivity) this.getActivity()).getRecipe().foto;
            Picasso.get().load(pic).into(recipe_picture);
            comments = ((RecipeEditorActivity) this.getActivity()).getRecipe().comments;

        }


        saveRecipeBtn = view.findViewById(R.id.save_recipe_btn);
        saveRecipeBtn.setOnClickListener(view12 -> {
            snackbar = Snackbar.make(view12, R.string.saving, Snackbar.LENGTH_INDEFINITE);
            ViewGroup layer = (ViewGroup) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
            ProgressBar bar = new ProgressBar(RecipeEditorFragment.this.getContext());
            layer.addView(bar);
            snackbar.show();

            boolean update = false;
            String id = "", f = "";

            if (recipe != null) {
                update = true;
                id = recipe.id;
                f = recipe.foto;
            }
            saveInfo(update, id, f);
        });


        deleteRecipeBtn = view.findViewById(R.id.delete_recipe_btn);
        if (recipe != null) {
            deleteRecipeBtn.setOnClickListener(view1 -> {
                snackbar = Snackbar.make(view1, R.string.deleting, Snackbar.LENGTH_INDEFINITE);
                ViewGroup layer = (ViewGroup) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
                ProgressBar bar = new ProgressBar(RecipeEditorFragment.this.getContext());
                layer.addView(bar);
                snackbar.show();

                deleteRecipe(recipe.id, recipe.foto);
            });
        } else {
            deleteRecipeBtn.setVisibility(View.GONE);
        }

    }

    private void saveInfo(boolean update, String recipe_ID, String fotoID) {

        String name = recipe_name.getText().toString();

        Bitmap bitmap = getBitmapFromDrawable(recipe_picture.getDrawable());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();

        try {
            bos.close();
        } catch (IOException ex) {
            if (ex.getMessage() != null) {
                Log.e("TYAM", ex.getMessage());
                return;
            }

            Log.e("TYAM", "Error getting bytearray...", ex);
        }

        String fileReferece;
        if (update) {
            StorageReference images = storage.getReferenceFromUrl(fotoID);
            fileReferece = images.getName();
        } else {
            fileReferece = String.format(Locale.US, "%s_%d.jpg", name, System.currentTimeMillis());
        }

        StorageReference images = storage.getReference("recipesPictures/" + fileReferece);
        images.putBytes(data)
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        Task<Uri> dlUrlTask = images.getDownloadUrl();

                        dlUrlTask.addOnCompleteListener(task1 -> {
                            Uri dlUrl = task1.getResult();
                            if (dlUrl == null) return;

                            String foto = dlUrl.toString();
                            doSave(foto, update, recipe_ID);
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("TYAM", e.getMessage()));

    }

    private void doSave(String pic, boolean update, String id) {
        this.pic = pic;
        this.update = update;
        this.id = id;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        if (user != null) {
            email = user.getEmail();
        }

        String name = recipe_name.getText().toString();
        String desc = recipe_desc.getText().toString();
        boolean priv = recipe_privacy.isChecked();

        Map<String, Object> receta = new HashMap<>();

        receta.put("name", name);
        receta.put("desc", desc);
        receta.put("owner", email);
        receta.put("foto", pic);
        receta.put("privacy", priv);
        receta.put("ingredients", ingredients);
        receta.put("directions", directions);
        receta.put("comments", comments);

        String new_recipeID;
        if (update) {
            new_recipeID = id;
        } else {
            new_recipeID = calculateStringHash(receta.toString());
        }
        receta.put("id", new_recipeID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String finalEmail = email;

        assert new_recipeID != null;
        db.collection("recipes").document(new_recipeID)
                .set(receta)
                .addOnSuccessListener(aVoid -> {
                    snackbar.dismiss();
                    Snackbar.make(root, R.string.data_saved, Snackbar.LENGTH_LONG).show();
                    getActivity().finish();

                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "datos no registrados!", Toast.LENGTH_LONG).show());
    }

    private void deleteRecipe(String id, String foto) {
        //Codigo para borrar la foto correspondiente a la receta
        StorageReference images = storage.getReferenceFromUrl(foto);
        String fileRef = images.getName();

        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child("recipesPictures/" + fileRef);
        desertRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            System.out.println("Foto eliminada");
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            System.out.println("No se pudo eliminar foto");
        });

        //Codigo para borrar datos de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    snackbar.dismiss();
                    Snackbar.make(root, R.string.recipe_erased, Snackbar.LENGTH_LONG).show();
                    getActivity().finish();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    public void removeIngredient(String item) {
        ingredients.remove(item);
        adapter.notifyDataSetChanged();
    }

    public void removeDirection(String item) {
        directions.remove(item);
        adapter2.notifyDataSetChanged();
    }

    private Bitmap getBitmapFromDrawable(Drawable drble) {
        if (drble instanceof BitmapDrawable) {
            return ((BitmapDrawable) drble).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drble.getIntrinsicWidth(), drble.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drble.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drble.draw(canvas);

        return bitmap;
    }

    private String calculateStringHash(String input) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes());
            byte[] digest = md5.digest();

            StringBuilder sb = new StringBuilder(digest.length * 2);

            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 8) & 0xf, 16));
                sb.append(Character.forDigit(b & 0xf, 16));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Log.e("TYAM", ex.getMessage());
        }
        return null;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) return;
            Uri uri = data.getData();
            recipe_picture.setImageURI(uri);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}

