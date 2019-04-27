package shiftman.server;

import java.util.Collections;
import java.util.List;

public class ShiftManServer implements ShiftMan {
    Roster roster;

    public String newRoster(String shopName) {
        if (shopName.isEmpty() || shopName == null) {
            return "ERROR: Cannot create a new roster due to invalid shop name.";
        }
        roster = new Roster(shopName);
        return "";
    }

    public String setWorkingHours(String dayOfWeek, String startTime, String endTime) {
        if (roster == null) {
            return "ERROR: no roster has been created";
        }

        try {
            TimePeriod workingHours = new TimePeriod(dayOfWeek, startTime, endTime);
            roster.setWorkingHours(workingHours);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String addShift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        if (roster == null) {
            return "ERROR: no roster has been created";
        }

        try {
            Shift shift = new Shift(dayOfWeek, startTime, endTime, minimumWorkers);
            roster.addShift(shift);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String registerStaff(String givenName, String familyName) {
        if (roster == null) {
            return "ERROR: no roster has been created";
        }

        if (givenName == null || familyName == null || givenName.isEmpty() || familyName.isEmpty()) {
            return "ERROR: Invalid name given.";
        }

        try {
            Employee employee = new Employee(givenName, familyName);
            roster.addEmployee(employee); // ShiftManException is thrown if staff is already registered
            return "";
        } catch (ShiftManException e) {
            return e.getMessage();
        }
    }

    public String assignStaff(String dayOfWeek, String startTime, String endTime, String givenName, String familyName, boolean isManager) {
        if (roster == null) {
            return "ERROR: no roster has been created";
        }

        String fullName = givenName + " " + familyName;
        Employee employee = roster.getEmployee(fullName);
        if (employee == null) {
            return "ERROR: \"" + fullName + "\" is not registered.";
        }

        try {
            Shift shift = roster.getShift(dayOfWeek, startTime, endTime); // may throw IllegalArgumentException if invalid inputs
            if (shift == null) {
                return "ERROR: Shift given does not exist";
            }
            roster.assignStaff(shift, employee, isManager);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public List<String> getRegisteredStaff() {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        return roster.getStaffList(true, false);
    }

    public List<String> getUnassignedStaff() {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        return roster.getStaffList(false, true);
    }

    public List<String> shiftsWithoutManagers() {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        return roster.getShiftList(true, false, false);
    }

    public List<String> understaffedShifts() {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        return roster.getShiftList(false, true, false);
    }

    public List<String> overstaffedShifts() {
        if (roster == null) {
            listError("ERROR: no roster has been created");
        }

        return roster.getShiftList(false, false, true);
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        if (!DayOfWeek.isValidDay(dayOfWeek)) {
            return listError("ERROR: Day given (" + dayOfWeek + ") is invalid.");
        }

        return roster.getRosterForDay(dayOfWeek);
    }

    public List<String> getRosterForWorker(String workerName) {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        Employee worker = roster.getEmployee(workerName);
        if (worker == null) {
            return listError("ERROR: \"" + workerName + "\" is not registered.");
        }

        return roster.getShiftsForEmployee(worker, false);
    }

    public List<String> getShiftsManagedBy(String managerName) {
        if (roster == null) {
            return listError("ERROR: no roster has been created");
        }

        Employee manager = roster.getEmployee(managerName);
        if (manager == null) {
            listError("ERROR: \"" + managerName + "\" is not registered.");
        }

        return roster.getShiftsForEmployee(manager, true);
    }

    public String reportRosterIssues() {
        return "";
    }

    public String displayRoster() {
        StringBuilder sb = new StringBuilder();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<String> list = getRosterForDay(day.toString());
            sb.append(day.toString() + "\n");

            if (list.size() == 0 || roster.getWorkingHours(day.toString()) == null) {
                sb.append("\t(no shifts) \n");
            }
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    sb.append("\t" + list.get(i) + "\n");
                }
            }
        }
        return sb.toString();
    }

    private List<String> listError(String message) {
        return Collections.singletonList(message);
    }
}
