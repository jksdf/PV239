package ly.betime.shuriken.service;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import ly.betime.shuriken.entities.Alarm;

public class DummyFiller {
    private static final Random random = new Random();

    public static List<Alarm> generate(int numberOfAlarms) {
        List<Alarm> alarms = new ArrayList<>();
        for (int i = 0; i < numberOfAlarms; i++) {
            alarms.add(generateAlarm());
        }
        return alarms;
    }

    private static Alarm generateAlarm() {
        Alarm alarm = new Alarm();
        alarm.setName(String.format(Locale.JAPAN, "先輩 %d", random.nextInt(999)));
        alarm.setTime(LocalTime.of(random.nextInt(24), random.nextInt(60)));
        alarm.setRepeating(generateRandomRepeat());
        return alarm;
    }

    private static EnumSet<DayOfWeek> generateRandomRepeat() {
        List<DayOfWeek> days = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
        Collections.shuffle(days);
        EnumSet<DayOfWeek> set = EnumSet.noneOf(DayOfWeek.class);
        set.addAll(days.subList(0, random.nextInt(days.size())));
        return set;
    }
}
