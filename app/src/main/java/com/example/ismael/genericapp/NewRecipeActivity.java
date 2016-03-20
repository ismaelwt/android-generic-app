package com.example.ismael.genericapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ismael.genericapp.adapter.SpinnerAdapter;
import com.example.ismael.genericapp.connect.AsyncTaskPost;
import com.example.ismael.genericapp.entity.GenericActivity;
import com.example.ismael.genericapp.entity.Recipe;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewRecipeActivity extends GenericActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1
            ;
    private EditText editDescription;
    private Spinner spinner;
    private Button btn;
    private ImageView thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);


        editDescription = (EditText) findViewById(R.id.editDescription);
        spinner = (Spinner) findViewById(R.id.recipe);
        btn = (Button) findViewById(R.id.btnNewRecipe);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);

        List<String> ingredients = new ArrayList<>();

        ingredients.add("Ovo");
        ingredients.add("Figado");
        ingredients.add("Feijao");
        ingredients.add("Arroz");
        ingredients.add("Sal");

        SpinnerAdapter adapter = new SpinnerAdapter(this, R.layout.spin_item, ingredients);

        spinner.setAdapter(adapter);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = new Recipe();

                String item = (String) spinner.getSelectedItem();

                recipe.setDescription(editDescription.getText().toString());
                recipe.setIngredients(item);

                new AsyncTaskPost(NewRecipeActivity.this, "http://192.168.25.11:3000/cadastrar-receita", "POST_RECEITA").execute(new Gson().toJson(recipe));

            }
        });
    }

    @Override
    public void onBackPressed() {
        openActicity(this, MainActivity.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                callCamera();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callCamera () {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onResultRequisition(String resul, String idReq) {
        if(idReq.equals("POST_RECEITA")){
            Recipe i = new Gson().fromJson(resul, Recipe.class);
            Toast.makeText(this, "Receita " + i.getDescription() + " criado com sucesso", Toast.LENGTH_LONG).show();
            openActicity(this, MainActivity.class);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == -1) {

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");

            try {
                Bundle extras = intent.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                thumbnail.setImageBitmap(thePic);


                //file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
