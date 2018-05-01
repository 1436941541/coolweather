package yang.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 这里是一个数组形式的，可以先定义出单日天气的实体类即可
 * Created by 杨云杰 on 2018/4/30.
 */

public class Forecast {
    public  String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;
    public class Temperature{
        public String max;
        public String min;
    }
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
