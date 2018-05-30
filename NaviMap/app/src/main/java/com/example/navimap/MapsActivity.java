package com.example.navimap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Лабораторные работы 4,5 сданы Журавлев, Касимов
//Лабораторные работы 2,3 сданы Журавлев, Касимов


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private double lng = 0.0;
    private double lat = 0.0;
    private String nameOfPlace = "";
    public static final String preferences_userInfo_set = "userInfo_set";
    static Boolean isUsernameRight = false;
    static Boolean isPasswordRight = false;

    ViewGroup reg_form;
    ViewGroup auth_form;
    TextInputEditText reg_username_textInput;
    TextInputEditText reg_password_textInput;
    TextInputLayout reg_username_layout;
    TextInputLayout reg_password_layout;
    TextInputEditText auth_username_textInput;
    TextInputEditText auth_password_textInput;
    TextInputLayout auth_username_layout;
    TextInputLayout auth_password_layout;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private Location mLastKnownLocation;
    double searchRadius = 2000;
    //static public Context mContext;
    //static SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    //static SharedPreferences.Editor edit = sp.edit();
    //static Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        reg_form = findViewById(R.id.reg_form_include);
        reg_form.setVisibility(View.INVISIBLE);
        auth_form = findViewById(R.id.auth_form_include);
        auth_form.setVisibility(View.INVISIBLE);

        reg_username_textInput = (TextInputEditText) findViewById(R.id.reg_username_editText);
        auth_username_textInput = (TextInputEditText) findViewById(R.id.auth_username_editText);
        reg_password_textInput = (TextInputEditText) findViewById(R.id.reg_password_editText);
        auth_password_textInput = (TextInputEditText) findViewById(R.id.auth_password_editText);

        reg_username_layout = (TextInputLayout) findViewById(R.id.reg_usernameInput);
        auth_username_layout = (TextInputLayout) findViewById(R.id.auth_usernameInput);
        reg_password_layout = (TextInputLayout) findViewById(R.id.reg_passwordInput);
        auth_password_layout = (TextInputLayout) findViewById(R.id.auth_passwordInput);

        Pattern p_username = Pattern.compile("^[a-zA-Z]([a-zA-Z0-9]){4,19}$");
        Pattern p_password = Pattern.compile("^([a-zA-Z0-9*-+()!&_\\S]){5,15}([0-9]){1,5}$");
        //^([a-zA-Z0-9*+!&?()]){9,19}$
        reg_username_textInput.addTextChangedListener(new addListenerOnTextChange(this, reg_username_textInput, p_username));
        auth_username_textInput.addTextChangedListener(new addListenerOnTextChange(this, auth_username_textInput, p_username));
        reg_password_textInput.addTextChangedListener(new addListenerOnTextChange(this, reg_password_textInput, p_password));
        auth_password_textInput.addTextChangedListener(new addListenerOnTextChange(this, auth_password_textInput, p_password));
        Button btnChoice = (Button) findViewById(R.id.choice_Button);
        btnChoice.setOnClickListener(viewClickListener);

        // Construct a GeoDataClient
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }
    };

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_p);
        auth_form.setVisibility(View.INVISIBLE);
        reg_form.setVisibility(View.INVISIBLE);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu0: {
                                Toast.makeText(getApplicationContext(),
                                        "Моё местоположение",
                                        Toast.LENGTH_SHORT).show();
                                getDeviceLocation();
                                showDeviceLocation();
                                return true;
                            }
                            case R.id.menu1: {
                                Toast.makeText(getApplicationContext(),
                                        "Кинотеатр",
                                        Toast.LENGTH_SHORT).show();
                                lat = 57.999956;
                                lng = 56.248481;
                                nameOfPlace = "Кинотеатр «Кристалл»";
                                onMapReady(mMap);
                                LatLng location = new LatLng(lat, lng);
                                mMap.addMarker(new MarkerOptions().position(location).title("Marker in " + nameOfPlace));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                                return true;
                            }
                            case R.id.menu2:
                                Toast.makeText(getApplicationContext(),
                                        "Заправка",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu3:
                                mMap.clear();
                                String hospital = "hospital";
                                getDeviceLocation();
                                double lat = mLastKnownLocation.getLatitude();
                                double lng = mLastKnownLocation.getLongitude();
                                String url = getUrl(lat,lng,hospital);
                                Object dataTransfer[] = new Object[4];
                                dataTransfer[0] = mMap;
                                dataTransfer[1] = url;
                                dataTransfer[2] = lat;
                                dataTransfer[3] = lng;

                                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                                getNearbyPlacesData.execute(dataTransfer);
                                Toast.makeText(getApplicationContext(),
                                        "Ближайшая больница",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu4:
                                Toast.makeText(getApplicationContext(),
                                        "Аптека",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu5:
                                Toast.makeText(getApplicationContext(),
                                        "Магазин",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        /*popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "Ошибка",
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        popupMenu.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
//        LatLng location = new LatLng(-34, 151);
//        if (lat != 0.0 || lng != 0.0) {
//            LatLng location = new LatLng(lat, lng);
//            mMap.addMarker(new MarkerOptions().position(location).title("Marker in " + nameOfPlace));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
//        }

        //Проверка и запрос разрешения на использование местоположения
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();

            }
            else
            {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }



        }
        else
        {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location Temp_mLastKnownLocation = (Location) task.getResult();
                        if (Temp_mLastKnownLocation != null)
                        {
                            mLastKnownLocation = Temp_mLastKnownLocation;
                        }

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Fail getDeviceLocation",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /////
    private void showDeviceLocation()
    {
        if (mLastKnownLocation != null)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), 15));
            LatLng MyLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(MyLocation).title("Marker in your location"));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Can't show your location",Toast.LENGTH_SHORT).show();
        }
    }

    private String getUrl(double lat, double lng, String nearbyPlace)
    {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+lat+","+lng);
        googlePlaceUrl.append("&radius="+searchRadius);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAFvBMwGtCsRB4yYZQVO1GurwjQxTXFOFQ");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    public void onClickSignUpButton(View view)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>());
        TextInputEditText username_textInput = (TextInputEditText)findViewById(R.id.reg_username_editText);
        String entered_username = username_textInput.getText().toString();
        Boolean isUsed = false;

        for (String s: userInfo_set)
        {
            String username = s.substring(0,s.indexOf("/"));
            if (username.equals(entered_username))
            {
                isUsed = true;
            }
        }
        if (isUsed)
        {
            Toast.makeText(getApplicationContext(),"Login Already Used!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (isUsernameRight && isPasswordRight)
            {
                TextInputEditText password_textInput = (TextInputEditText)findViewById(R.id.reg_password_editText);
                String userInfo = entered_username +"/"+ password_textInput.getText().toString();
                userInfo_set.add(userInfo);
                edit.putStringSet(preferences_userInfo_set,userInfo_set);
                edit.apply();
                Toast.makeText(getApplicationContext(),"You are signed up!",Toast.LENGTH_LONG).show();
                reg_form.setVisibility(View.INVISIBLE);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Wrong login or password!",Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void onClickSignUpTestButton(View view) {
        auth_form.setVisibility(View.INVISIBLE);
        reg_form.setVisibility(View.VISIBLE);
    }


    public void onClickCloseSignUpFormButton(View view) {
        reg_form.setVisibility(View.INVISIBLE);
    }

    public void onClickSignInRegFormButton(View view) {
        reg_form.setVisibility(View.INVISIBLE);
        auth_form.setVisibility(View.VISIBLE);
    }

    public void onClickCloseSignInFormButton(View view) { auth_form.setVisibility(View.INVISIBLE); }







    public void onClickSignInButton(View view) {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>());
        TextInputEditText auth_loginText = (TextInputEditText) findViewById(R.id.auth_username_editText);
        String entered_login = auth_loginText.getText().toString();
        TextInputEditText auth_passwordText = (TextInputEditText) findViewById(R.id.auth_password_editText);
        String entered_password = auth_passwordText.getText().toString();
        boolean isFound = false;

        for(String str: userInfo_set)
        {
            String login = str.substring(0,str.indexOf("/"));
            String password = str.substring(str.indexOf("/"),str.length()-1);
            if (login.equals(entered_login) && password.equals(entered_password))
            {
                isFound = true;
            }
        }

        if (!isFound)
        {
            Toast.makeText(getApplicationContext(),"Wrong login or password",Toast.LENGTH_SHORT).show();
        }

        else
        {
            if (isUsernameRight && isPasswordRight)
            {
                Toast.makeText(getApplicationContext(),"You are authorized!",Toast.LENGTH_LONG).show();
                auth_form.setVisibility(View.INVISIBLE);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Wrong password!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class addListenerOnTextChange implements TextWatcher {
        private Context mContext;
        TextInputEditText mEdittextview;
        Pattern p;

        public addListenerOnTextChange(Context context, TextInputEditText editTextView, Pattern p) {
            super();
            this.mContext = context;
            this.mEdittextview= editTextView;
            this.p=p;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Matcher m = p.matcher(mEdittextview.getText().toString());
            isUsernameRight = m.matches();
            isPasswordRight = m.matches();
            if (isUsernameRight && isPasswordRight)
            {
                mEdittextview.setError(null);
                reg_username_layout.setErrorEnabled(false);
                auth_username_layout.setErrorEnabled(false);
                reg_password_layout.setErrorEnabled(false);
                auth_password_layout.setErrorEnabled(false);
            }
            else
            {
                reg_username_layout.setErrorEnabled(true);
                auth_username_layout.setErrorEnabled(true);
                reg_password_layout.setErrorEnabled(true);
                auth_password_layout.setErrorEnabled(true);
                mEdittextview.setError("Field doesn't match pattern!");
            }

        }
    }
}