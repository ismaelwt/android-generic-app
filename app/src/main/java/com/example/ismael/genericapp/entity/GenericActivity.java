package com.example.ismael.genericapp.entity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class GenericActivity extends Activity {


    public void onResultRequisition(String resul, String idReq) {

    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    public  void openActicity(Context context, Class clazz){
        Intent i = new Intent(context, clazz);
        startActivity(i);
        finish();
    }
}
