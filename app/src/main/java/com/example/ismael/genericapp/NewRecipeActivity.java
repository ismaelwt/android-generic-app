package com.example.ismael.genericapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ismael.genericapp.adapter.SpinnerAdapter;
import com.example.ismael.genericapp.connect.AsyncTaskGet;
import com.example.ismael.genericapp.connect.AsyncTaskPost;
import com.example.ismael.genericapp.entity.GenericActivity;
import com.example.ismael.genericapp.entity.Recipe;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class NewRecipeActivity extends GenericActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText editDescription;
    private Spinner spinner;
    private Button btn;
    private ImageView thumbnail;
    private String bas64Img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);


        if(getIntent().hasExtra("id")){
            new AsyncTaskGet(NewRecipeActivity.this, "https://powerful-plains-95757.herokuapp.com/get-recipes/"+ getIntent().getStringExtra("id") , "GET_ONE").execute();
        }

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ingredients);

        spinner.setAdapter(adapter);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipe recipe = new Recipe();

                String item = (String) spinner.getSelectedItem();

                recipe.setDescription(editDescription.getText().toString());
                recipe.setIngredients(item);

                if(bas64Img != null && !bas64Img.isEmpty()){
                    recipe.setImage(bas64Img);
                }

                //https://powerful-plains-95757.herokuapp.com
                //http://192.168.25.11:3000
                new AsyncTaskPost(NewRecipeActivity.this, "https://powerful-plains-95757.herokuapp.com/cadastrar-receita", "POST_RECEITA").execute(new Gson().toJson(recipe));

            }
        });
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

    public void callCamera() {


            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);

            }
    }

    @Override
    public void onResultRequisition(String resul, String idReq) {
        if (idReq.equals("POST_RECEITA")) {
            Recipe i = new Gson().fromJson(resul, Recipe.class);
            Toast.makeText(this, "Receita " + i.getDescription() + " criado com sucesso", Toast.LENGTH_LONG).show();
            openActicity(this, MainActivity.class);

        }else if(idReq.equals("GET_ONE")){
            Recipe recipe = new Gson().fromJson(resul, Recipe.class);
            if(recipe != null){
                editDescription.setText(recipe.getDescription());
                if(recipe.getImage() != null){
                    byte[] decodedBytes = Base64.decode(recipe.getImage(), 0);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    thumbnail.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "true", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

                } else {
                    Toast.makeText(this, "false", Toast.LENGTH_LONG).show();
                }
                return;
            }
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
                bas64Img = Base64.encodeToString(getBytesFromBitmap(thePic),
                        Base64.NO_WRAP);
                //byte [] byteArray = extras.;
                //bas64Img = Base64.encodeToString(byteArray, 0);


                //file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}
