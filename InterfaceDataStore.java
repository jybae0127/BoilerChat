import java.util.List;
/**
 * InterfaceDataStore.java
 *
 * An interface used by the DataStore class. 
 *
 * @author team 1, lab 7
 *
 * @version April 14, 2024
 */
public interface InterfaceDataStore {
    void saveToFile(String filename, String data);
    List<String> readFromFile(String filename);
}
