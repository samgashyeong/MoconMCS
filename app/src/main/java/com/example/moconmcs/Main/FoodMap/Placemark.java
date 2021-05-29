package com.example.moconmcs.Main.FoodMap;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Placemark {
    private final String name;
    private final String desc;
    private final ArrayList<Double> pos;

    public Placemark(String name, String desc, ArrayList<Double> pos) {
        this.name = name;
        this.desc = desc;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<Double> getPos() {
        return pos;
    }

    public double getLatitude() {
        return pos.get(0);
    }

    public double getLongitude() {
        return pos.get(1);
    }

    @NotNull
    @Override
    public String toString() {
        return "Placemark[name=" + name + ", pos={" + pos.get(0) + ", " + pos.get(1) + ")]";
    }
}
