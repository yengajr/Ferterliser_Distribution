package com.example.ferterliserdistribution;


public class ReportClass {

    String itemCount, productName, totalPrice;

    public ReportClass() {}

    public ReportClass(String itemCount,  String productName, String totalPrice) {
        this.itemCount = itemCount;
        this.productName = productName;
        this.totalPrice = totalPrice;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}