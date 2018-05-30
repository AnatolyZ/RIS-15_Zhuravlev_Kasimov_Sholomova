package com.example.navimap;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SorA on 5/30/2018.
 */

public class GetDirection extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {
    String directionData;
    static GoogleMap mMap;

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
        JSONObject jsonObject = null;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jsonObject = new JSONObject(strings[0]);
            DataParser directionsParser = new DataParser();
            routes = directionsParser.parseDirection(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> lists) {

        ArrayList<LatLng> points = null;

        PolylineOptions polylineOptions = null;

        for (List<HashMap<String, String>> path : lists) {
            points = new ArrayList();
            polylineOptions = new PolylineOptions();

            for (HashMap<String, String> point : path) {
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));
                Log.d("MapsActivity", "points_res = "+Double.toString(Double.parseDouble(point.get("lat"))));

                points.add(new LatLng(lat, lon));
            }

            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);
        }
        if (mMap != null) {
            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            }
        } else {
            Log.d("MapsActivity", "mMap null");
        }
    }

    public class TaskRequestDirections extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... objects) {
            mMap =(GoogleMap)objects[0];
            DownloadURL downloadURL = new DownloadURL();
            try {
                directionData = downloadURL.readUrl((String) objects[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  directionData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            GetDirection getDirection = new GetDirection();
            getDirection.execute(s);
        }
    }
}
