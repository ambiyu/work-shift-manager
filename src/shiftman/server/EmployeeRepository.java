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

    public List<Employee> getAllValues() {
        Collections.sort(_employees);
        return Collections.unmodifiableList(new ArrayList<>(_employees));
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
}
