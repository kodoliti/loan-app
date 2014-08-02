package com.finance.domain.model;

import javax.persistence.Embeddable;

@Embeddable
public class Contact {

    private String eMailAddress;

    private String mobilePhoneNumber;

    private String apartmentNumber;

    private String buildingNumber;

    private String street;

    private String city;

    private String postcode;

    public Contact(){}

    public Contact(String eMailAddress, String mobilePhoneNumber, String apartmentNumber, String buildingNumber, String street, String city, String postcode) {
        this.eMailAddress = eMailAddress;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.apartmentNumber = apartmentNumber;
        this.buildingNumber = buildingNumber;
        this.street = street;
        this.city = city;
        this.postcode = postcode;
    }

    String geteMailAddress() {
        return eMailAddress;
    }

    String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    String getApartmentNumber() {
        return apartmentNumber;
    }

    String getBuildingNumber() {
        return buildingNumber;
    }

    String getStreet() {
        return street;
    }

    String getCity() {
        return city;
    }

    String getPostcode() {
        return postcode;
    }
}

