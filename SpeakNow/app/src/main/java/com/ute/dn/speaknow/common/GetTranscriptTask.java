package com.ute.dn.speaknow.common;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ute.dn.speaknow.R;
import com.ute.dn.speaknow.models.Transcript;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GetTranscriptTask extends AsyncTask<String, Integer, List<Transcript>> {

    OnGetTranscriptListener onGetTranscriptTask;

    public GetTranscriptTask(){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(onGetTranscriptTask != null) onGetTranscriptTask.onPreExecute();
    }

    @Override
    protected List<Transcript> doInBackground(String... urls) {
        List<Transcript> lstTranscript = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(urls[0]).openStream());

            NodeList list = doc.getElementsByTagName("p");
            for(int i = 0; i < list.getLength(); i++){
                Node node = list.item(i);
                Element elem = (Element) node;

                Transcript transcript = new Transcript(i, node.getTextContent(), Integer.parseInt(elem.getAttribute("t")),
                        Integer.parseInt(elem.getAttribute("d")));
                lstTranscript.add(transcript);
            }
        } catch (ParserConfigurationException e) {
            Log.d("GetTranscriptTask", "ParserConfigurationException: " + e);
        } catch (IOException e) {
            Log.d("GetTranscriptTask", "IOException: " + e);
        } catch (SAXException e) {
            Log.d("GetTranscriptTask", "SAXException: " + e);
        }
        return lstTranscript;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<Transcript> transcripts) {
        super.onPostExecute(transcripts);
        if(onGetTranscriptTask != null) onGetTranscriptTask.onResult(transcripts);
    }

    public void setOnGetTranscriptTask(OnGetTranscriptListener onGetTranscriptTask){
        this.onGetTranscriptTask = onGetTranscriptTask;
    }

    public interface OnGetTranscriptListener {
        void onPreExecute();
        void onResult(List<Transcript> lst);
    }
}
