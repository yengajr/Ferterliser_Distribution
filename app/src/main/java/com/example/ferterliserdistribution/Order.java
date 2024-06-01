package com.example.ferterliserdistribution;

public class Order {
String emailAddress,itemCount,itemId,orderDate,paymentMethod,productCategory,productName;
int served;

    public Order() {
    }

    public Order(String emailAddress, String itemCount, String itemId, String orderDate, String paymentMethod, String productCategory, String productName, int served) {
        this.emailAddress = emailAddress;
        this.itemCount = itemCount;
        this.itemId = itemId;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.productCategory = productCategory;
        this.productName = productName;
        this.served = served;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public int getServed() {
        return served;
    }

    public void setServed(int served) {
        this.served = served;
    }
}
