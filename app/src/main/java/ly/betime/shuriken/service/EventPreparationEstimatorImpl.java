package ly.betime.shuriken.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.dagger.MyApplication;
import ly.betime.shuriken.preferences.Preferences;

public class EventPreparationEstimatorImpl implements EventPreparationEstimator {

    private final SharedPreferences sharedPreferences;
    private final Context context;
    private final ExecutorService executorService;

    @Inject
    public EventPreparationEstimatorImpl(SharedPreferences sharedPreferences, @Named("application") Context context, @MyApplication ExecutorService executorService) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.executorService = executorService;
    }

    @Override
    public ListenableFuture<Long> timeToPrep(CalendarEvent event) {
        ListenableFuture<Integer> morningFuture = estimateMorning(event);
        ListenableFuture<Integer> travelFuture = estimateTravel(event);
        return Futures.transform(
                Futures.allAsList(morningFuture, travelFuture),
                vals -> {
                    long sum = 0;
                    for (int i = 0; i < vals.size(); i++) {
                        sum += vals.get(i);
                    }
                    return 1000L * sum;
                },
                executorService);
    }

    private ListenableFuture<Integer> estimateMorning(CalendarEvent event) {
        return Futures.immediateFuture(60 * sharedPreferences.getInt(Preferences.MORNING_TIME_ESTIMATE, Preferences.MORNING_TIME_ESTIMATE_DEFAULT));
    }

    private ListenableFuture<Integer> estimateTravel(CalendarEvent event) {
        int defaultEstimate = sharedPreferences.getInt(Preferences.TRAVEL_TIME_ESTIMATE, Preferences.TRAVEL_TIME_ESTIMATE_DEFAULT);
        if (event.getLocation() == null) {
            return Futures.immediateFuture(60 * defaultEstimate);
        }
        return Futures.transform(currentLocation(), location -> {
            if (location == null) {
                return defaultEstimate;
            }
            int estimate = travelTime(location, event.getLocation());
            if (estimate == 0) {
                return 0;
            }
            return defaultEstimate > estimate ? defaultEstimate : estimate;
        }, executorService);
    }

    @SuppressLint("MissingPermission")
    private ListenableFuture<String> currentLocation() {
        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        SettableFuture<String> locationFuture = SettableFuture.create();
        locationProviderClient
                .getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        locationFuture.set(null);
                    }
                    locationFuture.set(String.format("%s+%s", location.getLatitude(), location.getLongitude()));
                });
        return locationFuture;
    }

    @SuppressWarnings("unused")
    private int travelTime(String from, String to) {
        return 10;
    }
}
