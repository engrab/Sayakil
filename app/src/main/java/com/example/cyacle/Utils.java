package com.example.cyacle;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static final long LOCATION_INTERVAL = 10000;
    public static final long FASTEST_LOCATION_INTERVAL = 5000;
    public static final int UPDATE_INTERVAL = 2 * 1000;
    public static final int FASTEST_INTERVAL = 1000;
    public static String RELEASE_TOPIC_FOR_ACCOUNT = "topic_trendingapps";
    public static String DEBUG_TOPIC_FOR_ACCOUNT = "test_topic_trendingapps";
    public static String INAPPproductId ="com.trending.gps.map.navigation";

    public static String GetCustomDate(String s)
    {
        return new SimpleDateFormat(s, Locale.ENGLISH).format(Calendar.getInstance().getTime());
    }

    public static boolean isLocationEnabled(Context context)
    {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            }
            catch (Settings.SettingNotFoundException e)
            {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean isNetworkAvailable(Context context)
    {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI};
        try
        {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null)
            {
                for (int networkType : networkTypes)
                {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType)
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE)
    {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try
        {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null)
            {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                if (returnedAddress.getMaxAddressLineIndex() == 0)
                {
                    if (returnedAddress.getAddressLine(0) != null && !returnedAddress.getAddressLine(0).equals(""))
                    {
                        strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("\n");
                    }
                }
                else
                {
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++)
                    {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                }
                strAdd = strReturnedAddress.toString();
//                Log.w(TAG, "My Current loction address" + strReturnedAddress.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.w("", "My Current loction address,Canont get Address!");
        }
        return strAdd;
    }

    public static void locationNotFound(Context c)
    {
        Toast.makeText(c, "Turn on location first!", Toast.LENGTH_SHORT).show();
    }

    public static void wifiNotFound(Context c)
    {
        Toast.makeText(c, "No internet Connection!", Toast.LENGTH_SHORT).show();
    }

    public static void mapeBusy(Context context)
    {
        Toast.makeText(context, "Searching route...", Toast.LENGTH_SHORT).show();
    }

    public static void openWifi(Context c)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
        if (intent.resolveActivity(c.getPackageManager()) != null)
        {
            c.startActivity(intent);
        }
    }
}
