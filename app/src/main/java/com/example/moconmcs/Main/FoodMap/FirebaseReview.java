package com.example.moconmcs.Main.FoodMap;

public class FirebaseReview {
    private float rate;
    private String review;
    private long timestamp;

    public FirebaseReview(float rate, String review, long timestamp) {
        this.rate = rate;
        this.review = review;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
