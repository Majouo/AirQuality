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

    double howFar;

    JsonObject data;

    public Station(int id, Location location, String stationName) {
        this.id = id;
        this.location = location;
        this.stationName = stationName;
        howFar =-1;
    }

    public void setData(JsonObject data) {
        this.data = data;
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

    public JsonObject getStationData()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL endpoint = null;
                try {
                    endpoint = new URL("https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"+id);
                    HttpURLConnection connection = (HttpURLConnection)endpoint.openConnection();
                    if(connection.getResponseCode() == 200){
                        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                        JsonParser parser = new JsonParser();
                        JsonObject jsonObject = (JsonObject) parser.parse(reader);
                        data=jsonObject;
                    }else{
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return data;
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
