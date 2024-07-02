/**
 * InterfaceTimeStamp.java
 *
 * An interface for the TimeStamp class.
 *
 * @author team 1, lab 7
 *
 * @version April 14, 2024
 */
public interface InterfaceTimeStamp {

    long getTimeMillis();

    String dayOfWeek();

    int getAbsoluteDay();

    String getDate();

    String toStringShort();

    String toStringFull();

}
