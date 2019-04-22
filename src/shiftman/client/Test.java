package shiftman.client;

import java.util.List;

import shiftman.server.ShiftMan;
import shiftman.server.ShiftManServer;

public class Test {
    private static final String[][] STAFF = {
            { "Bayta", "Darell" },
            { "Hari", "Sheldon" },
            { "Ebling", "Mis" },
            { "Dors", "Venabili" },
            { "Gaal", "Dornick" },
            { "Jon", "Snow" },
            { "Tyrion", "Lannister" }
    };

    private static final String[][] OPENING_HOURS = {
            { "Friday", "09:00", "17:00", "", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
            { "Sunday", "09:00", "17:00", "", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
            { "Tuesday", "09:00", "17:00", "", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
            { "Thursday", "09:00", "21:00", "late", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
            { "Monday", "09:00", "17:00", "", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
            { "Wednesday", "09:00", "17:00", "", STAFF[0][0], STAFF[0][1], STAFF[1][0], STAFF[1][1] },
    };

    public static void main(String[] args) {
        ShiftMan scheduler = new ShiftManServer();
        scheduler.newRoster("Test Shop");

        for (String[] staff : STAFF) {
            scheduler.registerStaff(staff[0], staff[1]);
        }

        checkStatus("Set hours: ", scheduler.setWorkingHours("Sunday", "2:00", "14:00"));
        checkStatus("setHours2 : ", scheduler.setWorkingHours("asd", "12:00", "14:00" ));

        for (String[] dayspec: OPENING_HOURS) {
            scheduler.setWorkingHours(dayspec[0], dayspec[1], dayspec[2]);
        }

        //checkStatus("Add shift: ", scheduler.addShift("Sunday", "12:00", "22:00", "3"));
        checkStatus("Add shift: ", scheduler.addShift("Sunday", "08:00", "15:00", "1"));
        //checkStatus("Add shift: ", scheduler.addShift("Tuesday", "11:00", "15:00", "1"));
        //checkStatus("Add shift: ", scheduler.addShift("Monday", "01:00", "07:00", "5"));
        System.out.println(scheduler.displayRoster());
        System.out.println();

        checkStatus("Assign staff: ", scheduler.assignStaff("Sunday", "09:00", "12:00", "Jon", "Snow", true));
        report("Understaffed: ", scheduler.understaffedShifts());
        report("Overstaffed: ", scheduler.overstaffedShifts());
        checkStatus("Assign staff: ", scheduler.assignStaff("Sunday", "09:00", "12:00", "Tyrion", "Lannister", false));
        report("Understaffed: ", scheduler.understaffedShifts());
        report("Overstaffed: ", scheduler.overstaffedShifts());
        checkStatus("Assign staff: ", scheduler.assignStaff("Sunday", "09:00", "12:00", "Bayta", "Darell", false));
        report("Understaffed: ", scheduler.understaffedShifts());
        report("Overstaffed: ", scheduler.overstaffedShifts());
    }

    /**
     * Helper method for when a String is returned
     */
    private static void checkStatus(String header, String status) {
        if (!status.equals("")) {
            System.out.print("Report:" + header + " >>");
            System.out.println(status);
        }
    }
    /**
     * Helper method for when a list of String is returned.
     */
    private static void report(String header, List<String> status) {
        System.out.print("Report:" + header + " >>");
        if (status.size() == 1 && status.get(0).startsWith("ERROR")) {
            System.out.println(status.get(0));
        } else {
            System.out.println(status);
        }
    }
}
