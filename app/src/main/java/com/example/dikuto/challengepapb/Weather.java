package com.example.dikuto.challengepapb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Weather extends AppCompatActivity {

    private String kota;
    double latitude, longitude;
    private TextView latText,lonText;
    private TextView textTemp, textWeather, textCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            kota = bundle.getString("kota");
            latitude = bundle.getDouble("lat");
            longitude = bundle.getDouble("lon");
            latText = findViewById(R.id.latText);
            lonText = findViewById(R.id.lonText);
            textTemp = findViewById(R.id.textTemp);
            textWeather = findViewById(R.id.textWeather);
            textCity = findViewById(R.id.textCity);

            if (latitude != 0 && longitude != 0) {
                latText.setText("Lat :" + latitude);
                lonText.setText("Lon :" + longitude);
            }
            if (kota != null) {
                textCity.setText(kota);
                findWeather(latitude,longitude);
            }
        }
    }

    private void findWeather(double lat, double lon) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=3fd8da85e581b3ff8dfb191ea4454620";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET,
                url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");

                    JSONObject object = array.getJSONObject(0);

                    String temp = String.valueOf(Math.round((main_object.getDouble("temp")-273.15)));
                    String weather = object.getString("main");

                    textTemp.setText(temp + "C");
                    textWeather.setText(weather);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection";
                } else if (volleyError instanceof ServerError) {
                    message = "The location could not be found. Please try again";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);
    }

}
