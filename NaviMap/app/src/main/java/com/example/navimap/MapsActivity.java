package com.example.navimap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Лабораторные работы 4,5 сданы Журавлев, Касимов



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lng = 0.0;
    private double lat = 0.0;
    private String nameOfPlace = "";
    public static final String preferences_userInfo_set = "userInfo_set";
    static Boolean isRegUsernameRight = false;

    ViewGroup reg_form;
    ViewGroup auth_form;
    TextInputEditText reg_username_textInput;
    TextInputLayout reg_username_layout;
    TextInputEditText auth_username_textInput;
    TextInputLayout auth_username_layout;


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
        reg_username_textInput = (TextInputEditText)findViewById(R.id.reg_username_editText);
        auth_username_textInput = (TextInputEditText)findViewById(R.id.auth_username_editText);
        reg_username_layout = (TextInputLayout)findViewById(R.id.reg_usernameInput);
        auth_username_layout = (TextInputLayout)findViewById(R.id.auth_usernameInput);
        Pattern p_reg_username= Pattern.compile("^[a-zA-Z]([a-zA-Z0-9]){4,19}$");
        reg_username_textInput.addTextChangedListener(new addListenerOnTextChange(this,reg_username_textInput,p_reg_username));
        auth_username_textInput.addTextChangedListener(new addListenerOnTextChange(this,auth_username_textInput,p_reg_username));
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
        auth_form.setVisibility(View.INVISIBLE);
        reg_form.setVisibility(View.INVISIBLE);

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




    //SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
    //SharedPreferences.Editor edit = sp.edit(); // редактирование данных
    //Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>()); // множество строк, в котором хранятся логин/пароль


    public void onClickSignUpButton(View view)
    {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit(); // редактирование данных
        Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>()); // множество строк, в котором хранятся логин/пароль
        TextInputEditText username_textInput = (TextInputEditText)findViewById(R.id.reg_username_editText);
        String entered_username = username_textInput.getText().toString(); // присваиваем значение поля Логин
        Boolean isUsed = false; // проверка на использование логина

        for (String s: userInfo_set) // проходим по списку. если встречается, то выводим сообщение об уже использовании
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
            if (isRegUsernameRight)
            {
                TextInputEditText password_textInput = (TextInputEditText)findViewById(R.id.reg_password_editText);
                String userInfo = entered_username +"/"+ password_textInput.getText().toString();
                userInfo_set.add(userInfo);
                edit.putStringSet(preferences_userInfo_set,userInfo_set); // куда и что сохраняем
                edit.apply(); // вступление изменений в силу
                Toast.makeText(getApplicationContext(),"You are signed up!",Toast.LENGTH_LONG).show();
                reg_form.setVisibility(View.INVISIBLE);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Wrong Login!",Toast.LENGTH_SHORT).show();
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
        // сделать получение значений из userInfo_set
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences.Editor edit = sp.edit(); // редактирование данных
        Set<String> userInfo_set = sp.getStringSet(preferences_userInfo_set,new HashSet<String>()); // множество строк, в котором хранятся логин/пароль
        TextInputEditText auth_loginText = (TextInputEditText) findViewById(R.id.auth_username_editText);
        String entered_login = auth_loginText.getText().toString(); // присваиваем значение поля Логин
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
                auth_form.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Hello, " + login,Toast.LENGTH_LONG).show();
            }
        }

        if (!isFound)
        {
            Toast.makeText(getApplicationContext(),"Wrong login or password",Toast.LENGTH_SHORT).show();
        }


        /*
        TextInputEditText username_textInput = (TextInputEditText)findViewById(R.id.reg_username_editText);
        String entered_username = username_textInput.getText().toString(); // присваиваем значение поля Логин
        Boolean isUsed = false; // проверка на использование логина
        */
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
            isRegUsernameRight = m.matches();
            if (isRegUsernameRight)
            {
                mEdittextview.setError(null);
                reg_username_layout.setErrorEnabled(false);
            }
            else
            {
                reg_username_layout.setErrorEnabled(true);
                mEdittextview.setError("Login doesn't match pattern!");
            }

        }
    }
}

