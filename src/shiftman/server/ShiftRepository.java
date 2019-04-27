package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ShiftRepository implements Repository<Shift> {
    private final List<Shift> _shifts;

    public ShiftRepository() {
        _shifts = new ArrayList<>();
    }

    public void add(Shift shift) {
        _shifts.add(shift);
    }

    public boolean contains(Shift shift) {
        return _shifts.contains(shift);
    }

    private void sort() {
        Collections.sort(_shifts, new TimePeriod.TimeComparator());
    }

    /**
     * Gets a shift given a temporary shift object
     * @param tempShift temporary shift object with a day, start time, and end time.
     * @return the shift if an existing shift matches the temporary shift, otherwise null
     */
    public Shift getShift(Shift tempShift) {
        for (Shift shift : _shifts) {
            if (tempShift.equals(shift)) {
                return shift;
            }
        }
        return null;
    }

    /**
     * Gets a list of shifts that are on the specified day
     * @return an unmodifiable list of shifts for the day
     */
    public List<Shift> getShiftsForDay(DayOfWeek day) {
        List<Shift> shiftList = new ArrayList<>();
        for (Shift shift : _shifts) {
            if (shift.getDay().equals(day)) {
                shiftList.add(shift);
            }
        }

        Collections.sort(shiftList, new TimePeriod.TimeComparator());
        return Collections.unmodifiableList(shiftList);
    }

    /**
     * Gets all the shifts assigned to the employee as a list of strings. You can either get the shifts where
     * the employee is a manager or where the employee is a worker.
     * @param employee the employee to get the shifts for
     * @param asManager true if you want to get shifts where the employee is a manager.
     *                  false if you want the shifts where the employee is a worker.
     * @return the list of shifts assigned to the employee (either as manager or worker).
     */
    public List<String> getShiftsForEmployee(Employee employee, boolean asManager) {
        List<String> list = new ArrayList<>();
        sort();

        if (asManager) { // get shifts for manager
            for (Shift shift : _shifts) {
                if (shift.getManager() != null && shift.getManager().equals(employee)) {
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

    /**
     * Gets all the shifts as a list of strings either without manager, understaffed, or overstaffed
     * @param noManager true if you want a list of shifts without a manager assigned, otherwise false
     * @param understaffed true if you want a list of understaffed shifts, otherwise false
     * @param overstaffed true if you want a list of overstaffed shifts, otherwise false
     * @return a list of shifts
     */
    public List<String> getShiftList(boolean noManager, boolean understaffed, boolean overstaffed) {
        List<String> list = new ArrayList<>();
        sort();

        for (Shift shift : _shifts) {
            if ((noManager && shift.getManager() == null) || (understaffed && shift.workersNeeded() > 0) ||
                    (overstaffed && shift.workersNeeded() < 0)) {
                list.add(shift.toString());
            }
        }
        return list;
    }

    public List<String> getRosterForDay(DayOfWeek dayOfWeek) {
        List<String> roster = new ArrayList<>();
        sort();
        String managerName;

        for (Shift shift : getShiftsForDay(dayOfWeek)) {
            Employee manager = shift.getManager();
            if (manager != null) {
                managerName = " Manager:" + manager.getFamilyName() + ", " + manager.getGivenName();
            } else {
                managerName = " [No manager assigned]";
            }

            if (shift.hasWorkers()) {
                roster.add(shift + managerName + " " + shift.getWorkers().getAllStaff());
            } else {
                roster.add(shift + managerName + " " + "[No workers assigned]");
            }
        }

        return roster;
    }
}