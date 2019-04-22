package shiftman.server;

import java.util.List;
import java.util.ArrayList;

public class Shop {
    private String _shopName;
    private List<TimePeriod> _workingHours;
    private ShiftRepository _shifts;
    private EmployeeRepository _staff;
    private EmployeeRepository _assignedStaff; // repository of staff assigned to shifts as a manager or worker

    public Shop(String shopName) {
        _shopName = shopName;
        _workingHours = new ArrayList<>();
        _shifts = new ShiftRepository();
        _staff = new EmployeeRepository();
        _assignedStaff = new EmployeeRepository();
    }

    /**
     * Registers an employee to the shop. Also checks if employee is already registered
     * @param employee the employee to be registered
     * @throws ShiftManException if employee is already registered
     */
    public void addEmployee(Employee employee) throws ShiftManException {
        if (_staff.contains(employee)) {
            throw new ShiftManException("ERROR: \"" + employee + "\" is already registered");
        }
        _staff.add(employee);
    }

    /**
     * Adds the specified shift. The shift must not be added already
     * @param shift the shift to be added
     * @throws ShiftManException if shift already exists
     */
    public void addShift(Shift shift) throws ShiftManException {
        if (_shifts.contains(shift)) {
            throw new ShiftManException("ERROR: Shift \"" + shift + "\" already exists");
        }
        _shifts.add(shift);
    }

    /**
     * Assigns an employee to a shift as a worker or manager
     */
    public void assignStaff(Shift shift, Employee employee, boolean isManager) throws ShiftManException {
        _assignedStaff.add(employee);
        if (isManager) {
            if (shift.getManager() == null) {
                shift.setManager(employee);
            } else throw new ShiftManException("ERROR: A manager is already assigned to this shift") ;
        } else shift.assignWorker(employee);
    }

    /**
     * Sets the working hours for the shop
     * @param workday the working hours to be set
     * @throws ShiftManException if working hours are already set for that day
     */
    public void setWorkingHours(TimePeriod workday) throws ShiftManException {
        for (TimePeriod workingHours : _workingHours) {
            if (workday.getDay().equals(workingHours.getDay())) {
                throw new ShiftManException("ERROR: Working hours already set for " + workday.getDay());
            }
        }
        _workingHours.add(workday);
    }

    /**
     * Checks that a shift does not overlap with any existing shifts and that it is within the working hours of the shop.
     */
    public void validateShift(Shift shiftToCheck) throws ShiftManException {
        for (Shift shift : _shifts) {
            if (shiftToCheck.overlaps(shift)) {
                throw new ShiftManException("ERROR: Given shift overlaps with an already existing shift");
            }
        }

        for (TimePeriod workday : _workingHours) {
            if (shiftToCheck.isWithin(workday)) {
                return;
            }
        }
        throw new ShiftManException("ERROR: Given shift is not within the working hours");
    }

    /**
     * Gets the shops working hours for the given day as a string. eg. 08:00-15:00
     * @return the working hours for that day. If there is no working hours set for that day, then return null
     */
    public String getWorkingHours(String dayOfWeek) {
        for (TimePeriod day : _workingHours) {
            if (day.getDay().toString().equals(dayOfWeek)) {
                return day.getTimePeriod();
            }
        }
        return null;
    }

    /**
     * Gets the shift with the given parameters
     * @return the shift object. If no shift with the given parameters exist, then return null.
     */
    public Shift getShift(String dayOfWeek, String startTime, String endTime) {
        Shift shiftToCheck = new Shift(dayOfWeek, startTime, endTime);
        for (Shift shift : _shifts) {
            if (shiftToCheck.equals(shift)) {
                return shift;
            }
        }
        return null;
    }

    /**
     * Gets the registered employee given the full name
     * @param fullName the full name of the employee in the format: "givenName familyName"
     * @return the employee. If no employee with that name is registered, then return null.
     */
    public Employee getEmployee(String fullName) {
        for (Employee employee : _staff) {
            if (fullName.equalsIgnoreCase(employee.toString())) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Gets all the shifts assigned to the employee as a list of strings. You can either get all the shifts where
     * the employee is a manager or where the employee is a worker.
     * @param employee
     * @param manager true if you want to get shifts where the employee is a manager.
     *                false if you want the shift where the employee is a worker.
     * @return the list of shifts assigned to the employee (either as manager or worker). If there are no such shifts,
     *         then return an empty list
     */
    public List<String> getShiftsForEmployee(Employee employee, boolean manager) {
        List<String> shifts = new ArrayList<>();

        if (manager) { // get shifts for manager
            for (Shift shift : _shifts) {
                if (shift.getManager() == null) {
                    continue;
                } else if (shift.getManager().equals(employee)) {
                    shifts.add(shift.toString());
                }
            }
        } else { // get shifts for worker
            for (Shift shift : _shifts) {
                if (shift.hasWorkers() && shift.getWorkers().contains(employee)) { // if employee is working in that shift
                    shifts.add(shift.toString());
                }
            }
        }

        if (!shifts.isEmpty()) {
            shifts.add(0, employee.getFamilyName() + ", " + employee.getGivenName());
        }
        return shifts;
    }

    public EmployeeRepository getAssignedEmployees() {
        return _assignedStaff;
    }

    public EmployeeRepository getEmployeeRepository() {
        _staff.sort();
        return _staff;
    }

    public ShiftRepository getShiftRepository() {
        _shifts.sort();
        return _shifts;
    }

    @Override
    public String toString() {
        return _shopName;
    }
}
