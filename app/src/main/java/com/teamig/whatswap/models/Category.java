package com.teamig.whatswap.models;

/**
 * Created by lyk on 26/5/17.
 */

public class Category {
    private int imageId;
    private String name;

    public Category(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
