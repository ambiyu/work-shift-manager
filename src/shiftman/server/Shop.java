package shiftman.server;

import java.util.Collections;
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
     */
    public void addEmployee(Employee employee) throws ShiftManException {
        if (_staff.contains(employee)) {
            throw new ShiftManException("ERROR: \"" + employee + "\" is already registered");
        }
        _staff.add(employee);
    }

    /**
     * Adds the specified shift if it valid
     */
    public void addShift(Shift shift) throws ShiftManException {
        if (_shifts.contains(shift)) {
            throw new ShiftManException("ERROR: Shift \"" + shift + "\" already exists");
        }

        for (Shift existingShift : _shifts) {
            if (shift.overlaps(existingShift)) {
                throw new ShiftManException("ERROR: Given shift overlaps with an existing shift");
            }
        }

        for (TimePeriod workday : _workingHours) {
            if (shift.isWithin(workday)) {
                _shifts.add(shift);
                return;
            }
        }
        throw new ShiftManException("ERROR: Given shift is not within the working hours");
    }

    /**
     * Assigns an employee to a shift as a worker or manager. The employee must not already be assigned to the shift
     * and if the employee is to be a manager, there must not already be a manager for the shift
     */
    public void assignStaff(Shift shift, Employee employee, boolean isManager) throws ShiftManException {
        if ((shift.getManager() != null && employee.equals(shift.getManager())) || shift.getWorkers().contains(employee)) {
            throw new ShiftManException("ERROR: " + employee + " is already assigned to this shift");
        }

        if (isManager) {
            if (shift.getManager() == null) {
                shift.setManager(employee);
            } else throw new ShiftManException("ERROR: A manager is already assigned to this shift") ;
        } else {
            shift.assignWorker(employee);
        }

        _assignedStaff.add(employee);
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
     * Gets the shops working hours for the given day as a string. eg. 08:00-15:00
     * @return the working hours for that day. If there is no working hours set for that day, then return null
     */
    public String getWorkingHours(String dayOfWeek) {
        for (TimePeriod day : _workingHours) {
            if (dayOfWeek.equals(day.getDay().toString())) {
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
     * Gets either all registered staff or staff unassigned to shifts as a list of strings.
     * @param allStaff true if you want to get all registered staff, false if you want to only get unassigned staff
     * @return a list of staff
     */
    public List<String> getStaffList(boolean allStaff) {
        List<String> list = new ArrayList<>();
        _staff.sort();

        for (Employee employee : _staff) {
            if (allStaff || (!allStaff && !_assignedStaff.contains(employee))) {
                list.add(employee.toString());
            }
        }
        return list;
    }

    /**
     * Gets all the shifts as a list of strings given a condition
     * @param noManager true if you want a list of shifts without a manager assigned, otherwise false
     * @param understaffed true if you want a list of understaffed shifts, otherwise false
     * @param overstaffed true if you want a list of overstaffed shifts, otherwise false
     * @return a list of shifts
     */
    public List<String> getShiftList(boolean noManager, boolean understaffed, boolean overstaffed) {
        List<String> list = new ArrayList<>();
        _shifts.sort();

        for (Shift shift : _shifts) {
            if ((noManager && shift.getManager() == null) || (understaffed && shift.workersNeeded() > 0) ||
                    (overstaffed && shift.workersNeeded() < 0)) {
                list.add(shift.toString());
            }
        }
        return list;
    }

    /**
     * Gets all the shifts assigned to the employee as a list of strings. You can either get the shifts where
     * the employee is a manager or where the employee is a worker.
     * @param employee the employee to get the shifts for
     * @param forManager true if you want to get shifts where the employee is a manager.
     *                   false if you want the shifts where the employee is a worker.
     * @return the list of shifts assigned to the employee (either as manager or worker). If there are no such shifts,
     *         then return an empty list
     */
    public List<String> getShiftsForEmployee(Employee employee, boolean forManager) {
        List<String> list = new ArrayList<>();
        _shifts.sort();

        if (forManager) { // get shifts for manager
            for (Shift shift : _shifts) {
                if (shift.getManager() == null) {
                    continue;
                } else if (shift.getManager().equals(employee)) {
                    list.add(shift.toString());
                }
            }
        } else { // get shifts for worker
            for (Shift shift : _shifts) {
                if (shift.hasWorkers() && shift.getWorkers().contains(employee)) { // if employee is working in that shift
                    list.add(shift.toString());
                }
            }
        }

        if (!list.isEmpty()) {
            list.add(0, employee.getFamilyName() + ", " + employee.getGivenName());
        }
        return list;
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        String hours = getWorkingHours(dayOfWeek);
        if (hours == null) { // return empty list if no roster/working hours not set for that day
            return Collections.emptyList();
        }

        String managerName;
        List<String> roster = new ArrayList<>();
        roster.add(0, _shopName);
        roster.add(1, dayOfWeek + " " + hours);
        _shifts.sort();

        for (Shift shift : _shifts) {
            List<String> workers = new ArrayList<>();
            if (dayOfWeek.equals(shift.getDay().toString())) {
                Employee manager = shift.getManager();
                if (manager != null) {
                    managerName = " Manager:" + manager.getFamilyName() + ", " + manager.getGivenName();
                } else {
                    managerName = " [No manager assigned]";
                }

                if (!shift.hasWorkers()) {
                    roster.add(shift + managerName + " " + "[No workers assigned]");
                } else {
                    for (Employee employee : shift.getWorkers()) {
                        workers.add(employee.toString());
                    }
                    roster.add(shift + managerName + " " + workers);
                }
            }
        }

        if (roster.size() == 2) { // if there are no such shifts then return empty list
            return Collections.emptyList();
        }
        return roster;
    }

    @Override
    public String toString() {
        return _shopName;
    }
}
