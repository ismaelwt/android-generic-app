package com.example.ismael.genericapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ismael.genericapp.adapter.SpinnerAdapter;
import com.example.ismael.genericapp.connect.AsyncTaskDelete;
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
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends GenericActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView text;
    private ListView listView;
    private Button btnNew;
    private String oidRemove;
    private List<Recipe> ls = new ArrayList<>();
    private Integer positionRemove;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe itemAtPosition = (Recipe) parent.getItemAtPosition(position);

                Intent i = new Intent(MainActivity.this, NewRecipeActivity.class);
                i.putExtra("id", itemAtPosition.get_id());
                startActivity(i);
            }
        });

        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Recipe recipe = (Recipe) parent.getItemAtPosition(position);
                oidRemove = recipe.get_id();
                positionRemove = position;


                return false;
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuItem menuitem = menu.add("Remover");
        menuitem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if(oidRemove != null){
                    new AsyncTaskDelete(MainActivity.this, "https://powerful-plains-95757.herokuapp.com/delete/" + oidRemove, "DELETE").execute();
                }

                return false;
            }
        });
    }

    private void getNewRecipe(){
        new AsyncTaskGet(MainActivity.this, "https://powerful-plains-95757.herokuapp.com/get-recipes/", "GET_RECEITA").execute();
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
            if(!resul.isEmpty()){
                Recipe[] r = new Gson().fromJson(resul, Recipe[].class);
                ls = Arrays.asList(r);
                SpinnerAdapter lss = new SpinnerAdapter(MainActivity.this, android.R.layout.simple_list_item_1, ls);
                listView.setAdapter(lss);
            }
        }else if(idReq.equals("DELETE")) {
            ls = new ArrayList<>();

            Recipe[] r = new Gson().fromJson(resul, Recipe[].class);
            ls = Arrays.asList(r);
            SpinnerAdapter lss = new SpinnerAdapter(MainActivity.this, android.R.layout.simple_list_item_1, ls);
            listView.setAdapter(lss);
        }
    }
}
