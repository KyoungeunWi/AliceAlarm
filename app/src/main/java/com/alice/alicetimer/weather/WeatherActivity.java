package com.alice.alicetimer.weather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.alice.alicetimer.R;
import com.alice.alicetimer.service.AliceService;
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

/*This class is for the activity that showing real time wheather information of main city of the world.
* It brings the weather data from website(http://api.openweathermap.org) using OKhttp
* and shows the information by list.
*/

public class WeatherActivity extends AppCompatActivity {
    private ListView mWeatherListView ;
    public static final String TAG = "WeatherHttpAsyncTask ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        mWeatherListView = findViewById(R.id.weather_list);
        new WeatherHttpAsyncTask()
                .execute("http://api.openweathermap.org/data/2.5/group?id=5809844,1835848,2038349,2968815,5056033,5106292,6167863,1880252,524901,6354908&units=metric&appid=812e0c65a4909def45c5921d49949b93");
    }

    /* When back button is pressed the music playing by Service will stop .
    *  And the screen will move to timer activity */
    @Override
    public void onBackPressed() {
        super.onBackPressed( );
        Intent intentToSevice = new Intent(this, AliceService.class);
        stopService(intentToSevice);
    }


    /* This sub class is a AsyncTask getting weather json data from website and
       manufacturing to show users.
    */
    private class WeatherHttpAsyncTask extends AsyncTask<String, Void , List<WeatherData>> {
        public static final String TAG = "WeatherHttpAsyncTask ";

        @Override
        protected List<WeatherData> doInBackground(String... strings) {

            return getWeatherDataList(strings[0]);
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

    /* This method request the weather data with website URL.
    *  It will get the json data and change it to String
    *  it just pick the city name, weather description, maxium temprature, minium temprature among the datas.
    *  With the picked data this method makes list of weather
    *
    *  parameter : String url (weather data URL)
    *  return : String weather data ,List type*/

    public List<WeatherData> getWeatherDataList(String url){
        String jsonData = null;
        JSONArray jsonArrayList = null;
        JSONObject weatherObject = null ;

        String cityName = null;
        String weatherDescription = null;
        String temp_min = null;
        String temp_max = null;
        WeatherData weatherData = null ;
        List<WeatherData> weatherDataList = new ArrayList<>();

        Response response = null ;

        Log.d(TAG , "url = " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
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

        if (jsonArrayList != null) {
            for (int index = 0; index < jsonArrayList.length( ); index++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = jsonArrayList.getJSONObject(index);
                    Log.d("getWeatherdata:", jsonObject.toString( ));

                    /* getting City name of Weather data */
                    cityName = jsonObject.getString("name");

                    /* getting maxium & minium temperature of the  city */
                    JSONObject jsonObjTemprature = jsonObject.getJSONObject("main");
                    temp_max = jsonObjTemprature.getString("temp_max");
                    temp_min = jsonObjTemprature.getString("temp_min");

                    /* getting gereral weather  discription from weather data */
                    JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                    weatherDescription = jsonObjectWeather.getString("description");

                    Log.d("getWeatherdata:", cityName + ", " + weatherDescription + ", " + temp_max + ", " + temp_min);

                } catch (JSONException e) {
                    e.printStackTrace( );
                }
                weatherData = new WeatherData(cityName,weatherDescription,temp_max,temp_min);
                weatherDataList.add(weatherData);
            }
        }
        return weatherDataList;
    }

}
