package com.example.airquality;

import java.io.Serializable;

public class AirInfo implements Serializable {
    private String name;
    private String description;
    private int level;

    private double distance;

    public AirInfo(String name, String description, int level,double distance) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "AirInfo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                ", distance=" +  (String.format("%.2f", distance)) +" Km"+
                '}';
    }
}
