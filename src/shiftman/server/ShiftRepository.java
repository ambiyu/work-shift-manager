package shiftman.server;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ShiftRepository implements Repository<Shift> {
    private final List<Shift> _shifts;

    public ShiftRepository() {
        _shifts = new ArrayList<>();
    }

    public void add(Shift shift) {
        _shifts.add(shift);
    }

    public boolean contains(Shift shift) {
        return _shifts.contains(shift);
    }

    public List<Shift> getAllValues() {
        Collections.sort(_shifts, new TimePeriod.TimeComparator());
        return Collections.unmodifiableList(new ArrayList<>(_shifts));
    }

    /**
     * Gets a shift given a temporary shift object
     * @param tempShift temporary shift object with a day, start time, and end time.
     * @return the shift if an existing shift matches the temporary shift, otherwise null
     */
    public Shift getShift(Shift tempShift) {
        for (Shift shift : _shifts) {
            if (tempShift.equals(shift)) {
                return shift;
            }
        }
        return null;
    }

    /**
     * Gets a list of shifts that are on the specified day
     * @return an unmodifiable list of shifts for the day
     */
    public List<Shift> getShiftsForDay(DayOfWeek day) {
        List<Shift> shiftList = new ArrayList<>();
        for (Shift shift : _shifts) {
            if (shift.getDay().equals(day)) {
                shiftList.add(shift);
            }
        }

        Collections.sort(shiftList, new TimePeriod.TimeComparator());
        return Collections.unmodifiableList(shiftList);
    }
}