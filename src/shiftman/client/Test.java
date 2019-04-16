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

    public static void main(String[] args) {
        ShiftMan scheduler = new ShiftManServer();
        scheduler.newRoster("Test Shop");

        for (String[] staff : STAFF) {
            scheduler.registerStaff(staff[0], staff[1]);
        }
        System.out.println(scheduler.displayRoster());
        System.out.println();

        //checkStatus("Add shift: ", scheduler.addShift("Sunday", "12:00", "22:00", "3"));
        checkStatus("Add shift: ", scheduler.addShift("Saturday", "07:00", "12:00", "1"));
        //checkStatus("Add shift: ", scheduler.addShift("Tuesday", "11:00", "15:00", "1"));
        //checkStatus("Add shift: ", scheduler.addShift("Monday", "01:00", "07:00", "5"));
        System.out.println(scheduler.displayRoster());
        System.out.println();

        checkStatus("Assign staff: ", scheduler.assignStaff("Saturday", "07:00", "12:00", "Jon", "Snow", true));
        report("Understaffed: ", scheduler.understaffedShifts());
        report("Overstaffed: ", scheduler.overstaffedShifts());
        checkStatus("Assign staff: ", scheduler.assignStaff("Saturday", "07:00", "12:00", "Tyrion", "Lannister", false));
        report("Understaffed: ", scheduler.understaffedShifts());
        report("Overstaffed: ", scheduler.overstaffedShifts());
        checkStatus("Assign staff: ", scheduler.assignStaff("Saturday", "07:00", "12:00", "Bayta", "Darell", false));
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
