package com.oman.sayakil.ui.bottom_fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oman.sayakil.DirectionsJSONParser;
import com.oman.sayakil.R;
import com.oman.sayakil.Utils;
import com.oman.sayakil.databinding.FragmentMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements LocationListener,OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener{


    private FragmentMapsBinding binding;
    boolean refresh = false;
    Handler handler = new Handler();
    int repeatTime = 3000;
    Geocoder geocoder;
    List<Address> addresses;
    GoogleMap map;
    ArrayList<LatLng> mMarkerPoints;
    double mLatitude = 0;
    double mLongitude = 0;
    Runnable r = new Runnable() {
        @Override
        public void run() {

            if (Utils.isNetworkAvailable(getActivity())) {
                repeatTime = 1000;
                binding.latlong.setVisibility(View.VISIBLE);

                binding.topline.setVisibility(View.GONE);
                if (refresh) {
                    refresh = false;
                    Toast.makeText(getActivity(), "Internet Access!", Toast.LENGTH_SHORT).show();
                }
            } else {
                refresh = true;
                binding.topline.setVisibility(View.VISIBLE);
                binding.latlong.setVisibility(View.GONE);
                repeatTime = 3000;
            }
            handler.postDelayed(r, repeatTime);
        }
    };
    String TAG = "mapactivity";
    boolean showDialog = false;
    String addressFrom, addressTo, distanceInfo;
    boolean mapBusy = false;
    UiSettings mUiSettings;
    private String style = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
       View view = binding.getRoot();


        geocoder = new Geocoder(getContext(), Locale.getDefault());
        binding.latlong.setVisibility(View.GONE);

        binding.latlong.setText("Tap on the map to calculate the distance!");

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, requestCode);
            dialog.show();

        } else {
            SupportMapFragment fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
            displayLocationSettingsRequest(getContext());
            binding.latlong.setOnClickListener(this);
            handler.postDelayed(r, repeatTime);
            binding.topline.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }



    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Initializing
        mMarkerPoints = new ArrayList<>();
        if (style.equals("3d")) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (style.equals("satellite")) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        mUiSettings = map.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            String provider = locationManager.getBestProvider(new Criteria(), true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 1, this);
        }


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                try {
                    if (map == null) {
                        return;
                    }

                    if (map == null) {
                        return;
                    }
                    if (mapBusy) {
                        Utils.mapeBusy(getContext());
                        return;
                    }
                    if (!Utils.isNetworkAvailable(getContext())) {
                        Utils.wifiNotFound(getContext());
                        return;
                    }
                    if (!Utils.isLocationEnabled(getContext())) {
                        Utils.locationNotFound(getContext());
                        return;
                    }
                    // Already map contain destination location
                    if (mMarkerPoints.size() > 1) {
                        mMarkerPoints.clear();
                        map.clear();
                        LatLng startPoint = new LatLng(mLatitude, mLongitude);
                        drawMarker(startPoint);
                    }

                    drawMarker(point);

                    // Checks, whether start and end locations are captured
                    if (mMarkerPoints.size() >= 2) {
                        mapBusy = true;
                        LatLng origin = mMarkerPoints.get(0);
                        LatLng dest = mMarkerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);
                        CalculationByDistance(origin, dest);

                        // Start downloading json data from Google Directions API
                        new DownloadTask().execute(url);
                    }

                } catch (Exception ignored) {
                }
            }
        });
    }

    public void CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i(TAG, "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);

        if (valueResult > 1) {
            distanceInfo = "You are " + kmInDec + " KM " + meterInDec + " M away from destination";
            binding.latlong.setText(String.valueOf("Distance\n" + distanceInfo));
        } else {
            distanceInfo = "You are " + valueResult + " KM away from destination";
            binding.latlong.setText(String.valueOf("Distance\n" + distanceInfo));
        }

        addressFrom = Utils.getCompleteAddressString(getContext(), StartP.latitude, StartP.longitude);
        addressTo = Utils.getCompleteAddressString(getContext(), EndP.latitude, EndP.longitude);

        showDialog = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.latlong:
                openDialog();
                break;
            case R.id.topline:
                Utils.openWifi(getContext());
                break;
        }
    }

    private void openDialog() {
        if (!showDialog) {
            Toast.makeText(getContext(), "Select the destination first.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog dialog = new Dialog(getContext(), R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.rout_info_dialog);
        dialog.setTitle("Route Information!");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView from = dialog.findViewById(R.id.from);
        TextView to = dialog.findViewById(R.id.to);
        TextView innfoDistance = dialog.findViewById(R.id.distanceInfo);
        Button ok = dialog.findViewById(R.id.ok);
        from.setText(addressFrom);
        to.setText(addressTo);
        innfoDistance.setText(distanceInfo);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void drawMarker(LatLng point) {
        if (map == null) {
            return;
        }

        mMarkerPoints.add(point);
        MarkerOptions options = new MarkerOptions();
        options.position(point);
        options.title(getAddress(point));
        if (mMarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_on_24));
        } else if (mMarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_baseline_location_destination_on_24));
        }
        map.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (map == null) {
            return;
        }

        if (mMarkerPoints.size() < 1) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng point = new LatLng(mLatitude, mLongitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(point));
            map.animateCamera(CameraUpdateFactory.zoomTo(14));

            drawMarker(point);
        } else if (mMarkerPoints.size() != 2) {
            map.clear();
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng point = new LatLng(mLatitude, mLongitude);

            map.moveCamera(CameraUpdateFactory.newLatLng(point));
            map.animateCamera(CameraUpdateFactory.zoomTo(14));
            mMarkerPoints = new ArrayList<>();
            drawMarker(point);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //
        // TODO Auto-generated method stub
    }

    @Override
    public void onPause() {

        handler.removeCallbacks(r);
        super.onPause();
    }

    @Override
    public void onResume() {

        handler.postDelayed(r, 3000);
        super.onResume();
    }

    @Override
    public void onStop() {

        super.onStop();
        handler.removeCallbacks(r);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        displayLocationSettingsRequest(getContext());
        return false;
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 12);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    private String getAddress(LatLng location) {
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                if (returnedAddress.getMaxAddressLineIndex() == 0) {
                    if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals("")) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("\n");
                    }
                } else {
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                }
                return strReturnedAddress.toString();
//                Log.w(TAG, "My Current loction address" + strReturnedAddress.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            Log.d(TAG, "DownloadTask doInBackground: ");
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "DownloadTask onPostExecute: ");
            new ParserTask().execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            Log.d(TAG, "ParserTask doInBackground: ");
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                // Starts parsing data
                routes = new DirectionsJSONParser().parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                if (map == null) {
                    return;
                }
                Log.d(TAG, "ParserTask onPostExecute: ");
                System.gc();
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                if (result != null && result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);
                        if (path != null && path.size() > 0) {
                            // Fetching all the points in i-th route
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);
                                if (point != null) {
                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);
                                    points.add(position);
                                }
                            }
                        }

                        if (points.size() > 0) {
                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(5);
                            lineOptions.color(Color.RED);
                        }

                    }
                }

                if (lineOptions != null) {
                    // Drawing polyline in the Google Map for the i-th route
                    map.addPolyline(lineOptions);
                }
                mapBusy = false;
            } catch (Exception ignored) {
            }
        }
    }
}