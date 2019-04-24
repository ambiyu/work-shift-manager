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
        if (shop == null) { return "ERROR: no roster has been created"; }
        try {
            TimePeriod workingHours = new TimePeriod(dayOfWeek, startTime, endTime);
            shop.setWorkingHours(workingHours);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String addShift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        if (shop == null) { return "ERROR: no roster has been created"; }
        try {
            Shift shift = new Shift(dayOfWeek, startTime, endTime, minimumWorkers);
            shop.addShift(shift);
            return "";
        } catch (IllegalArgumentException | ShiftManException e) {
            return e.getMessage();
        }
    }

    public String registerStaff(String givenName, String familyName) {
        if (shop == null) { return "ERROR: no roster has been created"; }
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
        if (shop == null) { return "ERROR: no roster has been created"; }
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
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            for (Employee employee : shop.getEmployeeRepository()) {
                output.add(employee.toString());
            }
        }
        return output;
    }

    public List<String> getUnassignedStaff() {
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            EmployeeRepository assignedStaff = shop.getAssignedEmployees();
            for (Employee employee : shop.getEmployeeRepository()) {
                if (!assignedStaff.contains(employee)) {
                    output.add(employee.toString());
                }
            }
        }
        return output;
    }

    public List<String> shiftsWithoutManagers() {
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            for (Shift shift : shop.getShiftRepository()) {
                if (shift.getManager() == null) { // manager is null if there is no manager for that shift
                    output.add(shift.toString());
                }
            }
        }
        return output;
    }

    public List<String> understaffedShifts() {
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            for (Shift shift : shop.getShiftRepository()) {
                if (shift.workerSituation() == -1) {
                    output.add(shift.toString());
                }
            }
        }
        return output;
    }

    public List<String> overstaffedShifts() {
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            for (Shift shift : shop.getShiftRepository()) {
                if (shift.workerSituation() == 1) {
                    output.add(shift.toString());
                }
            }
        }
        return output;
    }

    public List<String> getRosterForDay(String dayOfWeek) {
        List<String> output = new ArrayList<>();
        if (shop == null) {
            output.add("ERROR: no roster has been created");
            return output;
        }
        if (!DayOfWeek.isValidDay(dayOfWeek)) {
            output.add("ERROR: Day given (" + dayOfWeek + ") is invalid.");
            return output;
        }

        String hours = shop.getWorkingHours(dayOfWeek);
        if (hours == null) { // return empty list no roster/working hours not set for that day
            return output;
        }

        String managerName;
        for (Shift shift : shop.getShiftRepository()) {
            List<String> workers = new ArrayList<>();

            if (dayOfWeek.equals(shift.getDay().toString())) {
                Employee manager = shift.getManager();
                if (manager != null) {
                    managerName = " Manager:" + manager.getFamilyName() + ", " + manager.getGivenName();
                } else managerName = " [No manager assigned]";

                if (!shift.hasWorkers()) {
                    output.add(shift + managerName + " " + "[No workers assigned]");
                } else {
                    for (Employee employee : shift.getWorkers()) {
                        workers.add(employee.toString());
                    }
                    output.add(shift + managerName + " " + workers);
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
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            Employee worker = shop.getEmployee(workerName);
            if (worker == null) {
                output.add("ERROR: \"" + workerName + "\" is not registered.");
            } else {
                output = shop.getShiftsForEmployee(worker, false);
            }
        }
        return output;
    }

    public List<String> getShiftsManagedBy(String managerName) {
        List<String> output = new ArrayList<>();
        if (shop == null) { output.add("ERROR: no roster has been created"); }
        else {
            Employee manager = shop.getEmployee(managerName);
            if (manager == null) {
                output.add("ERROR: \"" + managerName + "\" is not registered.");
            } else {
                output = shop.getShiftsForEmployee(manager, true);
            }
        }
        return output;
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
