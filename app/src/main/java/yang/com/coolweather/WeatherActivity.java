package yang.com.coolweather;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import yang.com.coolweather.gson.Weather;
import yang.com.coolweather.utils.Utility;


public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        init();
        pref = getSharedPreferences("data",MODE_PRIVATE);
        String weatherString = pref.getString("weather",null);
        if (weatherString != null) {
            Weather weather = Utility.handlerWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else {
            String weatherId = getIntent().getExtras().getString("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }
    private void requestWeather(String weather){

    }
    private void showWeatherInfo(Weather weather){

    }
    private void init(){
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        degreeText = (TextView)findViewById(R.id.degree_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
    }
}
