package shiftman.server;

import java.util.Comparator;

public class TimePeriod {
    public enum WeekDays {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    }

    private String _dayOfWeek;
    private Time _startTime;
    private Time _endTime;

    public TimePeriod(String dayOfWeek, String startTime, String endTime) throws ShiftManException {
        _dayOfWeek = dayOfWeek;
        _startTime = new Time(startTime);
        _endTime = new Time(endTime);

        if (_endTime.isBefore(_startTime)) {
            throw new ShiftManException("ERROR: Start time is after end time.");
        }
        if (_startTime.equals(_endTime)) {
            throw new ShiftManException("ERROR: Start time is the same as the end time");
        }

        if (!isValidDay(dayOfWeek)) {
            throw new ShiftManException("ERROR: Day given (" + dayOfWeek + ") is invalid.");
        }
    }

    public String getDay() {
        return _dayOfWeek;
    }

    public String getTimePeriod() {
        return _startTime + "-" + _endTime;
    }

    public boolean overlaps(TimePeriod other) {
        if (_dayOfWeek.equals(other._dayOfWeek)) {
            return _startTime.isBefore(other._endTime) && other._startTime.isBefore(_endTime);
        }
        return false;
    }

    public boolean isWithin(TimePeriod other) {
        if (_dayOfWeek.equals(other._dayOfWeek)) {
            return (other._startTime.isBefore(_startTime) || _startTime.equals(other._startTime)) &&
                    (_endTime.isBefore(other._endTime) || _endTime.equals(other._endTime));
        }
        return false;
    }

    public static boolean isValidDay(String dayOfWeek) {
        for (WeekDays day : WeekDays.values()) {
            if (dayOfWeek.equals(day.toString())) {
                return true;
            }
        }
        return false;
    }

    public WeekDays getEnumDay() {
        return WeekDays.valueOf(_dayOfWeek);
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

    public static class TimeComparator implements Comparator<TimePeriod> {
        public int compare(TimePeriod o1, TimePeriod o2) {
            if (o1.getEnumDay().ordinal() > o2.getEnumDay().ordinal()) {
                return 1;
            } else if (o1.getEnumDay().ordinal() < o2.getEnumDay().ordinal()) {
                return -1;
            }
            // same day at this point
            if (o1._startTime.isBefore(o2._startTime)) {
                return -1;
            } else if (o2._startTime.isBefore(o1._startTime)) {
                return 1;
            } else return 0;
        }
    }
}
