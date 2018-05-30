package com.example.navimap;


import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SorA on 5/21/2018.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    double userLat;
    double userLng;
    HashMap<String, String> closestPlace;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        userLat = (double) objects[2];
        userLng = (double) objects[3];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googlePlacesData = downloadURL.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        showNearbyPlaces(nearbyPlacesList);
        showClosestPlace(nearbyPlacesList);

        double lat = Double.parseDouble(closestPlace.get("lat"));
        double lng = Double.parseDouble(closestPlace.get("lng"));
        String url = getDirectionUrl(userLat, userLng, lat, lng);
        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        //dataTransfer[2] = lat;
        //dataTransfer[3] = lng;
        GetDirection.TaskRequestDirections requestDirections = new GetDirection().new TaskRequestDirections();
        requestDirections.execute(dataTransfer);
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList)
    {
        for (int i = 0; i < nearbyPlacesList.size() ; i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlacesList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        }
    }

    private HashMap<String,String> getClosestPlace(List<HashMap<String,String>> nearbyPlacesList)
    {
        float minDistance = Float.MAX_VALUE;
        closestPlace = null;

        for (int i = 0; i < nearbyPlacesList.size() ; i++)
        {
            HashMap<String,String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            float results[] = new float[10];
            Location.distanceBetween(userLng, userLat, lng, lat, results);
            float distance = results[0];
            if (distance < minDistance)
            {
                minDistance = distance;
                closestPlace = googlePlace;
            }

        }

        return closestPlace;
    }

    private void showClosestPlace(List<HashMap<String,String>> nearbyPlacesList)
    {
        MarkerOptions markerOptions = new MarkerOptions();
        HashMap<String, String> googlePlace = getClosestPlace(nearbyPlacesList);

        String placeName = googlePlace.get("place_name");
        String vicinity = googlePlace.get("vicinity");
        double lat = Double.parseDouble(googlePlace.get("lat"));
        double lng = Double.parseDouble(googlePlace.get("lng"));

        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName + " : " + vicinity);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }

    private String getDirectionUrl(double latUser, double lngUser, double latDestination, double lngDestination)
    {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+latUser+","+lngUser);
        googleDirectionUrl.append("&destination="+latDestination+","+lngDestination);
        googleDirectionUrl.append("&sensor=false");
        googleDirectionUrl.append("&mode=walking");
        googleDirectionUrl.append("&key="+"AIzaSyAFvBMwGtCsRB4yYZQVO1GurwjQxTXFOFQ");

        Log.d("MapsActivity", "directionUrl = "+googleDirectionUrl.toString());

        return googleDirectionUrl.toString();
    }
}

