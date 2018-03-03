package yairigal.com.eventsreminder;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Years;

import java.io.Serializable;

/**
 * Created by Yair Yigal on 2018-01-05.
 */

class DataObject implements Serializable {

    //time format -> year@@month@@day
    public DataObject(Date time, String title) {
        this.time = time;
        this.title = title;
    }

    public DataObject() {}

    public String getDisplayString(){
        DateTime now = DateTime.now();
        Date d = getElapsedTime(now,this.time);
        String text = "";
        if(d.year != 0){
            text += d.year + " Year";
            if(d.year > 1)
                text += "s";
            text += " ";
        }
        if(d.month != 0){
            text += d.month + " Month";
            if(d.month > 1)
                text += "s";
            text += " ";
        }
        if(d.day != 0){
            text += d.day + " Day";
            if(d.day > 1)
                text += "s";
            text += " ";
        }
        return text;
    }

    private static Date getElapsedTime(Date a, Date b){
        DateTime dta = new DateTime(a.year,a.month,a.day,0,0);
        DateTime dtb = new DateTime(b.year,b.month,b.day,0,0);
        Period p = new Period(dtb, dta);
        return new Date(p.getYears(),p.getMonths(),p.getDays());
    }

    private static Date getElapsedTime(DateTime a, Date b){
        DateTime dtb = new DateTime(b.year,b.month,b.day,0,0);
        int years = Years.yearsBetween(dtb, a).getYears();
        int months = Months.monthsBetween(dtb, a).getMonths();
        int days = Days.daysBetween(dtb, a).getDays();
        return new Date(years,months % 12,(days - years*365)%30);
    }

    public Date time;
    public String title;

}

