package com.oman.sayakil;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class AppController extends MultiDexApplication implements GoogleApiClient.OnConnectionFailedListener {

    private static AppController appController;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location globalLocation;

    public AppController() {
        appController = this;
    }

    public static AppController getAppInstance() {
        return appController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        MultiDex.install(getApplicationContext());

    }

    public void initialize() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(Utils.UPDATE_INTERVAL)
                .setFastestInterval(Utils.FASTEST_INTERVAL);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public LocationRequest getGlobalLocationRequest() {
        return mLocationRequest;
    }

    public GoogleApiClient getGlobalGoogleApiClient() {
        return mGoogleApiClient;
    }

    public Location getGlobalLocation() {
        return globalLocation;
    }

    public void setGlobalLocation(Location location) {
        globalLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }
}
