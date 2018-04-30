package yang.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 存储now
 * Created by 杨云杰 on 2018/4/30.
 */

public class Now {
    @SerializedName("tem")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More {
        @SerializedName("txt")
        public String info;
    }
}
