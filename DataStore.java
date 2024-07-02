import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DataStore.java
 *
 * A class which handles data storage and retrieval.
 *
 * @author team 1, lab 7
 *
 * @version April 14, 2024
 */
public class DataStore implements InterfaceDataStore {

    public void saveToFile(String filename, String data) {
        try (FileWriter fw = new FileWriter(filename, true); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
