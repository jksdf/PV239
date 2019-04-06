package ly.betime.shuriken.apis;

import android.content.Context;
import android.os.Build;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ApisModule {
    private static final Random random = new Random();
//    @Provides
//    public AlarmManagerApi alarmManagerApi(AlarmManager alarmManager, @Named("application") Context context) {
//        return new AlarmManagerApi(alarmManager, context);
//    }

    @Provides
    public CalendarApi calendarApi(@Named("application") Context context, ZoneId zoneId) {
        // TODO(slivka): only for debug
        List<CalendarEvent> eventList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            eventList.add(generateEvent());
        }
        return new CalendarApiFake(eventList, zoneId);
    }

    private static CalendarEvent generateEvent() {
        CalendarEvent event = new CalendarEvent();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plus(60 + random.nextInt() % 10000, ChronoUnit.MINUTES);
        event.setFrom(start);
        event.setTo(start.plus(20 + random.nextInt() % 50, ChronoUnit.MINUTES));
        event.setEventId(random.nextLong());
        event.setName("TestEvent" + random.nextInt() % 10);
        event.setStatus(random.nextInt() % 3);
        return event;
    }
}
