package com.example.navimap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lng = 0.0;
    private double lat = 0.0;
    private String nameOfPlace = "";

    ViewGroup reg_form;

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

        Button btnChoice = (Button) findViewById(R.id.choice_Button);
        btnChoice.setOnClickListener(viewClickListener);
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

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.menu1: {
                                Toast.makeText(getApplicationContext(),
                                        "Кинотеатр",
                                        Toast.LENGTH_SHORT).show();
                                lat = 57.999956;
                                lng = 56.248481;
                                nameOfPlace = "Кинотеатр «Кристалл»";
                                onMapReady(mMap);
                                return true;
                            }
                            case R.id.menu2:
                                Toast.makeText(getApplicationContext(),
                                        "Заправка",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.menu3:
                                Toast.makeText(getApplicationContext(),
                                        "Больница",
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

        // Add a marker in Sydney and move the camera
        //LatLng location = new LatLng(-34, 151);
        if(lat != 0.0 || lng != 0.0) {
            LatLng location = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(location).title("Marker in " + nameOfPlace));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        }
    }

    public void onClickSignUpButton(View view) {
        reg_form.setVisibility(View.INVISIBLE);
    }

    public void onClickSignUpTestButton(View view) {
        reg_form.setVisibility(View.VISIBLE);
    }


    public void onClickCloseSignUpFormButton(View view) {
        reg_form.setVisibility(View.INVISIBLE);
    }
}
