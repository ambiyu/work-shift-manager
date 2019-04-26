package shiftman.server;

import java.util.List;

public class Shift extends TimePeriod {
    private Employee _manager;
    private EmployeeRepository _workers;
    private int _minWorkers;

    /**
     * Creates a new shift with the given parameters. Calls the parent class to set the time period of the shift
     */
    public Shift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        super(dayOfWeek, startTime, endTime);
        _workers = new EmployeeRepository();
        _minWorkers = Integer.parseInt(minimumWorkers);
    }

    /**
     * Used to create new shift objects for validation purposes
     */
    public Shift(String dayOfWeek, String startTime, String endTime) {
        super(dayOfWeek, startTime, endTime);
        _workers = new EmployeeRepository();
    }

    public void setManager(Employee manager) {
        _manager = manager;
    }

    public Employee getManager() {
        return _manager;
    }

    public void assignWorker(Employee worker) {
        _workers.add(worker);
    }

    public boolean hasWorkers() {
        return _workers.size() != 0;
    }

    public List<Employee> getWorkers() {
        return _workers.getAllValues();
    }

    /**
     * Finds out how many workers are needed to meet the minimum workers requirement for the shift.
     * Used to check if the shift is overstaffed or understaffed
     * @return the number of workers needed.
     *         Positive number means the shift is understaffed
     *         Negative number means the shift is overstaffed
     */
    public int workersNeeded() {
        return _minWorkers - _workers.size();
    }
}
