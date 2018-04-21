package com.ute.dn.speaknow.common;

import android.os.AsyncTask;
import android.util.Log;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Caption;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.CaptionSnippet;
import java.util.List;

public class GetCaptionTask extends AsyncTask<String, Void, List<Caption>> {
    private YouTube mService = null;

    public GetCaptionTask(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new YouTube.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("YouTube Data API Android Quickstart")
                .build();
    }

    @Override
    protected List<Caption> doInBackground(String... videoIds) {
        try {
            // Call the YouTube Data API's captions.list method to
            // retrieve video caption tracks.
            Log("listCaptions: OK");
            CaptionListResponse captionListResponse = null;
            captionListResponse = mService.captions().list("snippet", videoIds[0]).execute();
            Log("captionListResponse: " + (captionListResponse == null ? "null" : captionListResponse.getItems().size()));

            List<Caption> captions = captionListResponse.getItems();
            Log("captions: OK");
            Log("captions: " + captions.size() + " item");
            // Print information from the API response.
            Log("\n================== Returned Caption Tracks ==================\n");
            CaptionSnippet snippet;
            for (Caption caption : captions) {
                snippet = caption.getSnippet();
                Log("  - ID: " + caption.getId());
                Log("  - Name: " + snippet.getName());
                Log("  - Language: " + snippet.getLanguage());
                Log("\n-------------------------------------------------------------\n");
            }

            return captions;
        } catch (Exception e) {
            Log("Exception: " + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Caption> captions) {
        Log("captions: " + captions.size() + " item");
        // Print information from the API response.
        Log("\n================== Returned Caption Tracks ==================\n");
        CaptionSnippet snippet;
        for (Caption caption : captions) {
            snippet = caption.getSnippet();
            Log("  - ID: " + caption.getId());
            Log("  - Name: " + snippet.getName());
            Log("  - Language: " + snippet.getLanguage());
            Log("\n-------------------------------------------------------------\n");
        }
    }

    private void Log(String message) {
        Log.d("DemoCaption", "/" + message + "/");
    }
}