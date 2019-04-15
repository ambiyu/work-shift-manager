package shiftman.server;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class EmployeeRepository implements Repository<Employee>, Iterable<Employee> {
    private List<Employee> _employees;

    public EmployeeRepository() {
        _employees = new ArrayList<>();
    }

    public void add(Employee employee) {
        _employees.add(employee);
    }

    public boolean contains(Employee person) {
        for (Employee employee : _employees) {
            if (employee.equals(person)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a list of registered staff in string format with the family names sorted in alphabetical order.
     */
    public List<String> getStaffStr() {
        Collections.sort(_employees);
        List<String> staffList = new ArrayList<>();
        for (Employee person : _employees) {
            staffList.add(person.toString());
        }
        return staffList;
    }

    public Iterator<Employee> iterator() {
        return _employees.iterator();
    }

}
