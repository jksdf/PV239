package ly.betime.shuriken.apis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionObtainer {
    private static final String[] PERMISSIONS =
            new String[]{
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    public static boolean obtain(Activity activity) {
        if (!allEnabled(activity)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, 42);
            return false;
        }
        return true;
    }

    private static boolean allEnabled(Context context) {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(context, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
