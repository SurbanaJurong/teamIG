package com.teamig.whatswap.models;


import java.util.List;

public class Item {

    public Item(String uid, String name, String desc, String price, String category, String trade, String delivery, List<String> photoUris) {
        this.uid = uid;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.category = category;
        this.trade = trade;
        this.delivery = delivery;
        this.photoUris = photoUris;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public List<String> getPhotoUris() {
        return photoUris;
    }

    public void setPhotoUris(List<String> photoUris) {
        this.photoUris = photoUris;
    }

    private String uid;
    private String name;
    private String desc;
    private String price;
    private String category;
    private String trade;
    private String delivery;
    private List<String> photoUris;


    public Item() {

    }

}
