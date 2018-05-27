package com.example.navimap;


import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;

/**
 * Created by SorA on 5/21/2018.
 */
//  2 комит Саня
public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    HashMap<String, String> closestPlace;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

//        DownloadURL downloadURL = new DownloadURL();
//        try {
//            googlePlacesData = downloadURL.readUrl(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        //showNearbyPlaces(nearbyPlacesList);
    }
}

