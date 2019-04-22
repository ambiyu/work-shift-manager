package shiftman.server;

import java.util.Comparator;

public class TimePeriod {
    private DayOfWeek _dayOfWeek;
    private Time _startTime;
    private Time _endTime;

    public TimePeriod(String dayOfWeek, String startTime, String endTime) {
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
            DayOfWeek day1 = o1.getDay();
            DayOfWeek day2 = o2.getDay();

            if (day1.ordinal() > day2.ordinal()) {
                return 1;
            } else if (day1.ordinal() < day2.ordinal()) {
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
