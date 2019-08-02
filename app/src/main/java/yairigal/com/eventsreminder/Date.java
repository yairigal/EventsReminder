package yairigal.com.eventsreminder;

import java.io.Serializable;

/**
 * Created by Yair Yigal on 2018-01-06.
 */

public class Date implements Serializable {
    public int year;

    public Date() {
    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int month;
    public int day;

    @Override
    public String toString() {
        return this.day + "-" + this.month + "-" + this.year;
    }
}
