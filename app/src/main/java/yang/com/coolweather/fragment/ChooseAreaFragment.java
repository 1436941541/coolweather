package yang.com.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import yang.com.coolweather.MainActivity;
import yang.com.coolweather.R;
import yang.com.coolweather.WeatherActivity;
import yang.com.coolweather.db.City;
import yang.com.coolweather.db.Country;
import yang.com.coolweather.db.Province;
import yang.com.coolweather.gson.Weather;
import yang.com.coolweather.urls.Url;
import yang.com.coolweather.utils.HttpUtil;
import yang.com.coolweather.utils.Utility;

/**
 * 这个碎片是遍历市和县的
 * Created by 杨云杰 on 2018/4/30.
 */

public class ChooseAreaFragment extends Fragment {
    /**
     * 这几个常量代表的是选取的内容是城市，乡镇和省份的时候
     * 还有一一个当前的选取的是什么的常量
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private int currentLevel;
    public static boolean a=false;
    public static String  b;
    private TextView title_text;
    private Button back;
    private ProgressDialog progressDialog;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    /**
     * 记录选取的省份和城市
     */
    private Province selectedProvince;
    private City selectedCity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        title_text = (TextView)view.findViewById(R.id.title_text);
        back = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }
                else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountry();
                }
                else  if (currentLevel == LEVEL_COUNTRY) {
                    String weatherId = countryList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        //当选取country是在MainActivity中的时候回跳转
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    //当fragment是在weatherActivity的时候关闭侧滑栏，显示在加载
                    else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        a = true;
                        b = weatherId;
                        activity.requestWeather(weatherId);
                        Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTRY) {
                    queryCity();
                }
                else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    /**
     * ，默认启动为先查找省份的
     * 查找数据的时候先判断数据库里的数据是否含有。
     * 有的话就在listview上刷新出来
     * 没有的话就去url上面查找
     */
    private void queryProvince(){
        title_text.setText("中国");
        back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            //将数据加载进来后刷新adapter，listview置于第一行当前选取的等级置为省等级
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            queryFromServer(Url.PROVINCE_ADDRESS,"province");
        }
    }

    /**
     * 查找城市，更具你选取的省份的id来找其中的数据
     */
    private void queryCity(){
        title_text.setText(selectedProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = Url.PROVINCE_ADDRESS + "/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    private void queryCountry(){
        title_text.setText(selectedCity.getCityName());
        back.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size() > 0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = Url.PROVINCE_ADDRESS + "/" + provinceCode +"/" + cityCode;
            queryFromServer(address,"country");
        }
    }
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.getInstance().sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                //result用来确定是否成功的解析出了数据
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }
                else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }
                else if ("country".equals(type)){
                    result = Utility.handleCountryResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }
                            else if ("city".equals(type)){
                                queryCity();
                            }
                            else if ("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 展示数据加载的ui
     */
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 隐藏数据加载的ui
     */
    private void closeProgressDialog() {
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
}
