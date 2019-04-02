package ly.betime.shuriken.apis;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;

import com.google.common.collect.ImmutableList;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.Random;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ApisModule {
    private static final Random random = new Random();
    @Provides
    public AlarmManagerApi alarmManagerApi(AlarmManager alarmManager, @Named("application") Context context, @Named("AlarmActivity") Class<? extends Activity> activity) {
        return new AlarmManagerApi(alarmManager, context, activity);
    }

    @Provides
    public CalendarApi calendarApi(@Named("application") Context context) {
        // TODO(slivka): only for debug
        return new CalendarApiFake(ImmutableList.of(generateEvent(), generateEvent(), generateEvent(), generateEvent(), generateEvent()));
    }

    private static CalendarEvent generateEvent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarEvent event = new CalendarEvent();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.plus(60 + random.nextInt() % 10000, ChronoUnit.MINUTES);
            event.setFrom(start);
            event.setTo(start.plus(20 + random.nextInt() % 50, ChronoUnit.MINUTES));
            event.setEventId(random.nextLong());
            event.setName("TestEvent" + random.nextInt() % 10);
            return event;
        }
        throw new RuntimeException();
    }
}
