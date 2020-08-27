package com.example.budspaces.Samples;

import java.util.Date;
import java.util.GregorianCalendar;

public class RandomDateOfBirth {

    public static Date getBirthDate() {
        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(1950, 1990);
        gc.set(gc.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        return gc.getTime();
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}