package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ShiftManServer implements ShiftMan {
    Shop shop;

    public String newRoster(String shopName) {
        if (shopName.isEmpty() || shopName == null) {
            return "ERROR: Cannot create a new roster due to invalid shop name.";
        }
        shop = new Shop(shopName);
        return "";
    }

    public String setWorkingHours(String dayOfWeek, String startTime, String endTime) {
        if (shop == null) {
            return "ERROR: no roster has been created";
        }

        try {
            TimePeriod workingHours = new TimePeriod(dayOfWeek, startTime, endTime);
            shop.setWorkingHours(workingHours);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String addShift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        if (shop == null) {
            return "ERROR: no roster has been created";
        }

        try {
            Shift shift = new Shift(dayOfWeek, startTime, endTime, minimumWorkers);
            shop.addShift(shift);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String registerStaff(String givenName, String familyName) {
        if (shop == null) {
            return "ERROR: no roster has been created";
        }

        if (givenName == null || familyName == null || givenName.isEmpty() || familyName.isEmpty()) {
            return "ERROR: Invalid name given.";
        }

        try {
            Employee employee = new Employee(givenName, familyName);
            shop.addEmployee(employee); // ShiftManException is thrown if staff is already registered
            return "";
        } catch (ShiftManException e) {
            return e.getMessage();
        }
    }

    public String assignStaff(String dayOfWeek, String startTime, String endTime, String givenName, String familyName, boolean isManager) {
        if (shop == null) {
            return "ERROR: no roster has been created";
        }

        String fullName = givenName + " " + familyName;
        Employee employee = shop.getEmployee(fullName);
        if (employee == null) {
            return "ERROR: \"" + fullName + "\" is not registered.";
        }

        try {
            Shift shift = shop.getShift(dayOfWeek, startTime, endTime); // may throw IllegalArgumentException if invalid inputs
            if (shift == null) {
                return "ERROR: Shift given does not exist";
            }
            shop.assignStaff(shift, employee, isManager);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public List<String> getRegisteredStaff() {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        return shop.getStaffList(true);
    }

    public List<String> getUnassignedStaff() {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        return shop.getStaffList(false);
    }

    public List<String> shiftsWithoutManagers() {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        return shop.getShiftList(true, false, false);
    }

    public List<String> understaffedShifts() {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        return shop.getShiftList(false, true, false);
    }

    public List<String> overstaffedShifts() {
        if (shop == null) {
            listError("ERROR: no roster has been created");
        }

        return shop.getShiftList(false, false, true);
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        if (!DayOfWeek.isValidDay(dayOfWeek)) {
            return listError("ERROR: Day given (" + dayOfWeek + ") is invalid.");
        }

        return shop.getRosterForDay(dayOfWeek);
    }

    public List<String> getRosterForWorker(String workerName) {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        Employee worker = shop.getEmployee(workerName);
        if (worker == null) {
            return listError("ERROR: \"" + workerName + "\" is not registered.");
        }

        return shop.getShiftsForEmployee(worker, false);
    }

    public List<String> getShiftsManagedBy(String managerName) {
        if (shop == null) {
            return listError("ERROR: no roster has been created");
        }

        Employee manager = shop.getEmployee(managerName);
        if (manager == null) {
            listError("ERROR: \"" + managerName + "\" is not registered.");
        }

        return shop.getShiftsForEmployee(manager, true);
    }

    public String reportRosterIssues() {
        return "";
    }

    public String displayRoster() {
        StringBuilder sb = new StringBuilder();
        for (DayOfWeek day : DayOfWeek.values()) {
            List<String> roster = getRosterForDay(day.toString());
            sb.append(day.toString() + "\n");

            if (roster.size() == 0 || shop.getWorkingHours(day.toString()) == null) {
                sb.append("\t(no shifts) \n");
            }
            for (int i = 0; i < roster.size(); i++) {
                if (i != 0) {
                    sb.append("\t" + roster.get(i) + "\n");
                }
            }
        }
        return sb.toString();
    }

    private List<String> listError(String message) {
        return Collections.singletonList(message);
    }
}
