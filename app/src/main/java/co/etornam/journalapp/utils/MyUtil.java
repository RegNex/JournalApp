package co.etornam.journalapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyUtil {
    public static String convertTime(long timestamp) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("E dd, MM-yyyy", Locale.getDefault());
        Date date = new Date(timestamp);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
