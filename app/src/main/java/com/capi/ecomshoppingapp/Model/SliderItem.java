package com.capi.ecomshoppingapp.Model;

public class SliderItem
{
    private String image;
    private String id;

    public SliderItem(String image, String id) {
        this.image = image;
        this.id = id;
    }

    public SliderItem() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
