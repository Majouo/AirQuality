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
    private static final int REQUEST_CHECK_SETTINGS = 10001;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stations = new ArrayList<>();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        fetchUserLocation();

        recyclerView = findViewById(R.id.recyclerView);

        List<AirInfo> list = new ArrayList<>();
        AirInfo testminus = new AirInfo("CO2","Dwutlenek Węgla",-1);
        AirInfo test0 = new AirInfo("CO2","Dwutlenek Węgla",0);
        AirInfo test1 = new AirInfo("CO2","Dwutlenek Węgla",1);
        AirInfo test2 = new AirInfo("CO2","Dwutlenek Węgla",2);
        AirInfo test3 = new AirInfo("CO2","Dwutlenek Węgla",3);
        AirInfo test4 = new AirInfo("CO2","Dwutlenek Węgla",4);
        AirInfo test5 = new AirInfo("CO2","Dwutlenek Węgla",5);
        list.add(testminus);
        list.add(test0);
        list.add(test1);
        list.add(test2);
        list.add(test3);
        list.add(test4);
        list.add(test5);

        AirInfoAdapter AirInfoAdapterViewAdapter = new AirInfoAdapter(list,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(AirInfoAdapterViewAdapter);


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
                        System.out.println(stations.get(0).toString());
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stations.clear();
                                for(int i=0;i<jsonArray.size();i++)
                                {
                                    JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                                    Location location =new Location(jsonObject.get("gegrLon").getAsDouble(),jsonObject.get("gegrLat").getAsDouble());
                                    Station station= new Station(jsonObject.get("id").getAsInt(),location,jsonObject.get("stationName").getAsString());
                                    stations.add(station);
                                }
                                adjustToUserLocation();

                            }
                        });
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
        for (Station station:stations) {
            double adjLon=station.getLocation().longitude-userLocation.longitude;
            double adjLat=station.getLocation().latitude-userLocation.latitude;
            double length=Math.sqrt((adjLon*adjLon)+(adjLat*adjLat));
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
}