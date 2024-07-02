/**
 * TimeStamp.java
 *
 * A class which can produce and perform operations with time stamps.
 * To get a timestamp of the form [day of week], [month] [day], [year], hh:mm:ss, use toStringFull(). 
 * To get a timestamp of, for example, [x] days ago, use toStringShort(). 
 *
 * If created without any parameters, will default to the current time.
 * Can also pass in a milliseconds parameter for a custom time. 
 * Could use math with System.currentTimeMillis() to make custom times, for instance, to make one corresponding to an
 * hour ago, could say System.currentTimeMillis() - 1000 * 3600. 
 * Note: might have to cast integers to long to get it to work. 
 *
 * @author lab 007 team 1
 *
 * @version March 30, 2024
 *
 */

public class TimeStamp {

    private long timeMillis;

    public TimeStamp() {
        timeMillis = System.currentTimeMillis();
    }

    /*
    A constructor which takes a custom timeMillis (for testing purposes)
     */
    public TimeStamp(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public String dayOfWeek() {
        switch (getAbsoluteDay() % 7) {
            case 0:
                return "Thursday";
            case 1:
                return "Friday";
            case 2:
                return "Saturday";
            case 3:
                return "Sunday";
            case 4:
                return "Monday";
            case 5:
                return "Tuesday";
            case 6:
                return "Wednesday";
            default:
                return "o_O";
        }
    }

    /*
    A method which returns whether an inputted year is a leap year.
    Leap Years are any year that can be exactly divided by 4 (such as 2020, 2024, 2028, etc)
 	Except if it can be exactly divided by 100, then it isn't (such as 2100, 2200, etc)
 	Except if it can be exactly divided by 400, then it is (such as 2000, 2400)
     */
    public static boolean yearIsLeapYear(int year) {
        if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        }
        return year % 4 == 0;
    }

    /*
    A method which returns the number of days in an inputted month and year (month is an integer from 1-12)
    */
    public static int daysInMonth(int month, int year) {
        switch (month) {
            case 1, 3, 5, 7, 8, 10, 12:
                return 31;
            case 4, 6, 9, 11:
                return 30;
            case 2:
                if (yearIsLeapYear(year)) {
                    return 29;
                }
                return 28;
            default:
                return -1;
        }
    }

    /*
    A method which returns the number of days in an inputted year
    */
    public static int daysInYear(int year) {
        if (yearIsLeapYear(year)) {
            return 366;
        }
        return 365;
    }

    /*
    Returns the number of days that have passed since January 1, 1970
     */
    public int getAbsoluteDay() {
        //UTC is 4 hours ahead of EST
        long timeMillisEST = timeMillis - 1000 * 3600 * 4;
        //Days that have passed since January 1, 1970
        return (int)(timeMillisEST / (1000 * 3600 * 24));
    }

    public String getDate() {
        /*
        Calculating year, month, and day
        */

        int theYear = 1970;
        int theMonth = 1;
        int theDay = 1;

        int daysRemaining = getAbsoluteDay();
        //Figuring out the year
        while (daysRemaining >= daysInYear(theYear)) {
            daysRemaining -= daysInYear(theYear);
            theYear++;
        }
        //Figuring out the month
        while (daysRemaining >= daysInMonth(theMonth, theYear)) {
            daysRemaining -= daysInMonth(theMonth, theYear);
            theMonth++;
        }
        //Figuring out the day
        theDay += daysRemaining;

        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", 
                           "October", "November", "December"};
        String monthString = months[theMonth - 1];

        return dayOfWeek() + ", " + monthString + " " + theDay + ", " + theYear;
    }

    /*
    Gives a String of the form
    "Just now" (<1 min) or
    "[x] minutes ago" or
    "[x] hours ago" or
    "[x] days ago" or
    "[x] months ago" or
    "[x] years ago"
     */
    public String toStringShort() {

        TimeStamp now = new TimeStamp();

        long millisGap = now.getTimeMillis() - timeMillis;
        System.out.println(millisGap);

        if (millisGap >= ((long)1000 * 3600 * 24 * 365 * 2)) {
            //Case: a year or more ago
            int years = 1;
            while (millisGap >= ((long)1000 * 3600 * 24 * 365 * (years + 1))) {
                years++;
            }
            return years > 1 ? years + " years ago" : "1 year ago";
        } else if (millisGap >= ((long)1000 * 3600 * 24 * 30) - 1) {
            //Case: a month or more ago
            int months = 1;
            while (millisGap >= ((long)1000 * 3600 * 24 * 30 * (months + 1))) {
                months++;
            }
            return months > 1 ? months + " months ago" : "1 month ago";
        } else if (millisGap >= ((long)1000 * 3600 * 24) - 1) {
            //Case: a day or more ago
            int days = 1;
            while (millisGap >= ((long)1000 * 3600 * 24 * (days + 1))) {
                days++;
            }
            return days > 1 ? days + " days ago" : "1 day ago";
        } else if (millisGap >= 1000 * 3600) {
            //Case: an hour or more ago
            int hours = 1;
            while (millisGap >= ((long)1000 * 3600 * (hours + 1))) {
                hours++;
            }
            return hours > 1 ? hours + " hours ago" : "1 hour ago";
        } else if (millisGap >= 1000 * 60) {
            //Case: a minute or more ago
            int minutes = 1;
            while (millisGap >= ((long)1000 * 60 * (minutes + 1))) {
                minutes++;
            }
            return minutes > 1 ? minutes + " minutes ago" : "1 minute ago";
        } else {
            return "just now";
        }

    }

    /*
    Gives a String of the form
    [day of week], [month] [day], [year], hh:mm:ss
     */
    public String toStringFull() {

        //UTC is 4 hours ahead of EST
        long timeMillisEST = timeMillis - 1000 * 3600 * 4;
        //Millis that belong just to this current day
        long leftoverMillis = timeMillisEST - (1000 * 3600 * 24 * (long)getAbsoluteDay());
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        //There are 1000 * 3600 milliseconds in an hour
        while (leftoverMillis > 1000 * 3600) {
            hours++;
            leftoverMillis -= 1000 * 3600;
        }
        //There are 1000 * 60 milliseconds in a minute
        while (leftoverMillis > 1000 * 60) {
            minutes++;
            leftoverMillis -= 1000 * 60;
        }
        //There are 1000 milliseconds in a second
        seconds = (int)(leftoverMillis / 1000);

        /*
        Formatting the result
         */

        String hour = "" + hours;
        String minute = "" + minutes;
        String second = "" + seconds;

        //If the hour is only a single digit, put a 0 on the start of it
        if (Integer.parseInt(hour) < 10) {
            hour = "0" + hour;
        }
        //If the minutes is only a single digit, put a 0 on the start of it
        if (Integer.parseInt(minute) < 10) {
            minute = "0" + minute;
        }
        //If the seconds is only a single digit, put a 0 on the start of it
        if (Integer.parseInt(second) < 10) {
            second = "0" + second;
        }

        //Returning result
        return getDate() + ", " + hour + ":" + minute + ":" + second;
    }

}
