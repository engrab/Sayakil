package com.oman.sayakil.model;

public class MemberModel {
    private String days;
    private String desc;
    private String price;

    public MemberModel(String days, String price,  String desc) {
        this.days = days;
        this.desc = desc;
        this.price = price;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
