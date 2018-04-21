package com.ute.dn.speaknow.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class GetTitleTask extends AsyncTask<String, Void, String> {

    TextView txt_title = null;

    public GetTitleTask(TextView txt){
        txt_title = txt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        txt_title.setText("Loading...");
    }

    @Override
    protected String doInBackground(String... urls) {
        return readData(urls[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            txt_title.setText(jsonObject.optString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readData(String urlStr){
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return content.toString();
    }
}
