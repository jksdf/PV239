package ly.betime.shuriken.persistance;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ly.betime.shuriken.entities.Alarm;

@Database(entities = {Alarm.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDAO alarmDAO();
}