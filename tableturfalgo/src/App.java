import java.time.Instant;

public class App {
    public static void main(String[] args) throws Exception {
        // generate a string to be a unique ID for this simulated game
        // the ID is used to recognise games across different text files
        // the GameMaster and each play agent writes to its own log (text file)
        String timestamp = Instant.now().toString();
        
        Player yellowPlayer = new AlphaBetaAlgo(true, timestamp, 1, (float) 1.1, (float) 2.01, (float) 1.05);
        Player bluePlayer = new AlphaBetaAlgo(false, timestamp, 1, (float) 1.1, (float) 2.01, (float) 1.05);
        new GameMaster(yellowPlayer, bluePlayer, timestamp);
    }
}