package com.oman.sayakil.model;

public class MemberModel {
    private String days;
    private String desc;
    private int price;

    public MemberModel(String days, int price,  String desc) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
