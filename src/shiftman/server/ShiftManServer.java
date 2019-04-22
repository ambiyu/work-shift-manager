package shiftman.server;

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
        try {
            TimePeriod workingHours = new TimePeriod(dayOfWeek, startTime, endTime);
            shop.setWorkingHours(workingHours);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String addShift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        try {
            Shift shift = new Shift(dayOfWeek, startTime, endTime, minimumWorkers);
            shop.validateShift(shift); // throws ShiftManException if invalid
            shop.addShift(shift);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String registerStaff(String givenName, String familyName) {
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
        String fullName = givenName + " " + familyName;
        Employee employee = shop.getEmployee(fullName);
        if (employee == null) {
            return "ERROR: \"" + fullName + "\" is not registered.";
        }

        try {
            Shift shift = shop.getShift(dayOfWeek, startTime, endTime);
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
        List<String> registeredStaff = new ArrayList<>();

        for (Employee employee : shop.getEmployeeRepository()) {
            registeredStaff.add(employee.toString());
        }
        if (registeredStaff.isEmpty()) {
            registeredStaff.add("ERROR: There are no registered staff.");
        }
        return registeredStaff;
    }

    public List<String> getUnassignedStaff() {
        List<String> unassignedStaff = new ArrayList<>();
        EmployeeRepository assignedStaff = shop.getAssignedEmployees();

        for (Employee employee : shop.getEmployeeRepository()) {
            if (!assignedStaff.contains(employee)) {
                unassignedStaff.add(employee.toString());
            }
        }
        if (unassignedStaff.isEmpty()) {
            unassignedStaff.add("ERROR: There are no unassigned staff.");
        }
        return unassignedStaff;
    }

    public List<String> shiftsWithoutManagers() {
        List<String> output = new ArrayList<>();

        for (Shift shift : shop.getShiftRepository()) {
            if (shift.getManager() == null) { // manager is null if there is no manager for that shift
                output.add(shift.toString());
            }
        }

        if (output.isEmpty()) {
            output.add("ERROR: There are no shifts without a manager.");
        }
        return output;
    }

    public List<String> understaffedShifts() {
        List<String> output = new ArrayList<>();

        for (Shift shift : shop.getShiftRepository()) {
            if (shift.workerSituation() == -1) {
                output.add(shift.toString());
            }
        }

        if (output.isEmpty()) {
            output.add("ERROR: There are no understaffed shifts.");
        }
        return output;
    }

    public List<String> overstaffedShifts() {
        List<String> output = new ArrayList<>();

        for (Shift shift : shop.getShiftRepository()) {
            if (shift.workerSituation() == 1) {
                output.add(shift.toString());
            }
        }

        if (output.isEmpty()) {
            output.add("ERROR: There are no overstaffed shifts.");
        }
        return output;
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        List<String> output = new ArrayList<>();
        String managerName;

        try{
            DayOfWeek.valueOf(dayOfWeek);
        } catch (IllegalArgumentException e) {
            output.add("ERROR: Day given (" + dayOfWeek + ") is invalid.");
            return output;
        }

        String hours = shop.getWorkingHours(dayOfWeek);
        if (hours == null) {
            output.add("ERROR: Working hours not set for " + dayOfWeek);
            return output;
        }

        for (Shift shift : shop.getShiftRepository()) {
            List<String> workers = new ArrayList<>();

            if (dayOfWeek.equals(shift.getDay().toString())) {
                Employee manager = shift.getManager();
                if (manager != null) {
                    managerName = manager.getFamilyName() + ", " + manager.getGivenName();
                } else managerName = "[No manager assigned]";

                if (!shift.hasWorkers()) {
                    output.add(shift + " Manager: " + managerName + " " + "[No workers assigned]");
                } else {
                    for (Employee employee : shift.getWorkers()) {
                        workers.add(employee.toString());
                    }
                    output.add(shift + " Manager: " + managerName + " " + workers);
                }
            }
        }

        if (!output.isEmpty()) {
            // insert shop name and working hours at the front of the list
            output.add(0, shop.toString());
            output.add(1, dayOfWeek + " " + hours);
        }
        return output;
    }

    public List<String> getRosterForWorker(String workerName) {
        List<String> shifts = new ArrayList<>();
        Employee worker = shop.getEmployee(workerName);
        if (worker == null) {
            shifts.add("ERROR: \"" + workerName + "\" is not registered.");
        } else {
            shifts = shop.getShiftsForEmployee(worker, false);
        }
        return shifts;
    }

    public List<String> getShiftsManagedBy(String managerName) {
        List<String> shifts = new ArrayList<>();
        Employee manager = shop.getEmployee(managerName);
        if (manager == null) {
            shifts.add("ERROR: \"" + managerName + "\" is not registered.");
        } else {
            shifts = shop.getShiftsForEmployee(manager, true);
        }
        return shifts;
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
}
