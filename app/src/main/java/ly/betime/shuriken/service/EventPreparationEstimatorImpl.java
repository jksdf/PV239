package ly.betime.shuriken.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.base.Strings;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import ly.betime.shuriken.R;
import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.dagger.MyApplication;
import ly.betime.shuriken.preferences.Preferences;

public class EventPreparationEstimatorImpl implements EventPreparationEstimator {

    private static final String LOG_TAG = "EventPreparationEstim";
    private final SharedPreferences sharedPreferences;
    private final Context context;
    private final ExecutorService executorService;
    private final DistanceMatrixApi distanceMatrixApi;

    private final String googleKey;

    @Inject
    public EventPreparationEstimatorImpl(SharedPreferences sharedPreferences,
                                         @Named("application") Context context,
                                         @MyApplication ExecutorService executorService,
                                         @MyApplication DistanceMatrixApi distanceMatrixApi) {
        try (InputStream stream = context.getResources().openRawResource(R.raw.google_key)) {
            googleKey = new BufferedReader(new InputStreamReader(stream)).readLine();
        } catch (IOException e) {
            throw new RuntimeException("Can not access API key. The application is corrupted.", e);
        }
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        this.executorService = executorService;
        this.distanceMatrixApi = distanceMatrixApi;
    }

    @Override
    public ListenableFuture<Long> timeToPrep(CalendarEvent event) {
        ListenableFuture<Integer> morningFuture = estimateMorning();
        ListenableFuture<Integer> travelFuture = estimateTravel(event);
        return Futures.transform(
                Futures.allAsList(morningFuture, travelFuture),
                vals -> {
                    long sum = 0;
                    for (int i = 0; i < vals.size(); i++) {
                        sum += vals.get(i);
                    }
                    return 1000L * 60 * sum;
                },
                executorService);
    }

    private ListenableFuture<Integer> estimateMorning() {
        return Futures.immediateFuture(sharedPreferences.getInt(Preferences.MORNING_TIME_ESTIMATE, Preferences.MORNING_TIME_ESTIMATE_DEFAULT));
    }

    private ListenableFuture<Integer> estimateTravel(CalendarEvent event) {
        int defaultEstimate = sharedPreferences.getInt(Preferences.TRAVEL_TIME_ESTIMATE, Preferences.TRAVEL_TIME_ESTIMATE_DEFAULT);
        if (event.getLocation() == null) {
            return Futures.immediateFuture(defaultEstimate);
        }
        return Futures.transformAsync(currentLocation(), location -> {
            if (location == null) {
                return Futures.immediateFuture(defaultEstimate);
            }
            return Futures.transform(travelTime(location, event.getLocation()), estimate -> {
                if (estimate == null) {
                    return defaultEstimate;
                }
                if (estimate == 0) {
                    return 0;
                }
                return defaultEstimate > estimate ? defaultEstimate : estimate;
            }, executorService);
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
                    locationFuture.set(String.format("%s,%s", location.getLatitude(), location.getLongitude()));
                });
        return locationFuture;
    }

    @SuppressWarnings("unused")
    private ListenableFuture<Integer> travelTime(String fromRaw, String toRaw) {
        if (Strings.isNullOrEmpty(fromRaw) || Strings.isNullOrEmpty(toRaw)) {
            return Futures.immediateFuture(null);
        }
        Single<DistanceMatrixResponse> distance = null;
        String from, to;
        try {
            from = URLEncoder.encode(fromRaw, "utf-8");
            to = URLEncoder.encode(toRaw, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        distance = distanceMatrixApi.getDistance(from, to, googleKey);
        SettableFuture<Integer> distanceFuture = SettableFuture.create();
        Disposable subscribe = distance.subscribe(
                (response) -> {
                    if (!response.getStatus().equals("OK")) {
                        Log.w(LOG_TAG, "Request '" + from + "' to '" + to + "' failed");
                        distanceFuture.setException(new RuntimeException("Bad status " + response.getStatus()));
                        return;
                    }
                    if (response.getRows().size() != 1) {
                        Log.w(LOG_TAG, "Request '" + from + "' to '" + to + "' failed");
                        distanceFuture.setException(new RuntimeException("Number of rows " + response.getRows().size()));
                        return;
                    }
                    if (response.getRows().get(0).getElements().size() != 1) {
                        Log.w(LOG_TAG, "Request '" + from + "' to '" + to + "' failed");
                        distanceFuture.setException(new RuntimeException("Number of elements " + response.getRows().get(0).getElements().size()));
                        return;
                    }
                    DistanceMatrixResponse.Element element = response.getRows().get(0).getElements().get(0);
                    if (element.getStatus().equals("ZERO_RESULTS") || element.getStatus().equals("NOT_FOUND")) {
                        Log.d(LOG_TAG, "No elements found from '" + from + "' to '" + to + "'");
                        distanceFuture.set(null);
                        return;
                    }
                    if (!element.getStatus().equals("OK")) {
                        Log.w(LOG_TAG, "Request '" + from + "' to '" + to + "' failed");
                        distanceFuture.setException(new RuntimeException("Bad status in element " + element.getStatus()));
                        return;
                    }
                    distanceFuture.set(element.getDuration().getValue());
                }, distanceFuture::setException);
        return distanceFuture;
    }
}
