package com.example.ismael.genericapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ismael.genericapp.connect.AsyncTaskPost;
import com.example.ismael.genericapp.entity.GenericActivity;
import com.example.ismael.genericapp.entity.User;
import com.google.gson.Gson;

public class LoginActivity extends GenericActivity {

    private String PREFS = "PREFS";
    private Button btnLogin, btnCadastrar;
    private EditText editLogin;
    private EditText editConfPass;
    private EditText editPass;
    private Boolean loginView = null;
    private CheckBox chkuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginView = false;

        TextView tx = (TextView) findViewById(R.id.textCadastrar);
        tx.setPaintFlags(tx.getPaintFlags() | Paint.LINEAR_TEXT_FLAG);

        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginView = !loginView;
                showView();
            }
        });

        editLogin = (EditText) findViewById(R.id.editLogin);
        editPass = (EditText) findViewById(R.id.editPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnCadastrar = (Button) findViewById(R.id.btnNewUser);
        chkuser = (CheckBox) findViewById(R.id.chkuser);


        remerberPass();

        chkuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remerberPass();
            }
        });


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editLogin = (EditText) findViewById(R.id.editNewUser);
                editPass = (EditText) findViewById(R.id.editNewPass);
                editConfPass = (EditText) findViewById(R.id.editConfNewPass);

                if (!editPass.getText().toString().isEmpty() && !editLogin.getText().toString().isEmpty() && editPass.getText().toString().equals(editPass.getText().toString())) {
                    User user = new User();

                    user.setUsername(editLogin.getText().toString());
                    user.setPassword(editPass.getText().toString());

                    new AsyncTaskPost(LoginActivity.this, "https://powerful-plains-95757.herokuapp.com/cadastrar", "CADASTRAR").execute(new Gson().toJson(user));
                } else if (editPass.getText().toString().equals(editConfPass.getText().toString())) {
                    editPass.setError("Senhas diferentes");
                    editConfPass.setError("Senhas diferentes");
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!editPass.getText().toString().isEmpty() && !editLogin.getText().toString().isEmpty()) {
                    User user = new User();

                    user.setUsername(editLogin.getText().toString());
                    user.setPassword(editPass.getText().toString());
                    // IP EXTERNO   https://infinite-thicket-16564.herokuapp.com/
                    new AsyncTaskPost(LoginActivity.this, "https://powerful-plains-95757.herokuapp.com/login", "LOGIN").execute(new Gson().toJson(user));
                }else {
                    editLogin.setError("Invalid Username");
                    editPass.setError("Invalido Password");
                }


            }
        });
    }


    private void remerberPass() {

        if (chkuser.isChecked() && !editLogin.getText().toString().isEmpty()){
            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", editLogin.getText().toString());
            editor.commit();
        }else if(!chkuser.isChecked() && !editLogin.getText().toString().isEmpty()){

            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("username", "");
            editPass.requestFocus();

        }else {

            SharedPreferences settings = getSharedPreferences(PREFS, 0);
            editLogin.setText(settings.getString("username", ""));
            editPass.requestFocus();
            if(!editLogin.getText().toString().isEmpty()){
                chkuser.setChecked(true);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().hide();
        return super.onCreateOptionsMenu(menu);
    }


    private void showView(){

        final RelativeLayout cadatrar = (RelativeLayout) findViewById(R.id.cadastrarse);
        final RelativeLayout login = (RelativeLayout) findViewById(R.id.login);

        if(cadatrar.getVisibility() == View.VISIBLE){
            cadatrar.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);

        }else {
            cadatrar.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        final RelativeLayout cadatrar = (RelativeLayout) findViewById(R.id.cadastrarse);
        if(cadatrar.getVisibility() == View.VISIBLE) {
            showView();
            return;
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResultRequisition(String resul, String idReq) {
        if(idReq.equals("LOGIN")) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }else if(idReq.equals("CADASTRAR")) {
            if(!resul.isEmpty()){
                User user = new Gson().fromJson(resul, User.class);
                Toast.makeText(LoginActivity.this, "Usuario " + user.getUsername() + "save success", Toast.LENGTH_LONG);
                showView();
            }
        }
    }
}
