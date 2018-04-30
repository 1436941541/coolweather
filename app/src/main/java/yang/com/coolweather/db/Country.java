package yang.com.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * 用来存储县城和区
 * Created by 杨云杰 on 2018/4/30.
 */

public class Country extends DataSupport {
    private int id;
    private int cityId;
    private int cityCode;
    private String cityName;

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

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
