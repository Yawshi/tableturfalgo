import java.time.Duration;
import java.time.Instant;

public class App {
    public static void main(String[] args) throws Exception {
        // String cardString = "121";
        // Card card = new Card(cardString, 1);
        // Move move = new Move(card, false);

        // Move moove = new Move(card, false);
        // System.out.println(move.equals(moove));

        runGame();
    }

    static void runGame() throws Exception {
        // generate a string to be a unique ID for this simulated game
        // the ID is used to recognise games across different text files
        // the GameMaster and each play agent writes to its own log (text file)
        String timestamp = Instant.now().toString();
        
        Duration timeLimit = Duration.ofSeconds(300);
        Player yellowSelfSim = new RandomDummyPlayer(true, false);
        Player yellowOppSim = new RandomDummyPlayer(true, true);
        Player yellowPlayer = new MonteCarloAlgo(true, timestamp, timeLimit, yellowSelfSim, yellowOppSim);

        // Player blueSelfSim = new RandomDummyPlayer(false, false);
        // Player blueOppSim = new RandomDummyPlayer(false, true);
        // Player bluePlayer = new MonteCarloAlgo(false, timestamp, timeLimit, blueSelfSim, blueOppSim);

        Player bluePlayer = new RandomDummyPlayer(false, false);

        new GameMaster(yellowPlayer, bluePlayer, timestamp);
    }
}