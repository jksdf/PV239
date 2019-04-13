package ly.betime.shuriken.adapters.data;

import ly.betime.shuriken.entities.GeneratedAlarm;

public class GeneratedAlarmShuriken {
    private GeneratedAlarm alarm;
    private boolean showDate = false;

    public GeneratedAlarmShuriken(GeneratedAlarm alarm) {
        this.alarm = alarm;
    }

    public GeneratedAlarmShuriken(GeneratedAlarm alarm, boolean showDate) {
        this.alarm = alarm;
        this.showDate = showDate;
    }

    public GeneratedAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(GeneratedAlarm alarm) {
        this.alarm = alarm;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }
}
