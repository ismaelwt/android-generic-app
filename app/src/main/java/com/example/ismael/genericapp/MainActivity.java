package com.example.ismael.genericapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ismael.genericapp.connect.AsyncTaskGet;
import com.example.ismael.genericapp.connect.AsyncTaskPost;
import com.example.ismael.genericapp.entity.GenericActivity;
import com.example.ismael.genericapp.entity.Recipe;
import com.example.ismael.genericapp.entity.User;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends GenericActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView text;
    private ListView listView;
    private Button btnNew;
    private List<String> listaReceitas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNew = (Button) findViewById(R.id.btnNew);
        listView = (ListView) findViewById(R.id.list);

        getNewRecipe();

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewRecipeActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Button btn = (Button) findViewById(R.id.btn);
        //Button btnPost = (Button) findViewById(R.id.btnPost);

        /*btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = new User();
                user.setName("Ismael");
                user.setUsername("ismaelnos5@gmail.com");
                user.setPassword("1234");
                user.setAdmin(true);

                Gson gson = new Gson();

                String json = gson.toJson(user);

                new AsyncTaskPost(MainActivity.this, "http://192.168.25.11:3000/cadastrar",  "POST").execute(json);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTaskGet(MainActivity.this, "https://murmuring-forest-27179.herokuapp.com/", "GET").execute();
            }
        });*/
    }


    private void getNewRecipe(){
        new AsyncTaskGet(MainActivity.this, "http://192.168.25.11:3000/get-recipes", "GET_RECEITA").execute();
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
        if(idReq.equals("GET") && resul != null){
            Toast.makeText(MainActivity.this, resul.toString(), Toast.LENGTH_SHORT).show();
        }else if(idReq.equals("GET_RECEITA")) {

            Recipe[] r = new Gson().fromJson(resul, Recipe[].class);
            List<Recipe> ls = Arrays.asList(r);

            for (Recipe l : ls) {
                if(l.getDescription() != null){
                    listaReceitas.add(0, l.getDescription());
                }
            }

            ArrayAdapter<String> lss = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listaReceitas);
            listView.setAdapter(lss);
        }
    }
}
