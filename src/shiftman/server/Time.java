package shiftman.server;

public class Time {
    private int _hour;
    private int _minute;

    public Time(String time) throws ShiftManException {
        if (!validFormat(time)) {
            throw new ShiftManException("ERROR: Start/end time format is invalid");
        }
        _hour = Integer.parseInt(time.substring(0, 2));
        _minute = Integer.parseInt(time.substring(3, 5));

        if (_hour > 23 || _hour < 0 || _minute > 59 || _minute < 0) {
            throw new ShiftManException("ERROR: Start/end time is invalid");
        }
    }

    private int getTotalMins() {
        return _hour * 60 + _minute;
    }

    public boolean isBefore(Time other) {
        return getTotalMins() < other.getTotalMins();
    }

    private boolean validFormat(String time) {
        return time.matches("\\d{2}:\\d{2}");
    }

    @Override
    public String toString() { // Not sure if needed due to Shift and OpeningHours class
        return String.format("%02d:%02d", _hour, _minute);
    }
}