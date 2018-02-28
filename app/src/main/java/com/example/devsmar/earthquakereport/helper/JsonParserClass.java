package com.example.devsmar.earthquakereport.helper;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.devsmar.earthquakereport.activity.Earthquake;
import com.example.devsmar.earthquakereport.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Dev Smar on 2/21/2018.
 */

public class JsonParserClass {

    private static final String vFeatures = "features";
    private static final String vProperties = "properties";
    private static final String vMag = "mag";
    private static final String vPlace = "place";
    private static final String vTime = "time";
    private static final String vUrl = "url";


    public JsonParserClass() {
    }

    public static ArrayList<Earthquake> getResultAsArrayList(String url){
        URL orgUrl = createUrl(url);
        ArrayList<Earthquake> resultAsArraylist = null;
        String jsonResponse = "";

        try {
            jsonResponse = makeHttpConnection(orgUrl);
            resultAsArraylist = getArrayListFromJason(jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultAsArraylist;
    }


    private static URL createUrl(String url){
        URL url1 = null;

        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url1;
    }

    private static String makeHttpConnection(URL url) throws IOException {
        String response = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            response = readFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String reader = bufferedReader.readLine();
            while (reader != null){
                stringBuilder.append(reader);
                reader = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }



    private static ArrayList<Earthquake> getArrayListFromJason(String sampleJsonResponse){
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject object = new JSONObject(sampleJsonResponse);
            JSONArray featuresObj = object.optJSONArray(vFeatures);

            for (int i=0; i<featuresObj.length(); i++){
               Double mag = featuresObj.getJSONObject(i).getJSONObject(vProperties).getDouble(vMag);
                String place = featuresObj.getJSONObject(i).getJSONObject(vProperties).getString(vPlace);
                long time = featuresObj.getJSONObject(i).getJSONObject(vProperties).getLong(vTime);
                String url = featuresObj.getJSONObject(i).getJSONObject(vProperties).getString(vUrl);

                Log.i("Result : ",mag + "\t" + place + "\t" + time);

                Earthquake earthquake = new Earthquake(mag, place, time, url);

                earthquakes.add(earthquake);
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return earthquakes;
    }
}
