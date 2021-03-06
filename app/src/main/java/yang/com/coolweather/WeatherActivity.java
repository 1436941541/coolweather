package yang.com.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import yang.com.coolweather.fragment.ChooseAreaFragment;
import yang.com.coolweather.gson.Forecast;
import yang.com.coolweather.gson.Weather;
import yang.com.coolweather.service.AutoUpdateService;
import yang.com.coolweather.utils.HttpUtil;
import yang.com.coolweather.utils.Utility;


public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    public SwipeRefreshLayout swipeRefresh;
    private ImageView bingPicImg;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private String weatherId;
    private TextView pm25Text;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //把上面的状态栏弄成隐藏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        init();
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        pref = getSharedPreferences("data",MODE_PRIVATE);
        String weatherString = pref.getString("weather",null);
        String bingPic = pref.getString("bing_pic",null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else {
            loadBingPic();
        }
        if (weatherString != null) {
            Weather weather = Utility.handlerWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }
        else {
            weatherId = getIntent().getExtras().getString("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!ChooseAreaFragment.a) {
                    requestWeather(weatherId);
                }
                else {
                    requestWeather(ChooseAreaFragment.b);
                }
                Toast.makeText(WeatherActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadBingPic(){
            String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.getInstance().sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getBaseContext()).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    public void requestWeather(String weatherId){
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                    "&key=cff43f11c2b741219477b897d5de43fd";
        HttpUtil.getInstance().sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handlerWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                        else {
                            Toast.makeText(getBaseContext(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
                loadBingPic();
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        if (weather != null &&weather.status.equals("ok")) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature+"°C";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();//因为这个layout里面是装的未来几天的数据
            //而我们不知道到底要装几天的数据，所以要用addview的这种形式来装载
            for (Forecast forecast:weather.forecastList){
                View view = getLayoutInflater().inflate(R.layout.forecast_item,forecastLayout,false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null){
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度" + weather.suggestion.comfort.info;
            String carWash = "洗车指数" + weather.suggestion.carWash.info;
            String sport = "运动建议" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(getApplicationContext(), AutoUpdateService.class);
            Log.d("yyj", "showWeatherInfo: "+0);
            startService(intent);
        }
        else {
            Toast.makeText(getBaseContext(),"获取天气失败",Toast.LENGTH_SHORT).show();
        }
    }
    private void init(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);
        degreeText = (TextView)findViewById(R.id.degree_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
    }
}
