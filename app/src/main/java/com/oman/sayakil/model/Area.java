package com.oman.sayakil.model;

public class Area {
    private String image;
    private String address;
    private Boolean available;

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    private String lat;
    private String lng;

    public Area() {
    }

    public Area(String image, String address, boolean available, String lat, String lng) {
        this.image = image;
        this.address = address;
        this.available = available;
        this.lat = lat;
        this.lng = lng;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
