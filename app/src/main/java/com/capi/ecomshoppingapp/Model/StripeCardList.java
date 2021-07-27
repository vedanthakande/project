package com.capi.ecomshoppingapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StripeCardList {

    @SerializedName("data")
    @Expose
    private List<CardData> data;

    @SerializedName("default_payment_method")
    @Expose
    private String default_payment_method;

    public List<CardData> getData() {
        return data;
    }

    public String getDefault_payment_method() {
        return default_payment_method;
    }

    public void setDefault_payment_method(String default_payment_method) {
        this.default_payment_method = default_payment_method;
    }

    public void setData(List<CardData> data) {
        this.data = data;
    }

    public class CardData {

        @SerializedName("card")
        @Expose
        private Card card;

        @SerializedName("type")
        @Expose
        private String type;

        private Boolean defaultCard;

        @SerializedName("id")
        @Expose
        private String id;

        public Boolean getDefaultCard() {
            return defaultCard;
        }

        public void setDefaultCard(Boolean defaultCard) {
            this.defaultCard = defaultCard;
        }

        public Card getCard() {
            return card;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class Card {
        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("brand")
        @Expose
        private String brand;

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("exp_month")
        @Expose
        private String exp_month;

        @SerializedName("exp_year")
        @Expose
        private String exp_year;

        @SerializedName("fingerprint")
        @Expose
        private String fingerprint;

        @SerializedName("funding")
        @Expose
        private String funding;

        @SerializedName("last4")
        @Expose
        private String last4;

        @SerializedName("customer")
        @Expose
        private String customer;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getExp_month() {
            return exp_month;
        }

        public void setExp_month(String exp_month) {
            this.exp_month = exp_month;
        }

        public String getExp_year() {
            return exp_year;
        }

        public void setExp_year(String exp_year) {
            this.exp_year = exp_year;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }


    }
}
