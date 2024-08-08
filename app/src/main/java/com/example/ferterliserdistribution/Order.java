package com.example.ferterliserdistribution;

import java.util.List;
import java.util.Map;

public class Order {
    private String emailAddress;
    private String orderId;
    private String orderDate;
    private String paymentNumber;
    private String orderTime;
    private String orderAddress;
    private String served;
    private String distributor;
    private List<Map<String, Object>> products; // List to store multiple products in the order

    public Order() {
    }

    public Order(String emailAddress, String orderDate, String paymentNumber, String orderTime, String orderAddress, String served, String distributor,String orderId, List<Map<String, Object>> products) {
        this.emailAddress = emailAddress;
        this.orderDate = orderDate;
        this.paymentNumber = paymentNumber;
        this.orderTime = orderTime;
        this.orderAddress = orderAddress;
        this.served = served;
        this.distributor = distributor;
        this.products = products;
        this.orderId = orderId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getServed() {
        return served;
    }

    public void setServed(String served) {
        this.served = served;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public List<Map<String, Object>> getProducts() {
        return products;
    }

    public void setProducts(List<Map<String, Object>> products) {
        this.products = products;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
