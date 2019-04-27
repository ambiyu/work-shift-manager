package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class EmployeeRepository implements Repository<Employee> {
    private final List<Employee> _employees;

    public EmployeeRepository() {
        _employees = new ArrayList<>();
    }

    public void add(Employee employee) {
        _employees.add(employee);
    }

    public boolean contains(Employee person) {
        return _employees.contains(person);
    }

    public List<String> getAllStaff() {
        List<String> staff = new ArrayList<>(_employees.size());
        Collections.sort(_employees);

        for (Employee employee : _employees) {
            staff.add(employee.toString());
        }
        return staff;
    }

    public int size() {
        return _employees.size();
    }

    /**
     * Gets the registered employee given the full name
     * @param fullName the full name of the employee in the format: "givenName familyName"
     * @return the employee. If no employee with that name is registered, then return null.
     */
    public Employee getEmployeeByName(String fullName) {
        for (Employee employee : _employees) {
            if (fullName.equalsIgnoreCase(employee.toString())) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Gets either all the registered staff or staff not assigned to any shifts, as a list of strings.
     * @param assignedStaff a list of staff assigned to a shift
     * @return a list of unassigned staff or all registered staff
     */
    public List<String> getUnassignedStaff(EmployeeRepository assignedStaff) {
        List<String> staff = new ArrayList<>();
        Collections.sort(_employees);

        for (Employee employee : _employees) {
            if (!assignedStaff.contains(employee)) {
                staff.add(employee.toString());
            }
        }
        return staff;
    }
}
