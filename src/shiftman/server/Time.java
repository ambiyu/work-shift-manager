package shiftman.server;

public class Time {
    private int _hour;
    private int _minute;

    public Time(String time) {
        if (!validFormat(time)) {
            throw new IllegalArgumentException("ERROR: Start/end time format is invalid");
        }
        _hour = Integer.parseInt(time.substring(0, 2));
        _minute = Integer.parseInt(time.substring(3, 5));

        if (_hour > 23 || _hour < 0 || _minute > 59 || _minute < 0) {
            throw new IllegalArgumentException("ERROR: Start/end time is invalid");
        }
    }

    /**
     * Converts to time to minutes
     */
    private int getTotalMins() {
        return _hour * 60 + _minute;
    }

    public boolean isBefore(Time other) {
        return getTotalMins() < other.getTotalMins();
    }

    /**
     * Checks if the time is in the correct format. The correct format should be hh:mm
     */
    private boolean validFormat(String time) {
        return time.matches("\\d{2}:\\d{2}");
    }

    @Override
    public String toString() { // Not sure if needed due to Shift and OpeningHours class
        return String.format("%02d:%02d", _hour, _minute);
    }

    @Override
    public boolean equals(Object obj) {
        Time other = (Time)obj;
        return _hour == other._hour && _minute == other._minute;
    }
}