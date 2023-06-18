package com.example.airquality;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;

public class Station implements Serializable {

    int id;

    Location location;

    String stationName;

    double howFar;//Km

    public Station(int id, Location location, String stationName) {
        this.id = id;
        this.location = location;
        this.stationName = stationName;
        howFar =Double.MAX_VALUE;
    }

    public double getHowFar() {
        return howFar;
    }

    public void setHowFar(double howFar) {
        this.howFar = howFar;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", " + location +
                ", stationName='" + stationName + '\'' +
                '}';
    }

}

class HowFarComparator implements Comparator<Station> {
    @Override
    public int compare(Station a, Station b) {
        return Double.compare(a.howFar, b.howFar);
    }
}
