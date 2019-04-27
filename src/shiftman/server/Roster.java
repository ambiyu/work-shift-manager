package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Roster {
    private final String _shopName;
    private final List<TimePeriod> _workingHours;
    private final ShiftRepository _shifts;
    private final EmployeeRepository _staff;
    private final EmployeeRepository _assignedStaff; // repository of staff assigned to a shift (as manager or worker)

    public Roster(String shopName) {
        _shopName = shopName;
        _workingHours = new ArrayList<>();
        _shifts = new ShiftRepository();
        _staff = new EmployeeRepository();
        _assignedStaff = new EmployeeRepository();
    }

    public void addEmployee(Employee employee) throws ShiftManException {
        if (_staff.contains(employee)) {
            throw new ShiftManException("ERROR: \"" + employee + "\" is already registered");
        }
        _staff.add(employee);
    }

    public void addShift(Shift shift) throws ShiftManException {
        if (_shifts.contains(shift)) {
            throw new ShiftManException("ERROR: Shift \"" + shift + "\" already exists");
        }

        for (Shift existingShift : _shifts.getShiftsForDay(shift.getDay())) {
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

    public void setWorkingHours(TimePeriod workday) throws ShiftManException {
        for (TimePeriod workingHours : _workingHours) {
            if (workday.getDay().equals(workingHours.getDay())) {
                throw new ShiftManException("ERROR: Working hours already set for " + workday.getDay());
            }
        }
        _workingHours.add(workday);
    }

    /**
     * Gets the working hours for the given day as a string. eg. 08:00-15:00
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
     * Gets the shift with the given period. Creates a temporary TimePeriod object for checking purposes
     * @return the shift object. If no shift with the given parameters exist, then return null.
     */
    public Shift getShiftByPeriod(String dayOfWeek, String startTime, String endTime) {
        TimePeriod period = new TimePeriod(dayOfWeek, startTime, endTime);
        return _shifts.getShiftByPeriod(period);
    }

    public Employee getEmployeeByName(String fullName) {
        return _staff.getEmployeeByName(fullName);
    }

    public List<String> getRegisteredStaff() {
        return _staff.getAllStaff();
    }

    public List<String> getUnassignedStaff() {
        return _staff.getUnassignedStaff(_assignedStaff);
    }

    public List<String> getShiftList(boolean noManager, boolean understaffed, boolean overstaffed) {
        return _shifts.getShiftList(noManager, understaffed, overstaffed);
    }

    public List<String> getShiftsForEmployee(Employee employee, boolean asManager) {
        return _shifts.getShiftsForEmployee(employee, asManager);
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        String workingHours = getWorkingHours(dayOfWeek);
        if (workingHours == null) { // return empty list if no roster/working hours not set for that day
            return Collections.emptyList();
        }

        List<String> roster = _shifts.getRosterForDay(DayOfWeek.valueOf(dayOfWeek));
        if (!roster.isEmpty()) {
            roster.add(0, _shopName);
            roster.add(1, dayOfWeek + " " + workingHours);
        }
        return roster;
    }
}
