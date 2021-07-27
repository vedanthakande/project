package com.capi.ecomshoppingapp.Model;

public class Review
{
    String userId;
    String productId;
    Integer rating;
    String review;
    String date;

    public Review(String userId, String productId, Integer rating, String review, String date) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
        this.date = date;
    }

    public Review() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
