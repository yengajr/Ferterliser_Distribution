package com.example.ferterliserdistribution;

public class Order {
String emailAddress,itemCount,itemId,orderDate,paymentNumber,productCategory,productName,orderTime,orderAddress,served,distributor,cumulativePrice;

    public Order() {
    }

    public Order(String emailAddress, String itemCount, String itemId, String orderDate, String paymentNumber, String productCategory, String productName, String served, String orderTime, String orderAddress, String distributor, String cumulativePrice) {
        this.emailAddress = emailAddress;
        this.itemCount = itemCount;
        this.itemId = itemId;
        this.orderDate = orderDate;
        this.paymentNumber = paymentNumber;
        this.productCategory = productCategory;
        this.productName = productName;
        this.served = served;
        this.orderTime = orderTime;
        this.orderAddress = orderAddress;
        this.distributor = distributor;
        this.cumulativePrice = cumulativePrice;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getServed() {
        return served;
    }

    public void setServed(String served) {
        this.served = served;
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

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getCumulativePrice() {
        return cumulativePrice;
    }

    public void setCumulativePrice(String cumulativePrice) {
        this.cumulativePrice = cumulativePrice;
    }





}
