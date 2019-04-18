package shiftman.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class ShiftRepository implements Repository<Shift> {

    private List<Shift> _shifts;

    public ShiftRepository() {
        _shifts = new ArrayList<>();
    }

    public ShiftRepository(Shift shift) {
        _shifts = new ArrayList<>();
        _shifts.add(shift);
    }

    public void add(Shift shift) {
        _shifts.add(shift);
    }

    public boolean contains(Shift shiftToCheck) {
        for (Shift shift : _shifts) {
            if (shift.equals(shiftToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts the shifts in chronological order
     */
    public void sort() {
        Collections.sort(_shifts, new TimePeriod.TimeComparator());
    }

    public Iterator<Shift> iterator() {
        return _shifts.iterator();
    }

}