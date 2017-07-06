package com.mycompany.readmark.utils.commen;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo.
 */

public class DateUtils {

    public static String MillsToDate(long timeMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

}
