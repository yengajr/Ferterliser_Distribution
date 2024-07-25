package com.example.ferterliserdistribution;

public class User {
    private String name;
    private String email;
    private String password;
    private String roleValue;
    private String userId;
    private String location;
    private String truckNumber;

    public User() {
    }

    public User(String name, String email, String password, String roleValue, String userId, String location, String truckNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleValue = roleValue;
        this.userId = userId;
        this.location = location;
        this.truckNumber = truckNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

}
