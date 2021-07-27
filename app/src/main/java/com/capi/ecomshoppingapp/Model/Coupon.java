package com.capi.ecomshoppingapp.Model;

public class Coupon
{
    String couponId;
    String couponCode;
    String couponPrice;
    String couponMin;

    public Coupon(String couponId, String couponCode, String couponPrice, String couponMin) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.couponPrice = couponPrice;
        this.couponMin = couponMin;
    }

    public Coupon() {
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(String couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getCouponMin() {
        return couponMin;
    }

    public void setCouponMin(String couponMin) {
        this.couponMin = couponMin;
    }
}
