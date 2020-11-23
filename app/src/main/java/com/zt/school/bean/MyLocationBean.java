package com.zt.school.bean;

public class MyLocationBean {
    public String lng,lat;
    public String adcode;
    public String address;
    public String province,city,district,street;

    public MyLocationBean(String lng, String lat, String adcode, String address, String province, String city, String district, String street) {
        this.lng = lng;
        this.lat = lat;
        this.adcode = adcode;
        this.address = address;
        this.province = province;
        this.city = city;
        this.district = district;
        this.street = street;
    }

    public MyLocationBean() {
    }
}
