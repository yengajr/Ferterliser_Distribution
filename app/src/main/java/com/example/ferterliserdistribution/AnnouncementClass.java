package com.example.ferterliserdistribution;

public class AnnouncementClass {

    String announcement,imageUrl;
    int Number;

    public AnnouncementClass() {
    }

    public AnnouncementClass(String announcement, String imageUrl, int Number) {
        this.announcement = announcement;
        this.imageUrl = imageUrl;
        this.Number = Number;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int Number) {
        this.Number = Number;
    }

}
