package shiftman.server;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class EmployeeRepository implements Repository<Employee> {
    private List<Employee> _employees;

    public EmployeeRepository() {
        _employees = new ArrayList<>();
    }

    public void add(Employee employee) {
        _employees.add(employee);
    }

    public boolean contains(Employee person) {
        return _employees.contains(person);
    }

    /**
     * Sorts the list of employees by their family name in alphabetical order
     */
    public void sort() {
        Collections.sort(_employees);
    }

    public Iterator<Employee> iterator() {
        return _employees.iterator();
    }

}
