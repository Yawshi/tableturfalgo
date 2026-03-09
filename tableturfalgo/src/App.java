import java.time.Instant;

public class App {
    public static void main(String[] args) throws Exception {
        // generate a string to be a unique ID for this simulated game
        // the ID is used to recognise games across different text files
        // the GameMaster and each play agent writes to its own log (text file)
        String timestamp = Instant.now().toString();
        
        Player yellowPlayer = new RandomDummyPlayer();
        Player bluePlayer = new RandomDummyPlayer();
        new GameMaster(yellowPlayer, bluePlayer, timestamp);
    }
}

// TODO: figure out how to track stats uuhhhhhhhhhhhhhhh