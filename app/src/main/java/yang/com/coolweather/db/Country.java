package yang.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 用来存储县城和区
 * Created by 杨云杰 on 2018/4/30.
 */

public class Country extends DataSupport {
    private int id;
    private int cityId;
    private String countryName;
    private String weatherId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
