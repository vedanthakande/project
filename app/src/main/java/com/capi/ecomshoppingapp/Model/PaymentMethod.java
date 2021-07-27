package com.capi.ecomshoppingapp.Model;

public class PaymentMethod {
    private String id;
    private String title;
    private int image;
    private boolean selected;

    public PaymentMethod(String id, String title, int image, boolean selected) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.selected = selected;
    }

    public PaymentMethod() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
