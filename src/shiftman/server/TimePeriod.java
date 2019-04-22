package shiftman.server;

import java.util.Comparator;

public class TimePeriod {
    private DayOfWeek _dayOfWeek;
    private Time _startTime;
    private Time _endTime;

    public TimePeriod(String dayOfWeek, String startTime, String endTime) {
        // Check if dayOfWeek is a valid day
        try{
            _dayOfWeek = DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("ERROR: Day given (" + dayOfWeek + ") is invalid.", e);
        }

        _startTime = new Time(startTime);
        _endTime = new Time(endTime);

        if (_endTime.isBefore(_startTime)) {
            throw new IllegalArgumentException("ERROR: Start time is after the end time.");
        }
        if (_startTime.equals(_endTime)) {
            throw new IllegalArgumentException("ERROR: Start time cannot be the same as the end time");
        }
    }

    public DayOfWeek getDay() {
        return _dayOfWeek;
    }

    /**
     * Gets the time period without the day as a string. eg. "08:00-12:00"
     */
    public String getTimePeriod() {
        return _startTime + "-" + _endTime;
    }

    /**
     * Determines if the two time periods overlap with each other
     * @param other the other time period to be checked against
     * @return true if the time periods overlap, otherwise false
     */
    public boolean overlaps(TimePeriod other) {
        if (_dayOfWeek.equals(other._dayOfWeek)) {
            return _startTime.isBefore(other._endTime) && other._startTime.isBefore(_endTime);
        }
        return false;
    }

    /**
     * Checks if this time period is within the other time period
     * @param other the other time period that is to be
     * @return true if this time period is within the other, otherwise false
     */
    public boolean isWithin(TimePeriod other) {
        if (_dayOfWeek.equals(other._dayOfWeek)) {
            return (other._startTime.isBefore(_startTime) || _startTime.equals(other._startTime)) &&
                    (_endTime.isBefore(other._endTime) || _endTime.equals(other._endTime));
        }
        return false;
    }

    @Override
    public String toString() {
        return _dayOfWeek + "[" + _startTime + "-" + _endTime + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimePeriod) {
            TimePeriod other = (TimePeriod) obj;
            return _dayOfWeek.equals(other._dayOfWeek) && _startTime.toString().equals(other._startTime.toString()) &&
                    _endTime.toString().equals(other._endTime.toString());
        }
        return false;
    }

    /**
     * Comparator class to compare time periods, allowing time periods to be sorted.
     */
    public static class TimeComparator implements Comparator<TimePeriod> {
        /**
         * Implementing the compare method for the comparator.
         * The time periods should be sorted in chronological order, first by the day of the week, and then by start time.
         * @return -1 if first time period is before the second
         *         0 if both time periods are the same
         *         1 if first time period is after the second
         */
        public int compare(TimePeriod o1, TimePeriod o2) {
            DayOfWeek day1 = o1.getDay();
            DayOfWeek day2 = o2.getDay();

            // compare the order of the days, with regards to the order of the DayOfWeek enum. ie. (Monday-Sunday)
            if (day1.ordinal() > day2.ordinal()) {
                return 1;
            } else if (day1.ordinal() < day2.ordinal()) {
                return -1;
            }
            // if same day, then compare start times
            if (o1._startTime.isBefore(o2._startTime)) {
                return -1;
            } else if (o2._startTime.isBefore(o1._startTime)) {
                return 1;
            } else return 0;
        }
    }
}
