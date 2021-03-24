package com.oman.sayakil.ui.bottom_fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.oman.sayakil.AppController;
import com.oman.sayakil.DirectionsJSONParser;
import com.oman.sayakil.LocationServiceRoute;
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
import com.oman.sayakil.ui.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    public static TextView speedometerText;
    private FragmentMapsBinding binding;
    static boolean status;
    public static float maxSpeed;
    Geocoder geocoder;
    List<Address> addresses;
    GoogleMap map;
    ArrayList<LatLng> mMarkerPoints;
    String TAG = "mapactivity";
    boolean mapBusy = false;
    Dialog mDialog;
    int VOICE_REQUEST = 12;
    SharedPreferences sharedpreferences;
    Activity context;
    int mapType = 1;
    LocationServiceRoute myService;
    LocationManager locationManager;
    boolean isFromStart = true;
    boolean isTrafficEnable = false;
    boolean isNightModEnable = false;
    private MapStyleOptions nightMapStyleOptions;
    private MapStyleOptions standradMapStyleOptions;
    private AutoCompleteTextView completeTextView;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationServiceRoute.LocalBinder binder = (LocationServiceRoute.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        speedometerText = binding.ivSpeedometr;

        context = getActivity();
        sharedpreferences = context.getSharedPreferences("AddressPref", Context.MODE_PRIVATE);
        nightMapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.nightmode_map);
        standradMapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.standard_map);
        showSpeedDialog();
        binding.nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNightModEnable) {
                    isNightModEnable = false;
                    binding.nightMode.setImageResource(R.drawable.ic_sun);
                    if (map != null) {
                        map.setMapStyle(standradMapStyleOptions);
                    }
                } else {
                    isNightModEnable = true;
                    binding.nightMode.setImageResource(R.drawable.ic_baseline_nights_stay_24);
                    if (map != null) {
                        map.setMapStyle(nightMapStyleOptions);
                    }
                }
            }
        });
        binding.ivTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTrafficEnable) {
                    isTrafficEnable = false;
                    binding.ivTraffic.setImageResource(R.drawable.ic_baseline_map_24);
                    if (map != null) {
                        map.setTrafficEnabled(false);
                    }
                } else {
                    isTrafficEnable = true;
                    binding.ivTraffic.setImageResource(R.drawable.ic_traffic);
                    if (map != null) {
                        map.setTrafficEnabled(true);
                    }
                }
            }
        });
        binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDestinationDialog();
            }
        });
        binding.ivLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromStart = false;
                showSpeedDialog();
            }
        });
        binding.imgZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        binding.imgZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        binding.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) {
                    if (mapType == 0) {
                        mapType = 1;
                        binding.ivMap.setImageResource(R.drawable.ic_baseline_map_24);
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else if (mapType == 1) {
                        mapType = 2;
                        binding.ivMap.setImageResource(R.drawable.ic_satellite);
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else if (mapType == 2) {
                        mapType = 3;
                        binding.ivMap.setImageResource(R.drawable.ic_hybrid);
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } else if (mapType == 3) {
                        mapType = 0;
                        binding.ivMap.setImageResource(R.drawable.ic_terrain);
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    }

                }
            }
        });
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        int mapStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (mapStatus != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, mapStatus, requestCode);
            dialog.show();
        }

        maxSpeed = sharedpreferences.getFloat("maxSpeed", 80);

        checkGps();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        }
        if (!status) {
            bindService();
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


    void bindService() {

        getContext().startService(new Intent(context, LocationServiceRoute.class));
    }

    void checkGps() {
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    void unbindService() {
//        if (!status)
//        {
//            return;
//        }
        try {

//            if (sc != null)
//            {
//                unbindService(sc);
//                status = false;
//            }
            getContext().stopService(new Intent(context, LocationServiceRoute.class));

//            Intent intent = new Intent(context, LocationServiceRoute.class);
//            intent.setAction(LocationServiceRoute.STOP_SERVICE);
//            startService(intent);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == VOICE_REQUEST && data != null) {
                String address = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                try {
                    if (address == null || address.equals("")) {
                        Toast.makeText(getContext(), "Address not found\nPlease Try Again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    completeTextView.setText(address);
                    completeTextView.setSelection(completeTextView.length());

                } catch (Exception ignored) {

                }
            }
        }

    }

    private void showSpeedDialog() {
        try {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.speedlimit_dialog, null, false);
            final EditText speedLimit = view.findViewById(R.id.speed_limit);
            speedLimit.setText(maxSpeed + "");
            speedLimit.setSelection(speedLimit.length());
//            speedLimit.setSelected(true);
//            speedLimit.setSelectAllOnFocus(true);
            view.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (speedLimit.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Please enter speed limit", Toast.LENGTH_SHORT).show();
                    } else {
                        maxSpeed = Float.parseFloat(speedLimit.getText().toString());
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putFloat("maxSpeed", maxSpeed);
                        editor.apply();
                        mDialog.dismiss();
                        if (isFromStart && !sharedpreferences.getString("address", "").equals("")) {
                            showLoadPreviousDialog(sharedpreferences.getString("address", ""));
                        }
                    }
                }
            });

            mDialog = new Dialog(getContext(), R.style.MaterialDialogSheet);
            mDialog.setContentView(view);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDialog.getWindow().setGravity(Gravity.CENTER);
            }
            mDialog.show();
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("SetTextI18n")
    private void showLoadPreviousDialog(final String destination) {
        try {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.showloadprevous_dialog, null, false);
            final TextView tvDestination = view.findViewById(R.id.tvMessage);
            tvDestination.setText("Destination: " + destination);
            view.findViewById(R.id.btnload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    setLatLng(destination);
                }
            });
            view.findViewById(R.id.btncancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.dismiss();

                }
            });

            mDialog = new Dialog(getContext(), R.style.MaterialDialogSheet);
            mDialog.setContentView(view);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDialog.getWindow().setGravity(Gravity.CENTER);
            }
            mDialog.show();
        } catch (Exception ignored) {
        }
    }

    private void showDestinationDialog() {
        try {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.destination_dialog, null, false);
            view.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (completeTextView.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Please enter destination first", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setLatLng(completeTextView.getText().toString());
                    mDialog.dismiss();
                }
            });
            view.findViewById(R.id.voice).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (getContext().getPackageManager().queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
                                .size() != 0) {
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Where you want to go! Speak Now!");
                            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                            startActivityForResult(intent, VOICE_REQUEST);
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                            alertDialog.setTitle("Warning!");
                            alertDialog.setMessage("Voice Recognition Engine on Your Device is Not Active");
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
            completeTextView = view.findViewById(R.id.Destination);
            completeTextView.setAdapter(new Filter(MapsFragment.this, getContext(), R.layout.adp_auto_complete));
            mDialog = new Dialog(getContext(), R.style.MaterialDialogSheet);
            mDialog.setContentView(view);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDialog.getWindow().setGravity(Gravity.CENTER);
            }
            mDialog.show();
        } catch (Exception ignored) {
        }
    }

    public ArrayList getOptions(String str) {
        HttpURLConnection httpURLConnection;
        Throwable th;
        HttpURLConnection httpURLConnection2 = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            String stringBuilder2 = "https://maps.googleapis.com/maps/api/place/autocomplete/json" +
                    "?key=AIzaSyAg2ClfHcXyv-Yp2RUrERF6Hfn53G0ntHw" +
                    "&input=" + URLEncoder.encode(str, "utf8");
            httpURLConnection = (HttpURLConnection) new URL(stringBuilder2).openConnection();
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
                char[] cArr = new char[1024];
                while (true) {
                    int read = inputStreamReader.read(cArr);
                    if (read == -1) {
                        break;
                    }
                    stringBuilder.append(cArr, 0, read);
                }
                httpURLConnection.disconnect();
                try {
                    JSONArray jSONArray = new JSONObject(stringBuilder.toString()).getJSONArray("predictions");
                    ArrayList arrayList = new ArrayList(jSONArray.length());
                    int i = 0;
                    while (i < jSONArray.length()) {
                        try {
                            System.out.println(jSONArray.getJSONObject(i).getString("description"));
                            System.out.println("============================================================");
                            arrayList.add(jSONArray.getJSONObject(i).getString("description"));
                            i++;
                        } catch (JSONException e) {
                            return arrayList;
                        }
                    }
                    return arrayList;
                } catch (JSONException e2) {
                    return null;
                }
            } catch (MalformedURLException e3) {
                httpURLConnection.disconnect();
                return null;
            } catch (IOException e4) {
                httpURLConnection.disconnect();
                return null;
            } catch (Throwable th2) {
                httpURLConnection2 = httpURLConnection;
                th = th2;
                if (httpURLConnection2 != null) {
                    httpURLConnection2.disconnect();
                }
                throw th;
            }
        } catch (MalformedURLException e5) {
            return null;
        } catch (IOException e6) {
            return null;
        } catch (Throwable th4) {
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }

        }

        return null;
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
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        mMarkerPoints = new ArrayList<>();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (map != null && AppController.getAppInstance().getGlobalLocation() != null) {
            Location location = AppController.getAppInstance().getGlobalLocation();
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(point));
            map.animateCamera(CameraUpdateFactory.zoomTo(18));
        }


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
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation));
        } else if (mMarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_pin));
        }
        map.addMarker(options);
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

    private void setLatLng(String address) {
        try {
            if (address == null || address.equals("")) {
                Toast.makeText(getContext(), "Address not found\nPlease Try Again", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null) {

                    Address locationAddress = addresses.get(0);
                    if (AppController.getAppInstance().getGlobalLocation() == null) {
                        Toast.makeText(getContext(), "Starting point not found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (map == null) {
                        return;
                    }
                    if (locationAddress == null) {
                        Toast.makeText(getContext(), "Address not found\nPlease Try Again", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("address", address);
                    editor.apply();
                    Location location = AppController.getAppInstance().getGlobalLocation();
                    LatLng orign = new LatLng(location.getLatitude(), location.getLongitude());
                    drawMarker(orign);
                    LatLng dest = new LatLng(locationAddress.getLatitude(), locationAddress.getLongitude());
                    drawMarker(dest);
                    String url = getDirectionsUrl(orign, dest);
                    new DownloadTask().execute(url);
                } else {
                    Toast.makeText(getContext(), "Address not found\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    Log.w("", "My Current loction address,No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("", "My Current loction address,Canont get Address!");
            }

        } catch (Exception ignored) {

        }
    }

    private void ExitDialog() {

        try {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.notification_dialog, null, false);
            view.findViewById(R.id.btnAccept).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        mDialog.dismiss();

                    } catch (Exception ignored) {
                    }

                }
            });
            view.findViewById(R.id.btnstop).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (status)
//                    {
                    unbindService();
//                    }
                    mDialog.dismiss();

                }
            });


            mDialog = new Dialog(getContext(), R.style.MaterialDialogSheet);
            mDialog.setContentView(view);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            if (mDialog.getWindow() != null) {
                mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mDialog.getWindow().setGravity(Gravity.BOTTOM);
            }
            mDialog.show();
        } catch (Exception ignored) {
        }
    }

    class Filter extends ArrayAdapter implements Filterable {
        MapsFragment drawPath;
        private ArrayList arrayList;

        Filter(MapsFragment routeHome, Context context, int i) {
            super(context, i);
            this.drawPath = routeHome;

        }

        String m13046a(int i) {
            return this.arrayList.get(i).toString();
        }

        public int getCount() {
            return this.arrayList.size();
        }

        @NonNull
        public android.widget.Filter getFilter() {
            return new filterClass(this);
        }

        public /* synthetic */ Object getItem(int i) {
            return m13046a(i);
        }

        class filterClass extends android.widget.Filter {
            Filter filter;

            filterClass(Filter filter) {
                this.filter = filter;
            }

            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    this.filter.arrayList = this.filter.drawPath.getOptions(charSequence.toString());
                    filterResults.values = this.filter.arrayList;
                    filterResults.count = this.filter.arrayList.size();
                }
                return filterResults;
            }

            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null || filterResults.count <= 0) {
                    this.filter.notifyDataSetInvalidated();
                } else {
                    this.filter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * A class to download data from Google Directions URL
     */
    @SuppressLint("StaticFieldLeak")
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
    @SuppressLint("StaticFieldLeak")
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
                            lineOptions.color(Color.GREEN);
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