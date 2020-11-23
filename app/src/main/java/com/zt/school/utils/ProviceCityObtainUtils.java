package com.zt.school.utils;

import com.zt.school.db.DataBaseDao;
import com.zt.school.db.bean.Area;
import com.zt.school.db.bean.City;
import com.zt.school.db.bean.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 省市区获取工具类
 */
public class ProviceCityObtainUtils {

    public static List<Province> provinces;

    public static List<String> provinces_names;
    public static List<List<String>> provin_citys_names = new ArrayList<>();
    public static List<List<List<String>>> provin_citys_areas_names = new ArrayList<>();

    public static void loadProvinceCityAreaData(){
        DataBaseDao dataBaseDao = new DataBaseDao(UIUtils.getContext());
        provinces = dataBaseDao.getAllProvinces();
        //获取所有省名
        provinces_names = dataBaseDao.getAllProvincesNames();
        if(provinces!=null && provinces.size()>0){
            for (int i=0;i<provinces.size();i++){
                Province provinceBean = provinces.get(i);

                List<String> cityNames = new ArrayList<>();
                //获取该省下的所有城市
                List<City> citys = dataBaseDao.getAllCityByProvinceId(provinceBean.province_id);
                List<List<String>> areaList = new ArrayList<>();
                if(citys!=null && citys.size()>0){
                    for (int j=0;j<citys.size();j++){
                        City city = citys.get(j);
                        cityNames.add(city.name);
                        //获取该城市下的所有区
                        List<Area> areas = dataBaseDao.getAllAreaByCityId(city.city_id);
                        List<String> areaNames = new ArrayList<>();
                        if(areas!=null && areas.size()>0){
                            for (int k=0;k<areas.size();k++){
                                Area area = areas.get(k);
                                areaNames.add(area.name);
                            }
                        }else { //如果无地区数据，建议添加空字符串，防止数据为null
                            areaNames.add("");
                        }
                        areaList.add(areaNames);
                    }
                }else { //如果无地区数据，建议添加空字符串，防止数据为null
                    cityNames.add("");

                    List<String> testList = new ArrayList<>();
                    testList.add("");
                    areaList.add(testList);
                }
                //获取该省下的所有城市名
                provin_citys_names.add(cityNames);
                provin_citys_areas_names.add(areaList);
            }
        }
    }
}
