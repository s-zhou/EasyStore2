package com.example.easystore2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculateDate {

    public static String getState(String dataExpired) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
            Date expiredDate = dateFormat.parse(dataExpired);
            final Calendar c2 = Calendar.getInstance();
            c2.setTime(expiredDate);
            c2.add(Calendar.DAY_OF_YEAR, -1);

            Date currentDate = dateFormat.parse(setDataFormat(Calendar.getInstance()));

            Date aboutToExpiredData2 = dateFormat.parse(setDataFormat(c2));
            if((currentDate.before(expiredDate) && currentDate.after(aboutToExpiredData2))||(currentDate.equals(expiredDate))||(currentDate.equals(aboutToExpiredData2))){
                return "about";
            }
            else if(expiredDate.before(currentDate)){
                return "expired";
            }
            else return "ok";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "ok";
    }


    private static String setDataFormat(Calendar c) {
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String d = String.valueOf(day);
        String m = String.valueOf(month);
        String y = String.valueOf(c.get(Calendar.YEAR));
        if(day<10) d ="0" + d;
        if(month<10) y = "0" + y;
        return (y + "-" + m + "-" + d);
    }


}

