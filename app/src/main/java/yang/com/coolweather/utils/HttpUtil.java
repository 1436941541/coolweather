package yang.com.coolweather.utils;


import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**用来向服务器发送请求
 * Created by 杨云杰 on 2018/4/30.
 */

public class HttpUtil {
    private static HttpUtil httpUtil;
    public synchronized static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
            return httpUtil;
    }
    public void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
