package com.example.ferterliserdistribution;

public class User {
    private String name;
    private String email;
    private String password;
    private String roleValue;

    public User(String name, String email, String password, String roleValue) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roleValue = roleValue;
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
}
