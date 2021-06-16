package com.example.moconmcs.Main.FoodMap;

import com.google.firebase.Timestamp;

public class ReviewInfo extends FirebaseReview {
    private String name;

    public ReviewInfo(float rate, String review, String name, long timestamp) {
        super(rate, review, timestamp);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
