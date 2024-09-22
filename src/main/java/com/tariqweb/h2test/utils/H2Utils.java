package com.tariqweb.h2test.utils;


import org.h2.expression.function.Function;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;


public class H2Utils {
    public static long daysBetween(Timestamp t1, Timestamp t2) {
        return ChronoUnit.DAYS.between(t1.toInstant(), t2.toInstant());
    }

    // Date formats to parse strings
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
    };

    public static long daysBetweenParseAny(String t1, String t2) {
        return ChronoUnit.DAYS.between(
                convertStringToSqlDate(t1).toInstant(),
                convertStringToSqlDate(t2).toInstant()
        );
    }

    public static Date convertStringToSqlDate(String obj) {
        if (obj == null) {
            return null;
        }
        String dateString = (String) obj;
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                sdf.setLenient(false); // Ensure strict parsing
                return new Date(sdf.parse(dateString).getTime());
            } catch (ParseException e) {
                // Try the next format if parsing fails
            }
        }
        // If all else fails, try parsing ISO-8601 format
        try {
            Instant instant = Instant.parse(dateString);
            return new Date(instant.toEpochMilli());
        } catch (Exception e) {
            // Do nothing if parsing fails
        }
        // If the conversion failed
        throw new IllegalArgumentException("Cannot convert to SQL Date: " + obj);
    }


    @SuppressWarnings("rawtypes")
    public static int removeDateDifference() {
        try {

            Field field = Function.class.getDeclaredField("FUNCTIONS_BY_NAME");
//            Field field = FunctionN.class.getDeclaredField("FUNCTIONS_BY_NAME");

//            Field field = org.h2.expression.function.JavaFunction.class.getDeclaredField("FUNCTIONS_BY_NAME");
//            Field field = org.h2.expression.function.JavaFunction.class.getDeclaredField("FUNCTIONS");
            field.setAccessible(true);
            ((Map)field.get(null)).remove("DATEDIFF");
        } catch (Exception e) {
            throw new RuntimeException("failed to remove date-difference", e);
        }
        return 0;
    }
}
