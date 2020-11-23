package com.zt.school.db.bean;

public class Area {
    public int city_id,area_id,code;
    public String name;

    public Area(int area_id, int code, String name) {
        this.area_id = area_id;
        this.code = code;
        this.name = name;
    }
}
