package shiftman.server;

import java.util.*;

public class Shop {

    private String _shopName;
    private List<TimePeriod> _workingHours;
    private EmployeeRepository _staff;
    private ShiftRepository _shifts;
    /**
     * Map of assigned shifts, where the key is the employee and the value is a list of shifts the employee is working in or managing
     */
    private Map<Employee, ShiftRepository> _assignedShifts;


    public Shop(String shopName) {
        _shopName = shopName;
        _staff = new EmployeeRepository();
        _shifts = new ShiftRepository();
        _assignedShifts = new HashMap<>();
        _workingHours = new ArrayList<>();
    }

    public void addEmployee(Employee employee) throws ShiftManException {
        if (_staff.contains(employee)) {
            throw new ShiftManException("ERROR: \"" + employee + "\" is already registered");
        }
        _staff.add(employee);
    }

    public void addShift(Shift shift) {
        _shifts.add(shift);
    }

    public void assignStaff(Shift shift, Employee employee) {
        // if employee is not already assigned to a shift
        if (!_assignedShifts.containsKey(employee)) {
            _assignedShifts.put(employee, new ShiftRepository(shift));
            shift.plusWorker();
            return;
        }

        ShiftRepository shiftList = _assignedShifts.get(employee);
        shiftList.add(shift);
        shift.plusWorker();
    }

    public void addWorkingHours(TimePeriod workday) {
        // Check if the day of week of this work day is already added. If so, then replace it.
        for (TimePeriod workingHours : _workingHours) {
            if (workday.getDay().equals(workingHours)) {
                _workingHours.remove(workingHours);
                break;
            }
        }
        _workingHours.add(workday);
    }

    public void validateShift(Shift shiftToCheck) throws ShiftManException {
        for (Shift shift : _shifts) {
            if (shiftToCheck.overlaps(shift)) {
                throw new ShiftManException("ERROR: Given shift overlaps with an already existing shift");
            }
        }

        for (TimePeriod workday : _workingHours) {
            if (shiftToCheck.getDay().equals(workday.getDay()) && !shiftToCheck.overlaps(workday)) {
                throw new ShiftManException("ERROR: Given shift is not within the working hours");
            }
        }
    }

    public String getWorkingHours(String dayOfWeek) {
        for (TimePeriod day : _workingHours) {
            if (day.getDay().equals(dayOfWeek)) {
                return day.getTimePeriod();
            }
        }
        return null;
    }

    public Shift getShift(String dayOfWeek, String startTime, String endTime) throws ShiftManException {
        Shift shiftToCheck = new Shift(dayOfWeek, startTime, endTime);
        for (Shift shift : _shifts) {
            if (shiftToCheck.equals(shift)) {
                return shift;
            }
        }
        throw new ShiftManException("ERROR: Shift given (\"" + shiftToCheck + "\") does not exist");
    }

    public Employee getEmployee(String fullName) throws ShiftManException{
        for (Employee employee : _staff) {
            if (fullName.equals(employee.toString())) {
                return employee;
            }
        }
        throw new ShiftManException("ERROR: \"" + fullName + "\" is not registered.");
    }

    public EmployeeRepository getEmployeeRepository() {
        return _staff;
    }

    public ShiftRepository getShiftRepository() {
        _shifts.sort();
        return _shifts;
    }

    public Map<Employee, ShiftRepository> getAssignedShifts() {
        return _assignedShifts;
    }



    @Override
    public String toString() {
        return _shopName;
    }
}
