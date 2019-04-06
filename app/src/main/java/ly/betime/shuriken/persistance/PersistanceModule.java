package ly.betime.shuriken.persistance;

import android.content.Context;

import javax.inject.Named;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class PersistanceModule {
    private final AppDatabase db;

    public PersistanceModule(@Named("application") Context context) {
        db = Room
                .databaseBuilder(context, AppDatabase.class, "db")
                .build();
    }

    @Provides
    public AlarmDAO alarm() {
        return db.alarmDAO();
    }
}
