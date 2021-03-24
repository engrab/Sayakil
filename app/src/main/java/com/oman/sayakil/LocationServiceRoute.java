package com.oman.sayakil;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.oman.sayakil.ui.activities.MainActivity;
import com.oman.sayakil.ui.bottom_fragments.MapsFragment;

import java.text.DecimalFormat;

public class LocationServiceRoute extends Service implements LocationListener
{
    public static final String CHANNEL_ID = "location_channel";
    private static final int NOTIFICATION_ID = 125;
    double speed = 0;
    MediaPlayer mediaPlayer;
    NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        startLocationUpdates();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        startLocationUpdates();
        startForeground(NOTIFICATION_ID, buildNotification("Speed detection activated.", getString(R.string.app_name)));
        return START_STICKY;
    }

    protected void stopLocationUpdates()
    {
        if (AppController.getAppInstance().getGlobalGoogleApiClient()!=null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(AppController.getAppInstance().getGlobalGoogleApiClient(),
                    this);
        }
    }

    protected void startLocationUpdates()
    {
        try
        {
            if (AppController.getAppInstance().getGlobalGoogleApiClient()!=null && AppController.getAppInstance()
                    .getGlobalLocationRequest()!=null)
            {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        AppController.getAppInstance().getGlobalGoogleApiClient(),
                        AppController.getAppInstance().getGlobalLocationRequest(), this);
            }
        }
        catch (SecurityException ignored)
        {
        }
    }

    private Notification buildNotification(String text, String title)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(false)
                .setSound(null)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setLargeIcon(artwork)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setContentTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationBuilder.setColorized(true);
        }

        return notificationBuilder.build();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        MapsFragment.speedometerText.setText(String.valueOf(new DecimalFormat("#.##").format(speed) + " km/hr"));
        if (speed > MapsFragment.maxSpeed)
        {
            notificationManager.notify(NOTIFICATION_ID, buildNotification("Running on high speed. Save your speed.", "Alert " + String.valueOf(new DecimalFormat("#.##")
                    .format(speed) + " km/hr")));
            if (mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(this, R.raw.speech);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }
        else
        {
            notificationManager.notify(NOTIFICATION_ID, buildNotification("Your speed is " + String.valueOf(new DecimalFormat("#.##")
                    .format(speed) + " km/hr"), getString(R.string.app_name)));
            stopMediaPlayer();
        }
        speed = location.getSpeed() * 18 / 5;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        stopLocationUpdates();
        stopMediaPlayer();
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
        stopLocationUpdates();
        stopMediaPlayer();
        removeNotification();
        super.onDestroy();
    }

    private void stopMediaPlayer()
    {
        if (mediaPlayer != null)
        {
            if (mediaPlayer.isPlaying())
            {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void removeNotification()
    {
        stopForeground(true);

    }

    public class LocalBinder extends Binder
    {

        public LocationServiceRoute getService()
        {
            return LocationServiceRoute.this;
        }


    }


}

