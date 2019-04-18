package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import shiftman.server.TimePeriod.WeekDays;

public class ShiftManServer implements ShiftMan{
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
            TimePeriod workday = new TimePeriod(dayOfWeek, startTime, endTime);
            shop.addWorkingHours(workday);
            return "";
        } catch (ShiftManException e) {
            return e.getMessage();
        }
    }

    public String addShift(String dayOfWeek, String startTime, String endTime, String minimumWorkers) {
        try {
            Shift shift = new Shift(dayOfWeek, startTime, endTime, minimumWorkers);
            shop.validateShift(shift); // throws ShiftManException if invalid
            shop.addShift(shift);
            return "";
        } catch (ShiftManException e) {
            return e.getMessage();
        }
    }

    public String registerStaff(String givenName, String familyName) {
        if (givenName == null || familyName == null || givenName.isEmpty() || familyName.isEmpty()) {
            return "ERROR: Invalid name given.";
        }

        try {
            Employee employee = new Employee(givenName, familyName);
            shop.addEmployee(employee);
            return "";
        } catch (ShiftManException e) { // Exception is thrown if staff is already registered
            return e.getMessage();
        }
    }

    public String assignStaff(String dayOfWeek, String startTime, String endTime, String givenName, String familyName, boolean isManager) {
        try {
            Shift shift = shop.getShift(dayOfWeek, startTime, endTime);
            Employee employee = shop.getEmployee(givenName + " " + familyName);
            if (isManager) {
                if (shift.getManager() == null) {
                    shift.setManager(employee);
                } else return "ERROR: There is already a manager assigned to this shift";
            } else shift.plusWorkerCount(); // increase worker count if not manager
            shop.assignStaff(shift, employee);
            return "";
        } catch (ShiftManException e) {
            return e.getMessage();
        }
    }

    public List<String> getRegisteredStaff() {
        List<String> registeredStaff = shop.getEmployeeRepository().getStaffStr();
        if (registeredStaff.isEmpty()) {
            registeredStaff.add("ERROR: There are no registered staff.");
        }
        return registeredStaff;
    }

    public List<String> getUnassignedStaff() {
        List<String> unassignedStaff = new ArrayList<>();
        Map<Employee, ShiftRepository> assignedShifts = shop.getAssignedShifts();

        for (Employee employee : shop.getEmployeeRepository()) {
            if (!assignedShifts.containsKey(employee)) {
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
            if (shift.isUnderstaffed()) {
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
            if (shift.isOverstaffed()) {
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

        if (!TimePeriod.isValidDay(dayOfWeek)) {
            output.add("ERROR: Day given (" + dayOfWeek + ") is invalid.");
            return output;
        }

        String hours = shop.getWorkingHours(dayOfWeek);
        if (hours == null) {
            output.add("ERROR: Working hours not set for " + dayOfWeek);
            return output;
        }

        Map<Employee, ShiftRepository> assignedShifts = shop.getAssignedShifts();
        List<String> workers = new ArrayList<>();
        List<Employee> employeeList = new ArrayList<>(assignedShifts.keySet()); // List of assigned employees
        Collections.sort(employeeList);

        for (Shift shift : shop.getShiftRepository()) {
            workers.clear();
            if (dayOfWeek.equals(shift.getDay())) {
                String managerName;
                Employee manager = shift.getManager();
                if (manager != null) {
                    managerName = manager.getFamilyName() + ", " + manager.getGivenName();
                } else managerName = "[No manager assigned]";

                if (!shift.hasWorkers()) {
                    output.add(shift + " Manager: " + managerName + " " + "[No workers assigned]");
                } else {
                    // find all employees working in that shift
                    for (Employee employee : employeeList) {
                        // do not include the manager in the worker list
                        if (assignedShifts.get(employee).contains(shift) && !employee.equals(shift.getManager())) {
                            workers.add(employee.toString());
                        }
                    }
                    output.add(shift + " Manager: " + manager.getFamilyName() + ", " + manager.getGivenName() + " " + workers);
                }

            }
        }

        if (output.isEmpty()) {
            return output;
        } else {
            // insert shop name and working hours at the front of the list
            output.add(0, shop.toString());
            output.add(1, dayOfWeek + " " + hours);
            return output;
        }
    }

    public List<String> getRosterForWorker(String workerName) {
        List<String> output = new ArrayList<>();
        List<String> workersShifts = new ArrayList<>();

        try {
            Employee worker = shop.getEmployee(workerName); // throws exception if worker is not registered
            ShiftRepository shiftList = shop.getAssignedShifts().get(worker);
            if (shiftList == null) { // shiftList is null if worker is not working in or managing any shifts
                return output;
            }

            shiftList.sort();
            for (Shift shift : shiftList) {
                if (shift.getManager() !=  null && !shift.getManager().equals(worker)) {
                    workersShifts.add(shift.toString());
                }
            }
            if (workersShifts.isEmpty()) {
                return output;
            }
            output.add(worker.getFamilyName() + ", " + worker.getGivenName());
            output.addAll(workersShifts);
        } catch (ShiftManException e) {
            output.add(e.getMessage());
        }
        return output;
    }

    public List<String> getShiftsManagedBy(String managerName) {
        List<String> output = new ArrayList<>();
        List<String> shifts = new ArrayList<>();

        try {
            Employee manager = shop.getEmployee(managerName);

            for (Shift shift : shop.getShiftRepository()) {
                if (shift.getManager().equals(manager)) {
                    shifts.add(shift.toString());
                }
            }
            if (shifts.isEmpty()) {
                output.add("ERROR: " + managerName + " is not managing any shifts");
                return output;
            }
            output.add(manager.getFamilyName() + ", " + manager.getGivenName());
            output.addAll(shifts);
        } catch (ShiftManException e) {
            output.add(e.getMessage());
        }
        return output;
    }

    public String reportRosterIssues() {
        return "";
    }

    public String displayRoster() {
        StringBuilder sb = new StringBuilder();
        for (WeekDays day : WeekDays.values()) {
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
