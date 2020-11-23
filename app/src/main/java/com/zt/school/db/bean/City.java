package com.zt.school.db.bean;

public class City {
    public int province_id,city_id,code;
    public String name;

    public City(int city_id, int code, String name) {
        this.city_id = city_id;
        this.code = code;
        this.name = name;
    }
}
