package com.capi.ecomshoppingapp.Model;

public class Address
{
    String addressId;
    String addressCountry;
    String addressFirstName;
    String addressLastName;
    String addressStreet;
    String addressStreetOpt;
    String addressCity;
    String addressState;
    String addressZipCode;
    String addressPhone;
    Boolean selected;

    public Address(String addressId, String addressCountry, String addressFirstName, String addressLastName, String addressStreet, String addressStreetOpt, String addressCity, String addressState, String addressZipCode, String addressPhone, Boolean selected) {
        this.addressId = addressId;
        this.addressCountry = addressCountry;
        this.addressFirstName = addressFirstName;
        this.addressLastName = addressLastName;
        this.addressStreet = addressStreet;
        this.addressStreetOpt = addressStreetOpt;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZipCode = addressZipCode;
        this.addressPhone = addressPhone;
        this.selected = selected;
    }

    public Address() {
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getAddressFirstName() {
        return addressFirstName;
    }

    public void setAddressFirstName(String addressFirstName) {
        this.addressFirstName = addressFirstName;
    }

    public String getAddressLastName() {
        return addressLastName;
    }

    public void setAddressLastName(String addressLastName) {
        this.addressLastName = addressLastName;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public String getAddressStreetOpt() {
        return addressStreetOpt;
    }

    public void setAddressStreetOpt(String addressStreetOpt) {
        this.addressStreetOpt = addressStreetOpt;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressZipCode() {
        return addressZipCode;
    }

    public void setAddressZipCode(String addressZipCode) {
        this.addressZipCode = addressZipCode;
    }

    public String getAddressPhone() {
        return addressPhone;
    }

    public void setAddressPhone(String addressPhone) {
        this.addressPhone = addressPhone;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
