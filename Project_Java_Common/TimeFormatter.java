public class TimeFormatter {
    public static String format(long millis) {
        long day = millis / (1000 * 60 * 60 * 24);
        long hour = (millis / (1000 * 60 * 60)) % 24;
        long minute = (millis / (1000 * 60))  % 60;
        long second = (millis / 1000)  % 60;
        long millisSec = millis % 1000;

        if (day > 0) {
            return String.format("%d day + %02d:%02d:%02d.%03d", day, hour, minute, second, millisSec);
        } else {
            return String.format("%02d:%02d:%02d.%03d", hour, minute, second, millisSec);
        }
    }
}
