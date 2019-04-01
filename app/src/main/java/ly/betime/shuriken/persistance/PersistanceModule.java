package ly.betime.shuriken.persistance;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class PersistanceModule {
    @Provides
//    @Singleton
    public AppDatabase db(@Named("application") Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "db").allowMainThreadQueries().build();
    }
}
