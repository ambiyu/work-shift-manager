package shiftman.server;

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

    /**
     * Assigns a worker to the shift
     * @param worker the worker to be assigned
     */
    public void assignWorker(Employee worker) {
        _workers.add(worker);
    }

    /**
     * Returns the workers assigned to the shift sorted in alphabetical order
     */
    public EmployeeRepository getWorkers() {
        _workers.sort();
        return _workers;
    }

    /**
     * Determines whether the shift is overstaffed or understaffed
     * @return 1 if there are more workers than the minimum workers required
     *         -1 if there are less workers than required
     *         0 if the number of workers equal the minimum workers required
     */
    public int workerSituation() {
        if (_workers.size() > _minWorkers) {
            return 1;
        } else if (_workers.size() < _minWorkers) {
            return -1;
        } else return 0;
    }

    public void setManager(Employee manager) {
        _manager = manager;
    }

    public Employee getManager() {
        return _manager;
    }

    public boolean hasWorkers() {
        return _workers.size() != 0;
    }
}
