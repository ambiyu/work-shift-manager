package shiftman.server;

public class Shift extends TimePeriod {
    private Employee _manager;
    private int _numWorkers;
    private int _minWorkers;

    public Shift(String dayOfWeek, String startTime, String endTime) throws ShiftManException {
        super(dayOfWeek, startTime, endTime);
    }

    public Shift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) throws ShiftManException {
        super(dayOfWeek, startTime, endTime);
        _minWorkers = Integer.parseInt(minimumWorkers);
    }

    public void plusWorker() {
        _numWorkers++;
    }

    public boolean isUnderstaffed() {
        return _numWorkers < _minWorkers;
    }

    public boolean isOverstaffed() {
        return _numWorkers > _minWorkers;
    }

    public void setManager(Employee manager) {
        _manager = manager;
    }

    public Employee getManager() {
        return _manager;
    }

    public boolean hasWorkers() {
        return _numWorkers != 0;
    }

    public String getDay() {
        return super.getDay();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)  {
            return true;
        } else if (obj == null) {
            return false;
        }
        if (obj instanceof Shift) {
            Shift other = (Shift)obj;
            return super.equals(other);
        }
        return false;
    }
}
