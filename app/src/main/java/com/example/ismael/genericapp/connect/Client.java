package com.example.ismael.genericapp.connect;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private String urlServer = null;
    private String deviceId;
    private String sessionid;

    public Client(String url, String ID, String sessionId){
        this.deviceId = ID;
        this.sessionid = sessionId;
        this.urlServer = url;
    }

    public String get(HashMap<String, Object> param) {

        URL url = null;
        try {

            url = new URL(urlServer);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int HttpResult = connection.getResponseCode();

            if(HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED) {
                return convertStreamToString(connection.getInputStream());
            } else {
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    public String post(String json) {
        try {
            URL obj = new URL(urlServer);

            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");

            if (json != null) {
                setParameters(json, connection);
            }

            int HttpResult = connection.getResponseCode();

            if(HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_CREATED) {
                return convertStreamToString(connection.getInputStream());
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            throw new InternalError("URL incorreta");
        } catch (IOException e) {
            throw new InternalError("Erro efetuar autenticação");
        }
    }

    public static void setParameters(String json, HttpURLConnection conexao) {
        try {

            Gson gson = new Gson();

            Type stringStringMap = new TypeToken<Map<String, String>>() {}.getType();

            Map<String, String> credentials = gson.fromJson(json, stringStringMap);

            DataOutputStream wr = new DataOutputStream(conexao.getOutputStream ());

            JSONObject cred = new JSONObject();

            for (Map.Entry<String, String> entry : credentials.entrySet()) {
                cred.put(entry.getKey(), entry.getValue());
            }

            wr.writeBytes(cred.toString());
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream inputStream) throws IOException {
        Writer writer = null;
        if (inputStream != null) {
            writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return writer.toString();
        }
    }
}