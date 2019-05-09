package ly.betime.shuriken.service;

import ly.betime.shuriken.apis.CalendarEvent;

interface EventPreparationEstimator {
    /**
     * Estimates the time needed to prepare for the calendar event.
     * @param event the calendar event
     * @return time to prepare in milliseconds
     */
    @SuppressWarnings("unused")
    long timeToPrep(CalendarEvent event);
}
