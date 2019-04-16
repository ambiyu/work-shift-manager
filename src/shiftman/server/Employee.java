package shiftman.server;

public class Employee implements Comparable<Employee> {
    private String _givenName;
    private String _familyName;

    public Employee(String givenName, String familyName) {
        _givenName = givenName;
        _familyName = familyName;
    }

    public String getGivenName() {
        return _givenName;
    }

    public String getFamilyName() {
        return _familyName;
    }

    @Override
    public int compareTo(Employee employee) {
        return _familyName.compareTo(employee._familyName);
    }

    @Override
    public String toString() {
        return _givenName + " " + _familyName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee) {
            Employee other = (Employee)obj;
            return _familyName.equalsIgnoreCase(other._familyName) && _givenName.equalsIgnoreCase(other._givenName);
        }
        return false;
    }
}
