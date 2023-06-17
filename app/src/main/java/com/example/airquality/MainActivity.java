package com.example.airquality;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;
import com.google.gson.Gson;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    private Location userLocation;

    List<Station> stations;

    HashMap<String, AirInfo> airInfos;
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stations = new ArrayList<>();
        airInfos = new HashMap<>();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        fetchUserLocation();

        recyclerView = findViewById(R.id.recyclerView);

        if(fetchUserLocation()==true)
        {
            try {
                System.out.println(stations.get(0).toString()+" "+stations.get(0).howFar+" Km");
            }
            catch (IndexOutOfBoundsException e)
            {
            }
            System.out.println(userLocation);
            fetchStations();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.location:
                if(fetchUserLocation()==true)
                {
                    try {
                        System.out.println(stations.get(0).toString()+" "+stations.get(0).howFar+" Km");
                    }
                    catch (IndexOutOfBoundsException e)
                    {

                    }
                    System.out.println(userLocation);
                    item.setIcon(getResources().getDrawable(R.drawable.baseline_location_on_24));
                    fetchStations();
                }
                else
                {
                    item.setIcon(getResources().getDrawable(R.drawable.baseline_wrong_location_24));
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean fetchUserLocation()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                if(isGPSEnabled())
                {
                    LocationServices.getFusedLocationProviderClient(MainActivity.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);

                            LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                    .removeLocationUpdates(this);
                            if(locationResult!=null&&locationResult.getLocations().size()>0)
                            {
                                int index= locationResult.getLocations().size()-1;
                                userLocation= new Location(locationResult.getLocations().get(index).getLongitude(),locationResult.getLocations().get(index).getLatitude());
                            }
                        }
                    }, Looper.getMainLooper());
                    if(userLocation!=null)
                    {
                        return true;
                    }
                }else
                {
                    turnOnGPS();
                }
            }else
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        return false;
    }
    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private void fetchStations() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL endpoint = null;
                try {
                    endpoint = new URL("https://api.gios.gov.pl/pjp-api/rest/station/findAll");
                    HttpURLConnection connection = (HttpURLConnection)endpoint.openConnection();
                    if(connection.getResponseCode() == 200){
                        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray = (JsonArray)parser.parse(reader);

                            stations.clear();
                            for(int i=0;i<jsonArray.size();i++)
                            {
                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                Location location =new Location(jsonObject.get("gegrLon").getAsDouble(),jsonObject.get("gegrLat").getAsDouble());
                                Station station= new Station(jsonObject.get("id").getAsInt(),location,jsonObject.get("stationName").getAsString());
                                stations.add(station);
                            }
                          adjustToUserLocation();
                          searchForData();
                    }else{
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void adjustToUserLocation()
    {
        final double radius = 6371;
        for (Station station:stations) {

            double uLat= Math.toRadians(userLocation.latitude);
            double uLon= Math.toRadians(userLocation.longitude);
            double sLat= Math.toRadians(station.getLocation().latitude);
            double sLon= Math.toRadians(station.getLocation().longitude);

            double adjLat=sLat-uLat;
            double adjLon=sLon-uLon;

            double a=Math.sin(adjLat / 2) * Math.sin(adjLat / 2)
                    + Math.cos(uLat) * Math.cos(sLat)
                    * Math.sin(adjLon / 2) * Math.sin(adjLon / 2);
            double length=radius*(2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
            station.setHowFar(length);
        }
        Collections.sort(stations, new HowFarComparator());
    }

    private boolean isGPSEnabled()
    {
        LocationManager locationManager = null;
        boolean isEnabled=false;
        if(locationManager==null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void searchForData()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int i=0;
                airInfos.clear();
                double searchDistance=30.0;
                while(stations.get(i).getHowFar()<=searchDistance&&i<stations.size()) {
                    URL endpoint = null;
                    try {
                        endpoint = new URL("https://api.gios.gov.pl/pjp-api/rest/station/sensors/"+stations.get(i).getId());
                        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
                        if (connection.getResponseCode() == 200) {
                            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                            JsonParser parser = new JsonParser();
                            JsonArray jsonArray = (JsonArray)parser.parse(reader);

                            endpoint = new URL("https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/"+stations.get(i).getId());
                            connection = (HttpURLConnection) endpoint.openConnection();

                            if (connection.getResponseCode() == 200)
                            {
                                InputStreamReader paramReader = new InputStreamReader(connection.getInputStream());
                                parser = new JsonParser();
                                JsonObject index = (JsonObject) parser.parse(paramReader);

                                for(int j=0;j<jsonArray.size();j++)
                                {
                                    JsonObject jsonObject = jsonArray.get(j).getAsJsonObject();
                                    JsonObject param= jsonObject.get("param").getAsJsonObject();
                                    String code= (param.get("paramCode").getAsString()).toLowerCase();
                                    code=code.replace(".","");
                                    String key = code+"IndexLevel";
                                    if(!airInfos.containsKey(key)) {
                                        JsonObject indexName= index.getAsJsonObject(key);
                                        if(indexName!=null) {
                                            AirInfo value = new AirInfo(param.get("paramFormula").getAsString(), param.get("paramName").getAsString(), indexName.get("id").getAsInt(), stations.get(i).getHowFar());
                                            airInfos.put(key, value);
                                        }
                                    }
                                }
                            }
                            else {
                            }
                        } else {
                        }
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    i++;
                }
                MainActivity.this.runOnUiThread(new Runnable(){
                    public void run() {
                        List<AirInfo> list = new ArrayList<>();
                        for(AirInfo ai : airInfos.values())
                        {
                            list.add(ai);
                        }
                        AirInfoAdapter AirInfoAdapterViewAdapter = new AirInfoAdapter(list,MainActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(AirInfoAdapterViewAdapter);
                    }
                });
            }
        });

    }
}