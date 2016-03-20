package com.example.ismael.genericapp.connect;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.provider.Settings;

import com.example.ismael.genericapp.entity.GenericActivity;

import java.util.HashMap;

public class AsyncTaskGet extends AsyncTask<HashMap<String, Object>, Double, String>{

    private GenericActivity activity;
    private String urlServidor;
    private String idcom;
    private ProgressDialog progress;
    private String result;

    public AsyncTaskGet(GenericActivity activity, String urlServidor, String idcom) {
        this.activity = activity;
        this.urlServidor = urlServidor;
        this.idcom = idcom;
    }



    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(activity, "Wait..", "sending", true, true);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

    }




    @Override
    protected String doInBackground(HashMap<String, Object>... params) {
        try {

            HashMap<String, Object> hmsend = null;
            if(params != null){
                if(params.length > 0){
                    hmsend = params[0];
                }
            }

            String android_id = Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.ANDROID_ID);


            result = new Client(urlServidor, "GTTSession.getInstance(activity).getSessionID()", android_id).get(hmsend);

            //statuscode = hashResposta.get("statuscode");

            //String resposta = hashResposta.get("resposta");
            return  result;

        } catch (Exception e) {
            return "Erro "+ e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        progress.dismiss();

        if(result != null) {
            activity.onResultRequisition(result, idcom);
        }

    }
}

