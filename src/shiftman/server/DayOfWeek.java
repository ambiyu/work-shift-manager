package shiftman.server;

public enum DayOfWeek {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;

    public static boolean isValidDay(String dayOfWeek) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (dayOfWeek.equals(day.toString())) {
                return true;
            }
        }
        return false;
    }
}
