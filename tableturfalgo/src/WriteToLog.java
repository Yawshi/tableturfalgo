import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to write to text files for tracking stats.
 */
public class WriteToLog {
    /**
     * Append a message to a text file. This automatically adds a "\n" to the end of the message.
     * @param filename text file to be written to
     * @param msg message to be written
     * @throws IOException if an error occurs when writing to the text file
     */
    public static void write(String filename, String msg) throws IOException {
        try (FileWriter pointer = new FileWriter(filename,true)) {
            pointer.write(msg + "\n");
        } catch (IOException e) {
            System.out.println("WriteToLog: An error occurred!");
            System.out.println("was writing: " + msg);
            System.out.println("to file: " + filename);
            e.printStackTrace();
        }
    }
}
