package com.alice.alicetimer.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alice.alicetimer.R;
import com.alice.alicetimer.weather.WeatherData.WeatherData;

import java.util.List;

public class WeatherAdapter extends BaseAdapter {
    private final List<WeatherData> mWeatherList ;

    public WeatherAdapter(List<WeatherData> weatherList){
        mWeatherList = weatherList ;
    }

    @Override
    public int getCount() {
        return mWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /* This method is calling the layout of each item of the list
    *  and put data from DB to View*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        WeatherData weatherData = mWeatherList.get(position);
        String city = weatherData.getCityName();
        String weatherDescription = weatherData.getWeatherDesciption() ;
        String minTemp = weatherData.getMinTemprature() ;
        String maxTemp = weatherData.getMaxTemprature() ;

         convertView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_list_item , parent, false);

        TextView cityText = convertView.findViewById(R.id.city);
        TextView weatherDescriptionText = convertView.findViewById(R.id.weather_text);
        TextView temprature = convertView.findViewById(R.id.weather_temp);

        cityText.setText(city);
        weatherDescriptionText.setText(weatherDescription);
        temprature.setText( maxTemp + "°C  /  " +  minTemp +" °C");

        return convertView;
    }
}
