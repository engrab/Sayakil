package com.oman.sayakil.model;

public class CycleModel {
    private String title;
    private int price;
    private String image;

    public CycleModel(String title, int price, String image) {
        this.title = title;
        this.price = price;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CycleModel(String title, int price) {
        this.title = title;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public CycleModel() {
    }

    public CycleModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
