package com.alice.alicetimer.weather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.alice.alicetimer.R;
import com.alice.alicetimer.weather.WeatherData.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ListView mWeatherListView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mWeatherListView = findViewById(R.id.weather_list);
        new WeatherHttpAsyncTask()
                .execute("http://api.openweathermap.org/data/2.5/group?id=5809844,1835848,2038349,2968815,5056033,5106292,6167863,1880252,524901,6354908&units=metric&appid=812e0c65a4909def45c5921d49949b93");
    }

    public void cancelButtonClicked(View view) {

    }

    private class WeatherHttpAsyncTask extends AsyncTask<String, Void , List<WeatherData>> {
        public static final String TAG = "WeatherHttpAsyncTask ";

        OkHttpClient client = new OkHttpClient();

        @Override
        protected List<WeatherData> doInBackground(String... strings) {
            List<WeatherData> weatherDataList = new ArrayList<>();
            Response response = null ;
            String jsonData = null;
            JSONArray jsonArrayList = null;
            JSONObject weatherObject = null ;

            String cityName = null;
            String weatherDescription = null ;
            String temp_min = null ;
            String temp_max = null ;

            String strUrl = strings[0];
            Log.d(TAG , "url = " + strUrl);

            Request request = new Request.Builder()
                    .url(strUrl)
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace( );
            }

            try {
                jsonData = response.body().string();
            } catch (IOException e) {
                e.printStackTrace( );
            }
            Log.d(TAG , "response: " + jsonData );


            try {
                weatherObject = new JSONObject(jsonData);
            } catch (JSONException e) {
                e.printStackTrace( );
            }

            try {
                jsonArrayList = weatherObject.getJSONArray("list");
            } catch (JSONException e) {
                e.printStackTrace( );
            }

            if(jsonArrayList != null) {
                for (int index = 0; index < jsonArrayList.length( ); index++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArrayList.getJSONObject(index);
                        Log.d(TAG, jsonObject.toString( ));
                        /* getting City name of Weather data */
                        cityName = jsonObject.getString("name");

                        /* getting maxium & minium temperature of the  city */
                        JSONObject jsonObjTemprature = jsonObject.getJSONObject("main");
                        temp_max = jsonObjTemprature.getString("temp_max");
                        temp_min = jsonObjTemprature.getString("temp_min");
                        //Log.d(TAG ,"temp_max= " + temp_max + ", " + "temp_min= " + temp_min ) ;

                        /* getting gereral weather  discription from weather data */
                        JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                        //Log.d(TAG ,"jsonArrayWeather = " + jsonArrayWeather.toString()) ;
                        JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                        //Log.d(TAG ,"jsonArrayWeather(0) = " + jsonObjectWeather.toString()) ;
                        weatherDescription = jsonObjectWeather.getString("description");

                    } catch (JSONException e) {
                        e.printStackTrace( );
                    }
                    Log.d(TAG, cityName + ", " + weatherDescription + ", " + temp_max + ", " + temp_min);

                    WeatherData weatherData = new WeatherData(cityName, weatherDescription, temp_max, temp_min);
                    weatherDataList.add(weatherData);
                }
            }
            return weatherDataList ;
        }

        @Override
        protected void onPostExecute(List<WeatherData> weatherData) {
            super.onPostExecute(weatherData);
            if(weatherData != null){
                Log.d(TAG , weatherData.toString());
                WeatherAdapter weatherAdapter = new WeatherAdapter(weatherData);
                mWeatherListView.setAdapter(weatherAdapter);
            }
        }
    }

}
