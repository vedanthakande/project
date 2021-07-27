package com.capi.ecomshoppingapp.Model;

public class Order {
    String orderId;
    String orderCode;
    String userId;
    String orderDate;
    String orderTime;
    String subtotalPrice;
    String totalPrice;
    String shippingPrice;
    String address;
    String paymentMethod;
    String status;
    String paymentStatus;

    public Order(String orderId, String orderCode, String userId, String orderDate, String orderTime, String subtotalPrice, String totalPrice, String shippingPrice, String address, String paymentMethod, String status, String paymentStatus) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.userId = userId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.subtotalPrice = subtotalPrice;
        this.totalPrice = totalPrice;
        this.shippingPrice = shippingPrice;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public Order() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getSubtotalPrice() {
        return subtotalPrice;
    }

    public void setSubtotalPrice(String subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
}
