package yang.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 存储AQI的数据
 * Created by 杨云杰 on 2018/4/30.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
