package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView cityName,temperatureTV,conditionTV;
    private TextInputEditText cityEdit;
    private ImageView weather,backIV,sicon;
    private RecyclerView weatherRV;
    private RelativeLayout RL;
    private ProgressBar pb;
    private ArrayList<weatherRVmodal> weatherRVmodalArrayList;
    private RVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int permission_code=1;
    private String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName=findViewById(R.id.cityName);
        temperatureTV=findViewById(R.id.tempValue);
        conditionTV=findViewById(R.id.weatherCond);
        cityEdit=findViewById(R.id.search);
        weatherRV=findViewById(R.id.bottom);
        RL=findViewById(R.id.RL);
        pb=findViewById(R.id.PB);
        weather=findViewById(R.id.weatherIcon);
        backIV=findViewById(R.id.bg);
        sicon=findViewById(R.id.searchIcon);
        weatherRVmodalArrayList = new ArrayList<>();
        weatherRVAdapter = new RVAdapter(this, weatherRVmodalArrayList );
        weatherRV.setAdapter(weatherRVAdapter);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},permission_code);
        }
        Location location= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null){
        city=getCityName(location.getLongitude(),location.getLatitude());
        getWeatherInfo(city);}


        sicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityy= cityEdit.getText().toString();
                if(cityy.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter city Name",Toast.LENGTH_SHORT).show();
                }else {
                    cityName.setText(city);
                    getWeatherInfo(cityy);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==permission_code){
            if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission grnated",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Please provide the permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName="Not Found";
        Geocoder gcd= new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr:addresses){
                if(adr!=null){
                    String city =adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;

                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"User City Not Found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityNamee){
    String url="http://api.weatherapi.com/v1/forecast.json?key=a3503be053d24893baa80833232304&q="+cityNamee+"&days=1&aqi=yes&alerts=yes";
    cityName.setText(cityNamee);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pb.setVisibility(View.GONE);
                RL.setVisibility(View.VISIBLE);
                weatherRVmodalArrayList.clear();


                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°c");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condition= response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon= response.getJSONObject("current").getJSONObject("condition").getString("text");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weather);
                    conditionTV.setText(condition);
                    if(isDay==1){
                        //morning
                        Picasso.get().load("https://www.freepik.com/free-photo/sun-rays-cloudy-sky_12108628.htm#query=day%20weather&position=10&from_view=search&track=robertav1_2_sidr").into(backIV);
                    }else {
                        Picasso.get().load("https://unsplash.com/photos/q4TfWtnz_xw").into(backIV);
                    }
                    JSONObject forecastObj= response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time= hourObj.getString("time");
                        String temp= hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind= hourObj.getString("wind_kph");
                        weatherRVmodalArrayList.add(new weatherRVmodal(time,temp,img,wind));

                    }
                    weatherRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please enter valid city name...",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}