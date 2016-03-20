package com.example.ismael.genericapp.connect;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.provider.Settings;

import com.example.ismael.genericapp.entity.GenericActivity;

public class AsyncTaskPost extends AsyncTask<String, Double, String> {

    private GenericActivity activity;
    private ProgressDialog progress;
    private String urlServidor;
    private String idcom;

    private Exception myException;
    private String statuscode;

    public AsyncTaskPost(GenericActivity activity, String urlServidor, String idcom) {
        this.activity = activity;
        this.urlServidor = urlServidor;
        this.idcom = idcom;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(activity, "", "wait ...", true, true);
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            String dadossend = null;
            if(params != null){
                if(params.length > 0){
                    dadossend = params[0];
                }
            }

            String android_id = Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.ANDROID_ID);

            String resposta = new Client(urlServidor, "sesseion_id", android_id).post(dadossend);

            return resposta;

        } catch (Exception e) {
            myException = e;
            //throw new Exception("");
            return "";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        progress.dismiss();

        if(result != null) {
            activity.onResultRequisition(result, idcom);
        }

    }

}
