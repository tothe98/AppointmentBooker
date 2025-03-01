package com.example.appointmentbooker.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static Date StringToDate(String dateString){
        Date date=null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        try {
            date = formatter.parse(dateString);

        }  catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date AddMinutesToDate(Date date, int minutes){
        Date updatedDate=null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        long updatedTimeInMillis = date.getTime() + minutes * 60 * 1000;
        updatedDate = new Date(updatedTimeInMillis);
        return updatedDate;
    }
}
