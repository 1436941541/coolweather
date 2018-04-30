package yang.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 解析复杂json数据所建的类，GSON 解析方式,解析basic里面的数据
 * Created by 杨云杰 on 2018/4/30.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public update update;
    public class update{
        @SerializedName("loc")
        public String updateTime;
    }
}
