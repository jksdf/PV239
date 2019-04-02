package ly.betime.shuriken.apis;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public interface CalendarApi {

    default void getPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, 42);
        }
    }

    List<CalendarEvent> getEvents(long from, long to);
}
