package ly.betime.shuriken;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(getApplicationContext());
    }
}
