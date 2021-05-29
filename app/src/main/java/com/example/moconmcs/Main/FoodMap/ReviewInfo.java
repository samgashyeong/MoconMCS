package com.example.moconmcs.Main.FoodMap;

import com.google.firebase.Timestamp;

public class ReviewInfo {
    private float rate;
    private String review;
    private String name;
    private long timestamp;

    public ReviewInfo(float rate, String review, String name, long timestamp) {
        this.rate = rate;
        this.review = review;
        this.name = name;
        this.timestamp = timestamp;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
