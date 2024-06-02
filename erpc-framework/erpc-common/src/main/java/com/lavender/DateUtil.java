package com.lavender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: lavender
 * @Desc:
 * @create: 2024-06-01 20:14
 **/

public class DateUtil {
    public static Date get(String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(pattern);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
