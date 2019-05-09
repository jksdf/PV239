package ly.betime.shuriken.service;

import android.content.SharedPreferences;

import javax.inject.Inject;

import ly.betime.shuriken.apis.CalendarEvent;
import ly.betime.shuriken.preferences.Preferences;

public class EventPreparationEstimatorImpl implements EventPreparationEstimator {

    private final SharedPreferences sharedPreferences;

    @Inject
    public EventPreparationEstimatorImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    @SuppressWarnings("unused")
    public long timeToPrep(CalendarEvent event) {
        long prepTime = 0L; // in seconds
        return 1000L * prepTime;
    }

    @SuppressWarnings("unused")
    private long estimateMorning(CalendarEvent event) {
        return 60 * sharedPreferences.getInt(Preferences.MORNING_TIME_ESTIMATE, Preferences.MORNING_TIME_ESTIMATE_DEFAULT);
    }

    private long estimateTravel(CalendarEvent event) {
        int defaultEstimate = sharedPreferences.getInt(Preferences.TRAVEL_TIME_ESTIMATE, Preferences.TRAVEL_TIME_ESTIMATE_DEFAULT);
        if (event.getLocation() == null) {
            return 60 * defaultEstimate;
        }
        long estimate = travelTime(currentLocation(), event.getLocation());
        if (estimate == 0) {
            return 0;
        }
        return defaultEstimate > estimate ? defaultEstimate : estimate;
    }

    private String currentLocation() {
        return "12.5,15.7";
    }

    @SuppressWarnings("unused")
    private long travelTime(String from, String to) {
        return 10;
    }
}
