package com.zt.school.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zt.school.db.bean.Area;
import com.zt.school.db.bean.City;
import com.zt.school.db.bean.Province;

import java.util.ArrayList;
import java.util.List;

public class DataBaseDao {
    private DataBaseHelper helper;
    public DataBaseDao(Context context){
        helper = new DataBaseHelper(context);
    }
    /**
     * 获取所有省
     */
    public List<Province> getAllProvinces(){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("province", null, null, null, null, null, null);
        List<Province> list = null;
        if(cursor!=null){
            list = new ArrayList<>();
            Province provinceBean;
            while (cursor.moveToNext()){
                int province_id = cursor.getInt(cursor.getColumnIndex("province_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                provinceBean = new Province(province_id, code, name);
                list.add(provinceBean);
            }
            cursor.close();
            rdb.close();
        }
        return list;
    }
    /**
     * 获取所有省名
     */
    public List<String> getAllProvincesNames(){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("province", null, null, null, null, null, null);
        List<String> list = null;
        if(cursor!=null){
            list = new ArrayList<>();
            while (cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex("name"));
                list.add(name);
            }
            cursor.close();
            rdb.close();
        }
        return list;
    }
    /**
     * 获取某省
     */
    public Province getProvinceByProvinceId(int province_id){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("province", null, "province_id="+province_id, null, null, null, null);
        Province provinceBean  = null;
        if(cursor!=null){
            if(cursor.moveToFirst()){
                province_id = cursor.getInt(cursor.getColumnIndex("province_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                provinceBean = new Province(province_id, code, name);
            }
            cursor.close();
            rdb.close();
        }
        return provinceBean;
    }
    /**
     * 通过province_id获取该省下的所以城市
     */
    public List<City> getAllCityByProvinceId(int province_id){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("city", null, "province_id=" + province_id, null, null, null, null);
        List<City> list = null;
        if(cursor!=null){
            list = new ArrayList<>();
            City city;
            while (cursor.moveToNext()){
                int city_id = cursor.getInt(cursor.getColumnIndex("city_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                city = new City(city_id, code, name);
                list.add(city);
            }
            cursor.close();
            rdb.close();
        }
        return list;
    }
    /**
     * 获取某市
     */
    public City getCityByCityId(int city_id){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("city", null, "city_id="+city_id, null, null, null, null);
        City city  = null;
        if(cursor!=null){
            if(cursor.moveToFirst()){
                city_id = cursor.getInt(cursor.getColumnIndex("city_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                city = new City(city_id, code, name);
            }
            cursor.close();
            rdb.close();
        }
        return city;
    }
    /**
     * 根据city_id获取该城市下的所有区
     */
    public List<Area> getAllAreaByCityId(int city_id){
        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor cursor = rdb.query("area", null, "city_id=" + city_id, null, null, null, null);
        List<Area> list = null;
        if(cursor!=null){
            list = new ArrayList<>();
            Area area;
            while (cursor.moveToNext()){
                int area_id = cursor.getInt(cursor.getColumnIndex("area_id"));
                int code = cursor.getInt(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                area = new Area(area_id, code, name);
                list.add(area);
            }
        }
        return list;
    }
}
