package com.capi.ecomshoppingapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StripeNewUserDetails {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("object")
    @Expose
    private String object;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("delinquent")
    @Expose
    private String delinquent;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDelinquent() {
        return delinquent;
    }

    public void setDelinquent(String delinquent) {
        this.delinquent = delinquent;
    }
}
