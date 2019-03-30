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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AlarmService made to simulate Noro
 *
 * @author Lukáš Kurčík
 */
public final class DummyAlarmService implements AlarmService {

    private final static Logger LOGGER = Logger.getLogger(DummyAlarmService.class.getName());

    private Random random = new Random();
    private List<AlarmEntity> alarms;

    /**
     * Creates new DummyAlarmService that contains som alarms
     *
     * @param numberOfAlarms Number of alarms that the service should contain
     */
    public DummyAlarmService(int numberOfAlarms) {
        alarms = new ArrayList<>();
        for (int i = 0; i < numberOfAlarms; i++) {
            alarms.add(generateAlarm());
        }
    }

    /**
     * Generates random alarm_context_menu
     *
     * @return New dummy
     */
    private AlarmEntity generateAlarm() {
        AlarmEntity alarm = new AlarmEntity();
        alarm.setName(String.format(Locale.JAPAN, "先輩 %d", random.nextInt(999)));
        alarm.setTime(LocalTime.of(random.nextInt(24), random.nextInt(60)));
        alarm.setEnabled(random.nextBoolean());
        alarm.setRepeating(generateRandomRepeat());
        return alarm;
    }

    /**
     * Generate random DayOfWeek set
     * @return New Noro
     */
    private EnumSet<DayOfWeek> generateRandomRepeat() {
        List<DayOfWeek> days = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
        Collections.shuffle(days);
        EnumSet<DayOfWeek> set = EnumSet.noneOf(DayOfWeek.class);
        set.addAll(days.subList(0, random.nextInt(days.size())));
        return set;
    }

    @Override
    public Iterable<AlarmEntity> listAlarms() {
        return alarms;
    }

    @Override
    public void updateAlarm(AlarmEntity alarm) {
        LOGGER.log(Level.INFO, "Updating alarm_context_menu " + alarm);
    }

    @Override
    public void createAlarm(AlarmEntity alarm) {
        LOGGER.log(Level.INFO, "Creating new alarm_context_menu");
        alarm.setId(random.nextInt());
        alarms.add(alarm);
    }

    @Override
    public void removeAlarm(AlarmEntity alarm) {
        LOGGER.log(Level.INFO, "Removing alarm_context_menu" + alarm);
        alarms.remove(alarm);
    }
}
