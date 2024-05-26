package com.example.ferterliserdistribution;

public class Product {
    String name, image, price, quantity, itemId, category,description,itemTotal,totalAmount,itemCount,remainingQuantity,soldQuantity;

    public Product() {}

    public Product(String name, String image, String price, String quantity, String itemId, String category, String itemTotal, String totalAmount, String itemCount , String remainingQuantity, String soldQuantity,String description) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
        this.itemId = itemId;
        this.category = category;
        this.itemTotal = itemTotal;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.remainingQuantity = remainingQuantity;
        this.soldQuantity = soldQuantity;
        this.description = description;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }


    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(String remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(String soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
