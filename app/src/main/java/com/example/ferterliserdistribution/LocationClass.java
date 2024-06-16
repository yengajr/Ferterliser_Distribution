package com.example.ferterliserdistribution;

public class LocationClass {

    String location,price;

    public LocationClass(String location, String price) {
        this.location = location;
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
