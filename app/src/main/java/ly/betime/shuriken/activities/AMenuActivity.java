package ly.betime.shuriken.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import ly.betime.shuriken.R;

public abstract class AMenuActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.alarms:
                if (!(this instanceof AlarmsActivity)) {
                    intent = new Intent(this, AlarmsActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.calendar:
                if (!(this instanceof CalendarActivity)) {
                    intent = new Intent(this, CalendarActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.settings:
                if (!(this instanceof SettingsActivity)) {
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
