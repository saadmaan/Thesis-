package ac.sust.saimon.sachetan.data.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saimon on 22-May-17.
 */

public class DateFormatter {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
    private static final long MINUTE = 60000L;
    private static final long HOUR = 3600000L;
    private static final long DAY = 86400000L;

    public static String getDateAsString(Date date) {
        Date now = new Date();
        String formattedDate = dateFormat.format(date);
        long interval = now.getTime() - date.getTime();
        String formattedInterval;
        if (interval >= DAY) {
            formattedInterval = formattedDate;
        } else if (interval >= HOUR) {
            formattedInterval = Long.toString(interval / HOUR) + " hours ago";
        } else {
            formattedInterval = Long.toString(interval / MINUTE) + " minutes ago";
        }
        return formattedInterval;
    }
}
