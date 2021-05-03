package com.oman.sayakil.utils;
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
    public static final int UPDATE_INTERVAL = 2 * 1000;
    public static final int FASTEST_INTERVAL = 1000;


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
