import java.time.Duration;
import java.time.Instant;

public class App {
    public static void main(String[] args) throws Exception {
        for (int i=0; i<15; i++) {
            runGame("montecarlo(L,150s)", "randomdummy");
            runGame("randomdummy", "montecarlo(L,150s)");
        }
    }

    static void runGame(String yellowName, String blueName) throws Exception {
        // generate a string to be a unique ID for this simulated game
        // the ID is used to recognise games across different text files
        // the GameMaster and each play agent writes to its own log (text file)
        String timestamp = Instant.now().toString();

        // generate both players. take a name from this list:
        // "randomdummy"
        // "minimax(0,0,0)"
        // "minimax(1.1,0.3,0.4)"
        // "alphabeta(0,0,0)" missing x5*2
        // "alphabeta(1.1,0.3,0.4)" missing x5*2
        // "montecarlo(D,30s)" missing x5*2
        // "montecarlo(D,150s)"
        // "montecarlo(L,30s)" missing x15*2
        // "montecarlo(L,150s)" missing x15*2

        Player yellowPlayer = generateNewPlayer(yellowName, true, timestamp);
        Player bluePlayer = generateNewPlayer(blueName, false, timestamp);
        new GameMaster(yellowPlayer, yellowName, bluePlayer, blueName, false, timestamp);
    }

    private static Player generateNewPlayer(String player, boolean isYellow, String timestamp) {
        switch (player) {
            case "randomdummy":
                return new RandomDummyPlayer(isYellow, false, timestamp);
            case "minimax(0,0,0)":
                return new MinimaxAlgo(isYellow, false, timestamp, 1, 0, 0, 0);
            case "minimax(1.1,0.3,0.4)":
                return new MinimaxAlgo(isYellow, false, timestamp, 1, (float)1.1, (float)0.3, (float)0.4);
            case "alphabeta(0,0,0)":
                return new MinimaxAlgo(isYellow, true, timestamp, 1, 0, 0, 0);
            case "alphabeta(1.1,0.3,0.4)":
                return new MinimaxAlgo(isYellow, true, timestamp, 1, (float)1.1, (float)0.3, (float)0.4);
            case "montecarlo(D,30s)":
                {Player selfSim = new RandomDummyPlayer(isYellow, false);
                Player oppSim = new RandomDummyPlayer(isYellow, true);
                return new MonteCarloAlgo(isYellow, timestamp, Duration.ofSeconds(30), selfSim, oppSim);}
            case "montecarlo(D,150s)":
                {Player selfSim = new RandomDummyPlayer(isYellow, false);
                Player oppSim = new RandomDummyPlayer(isYellow, true);
                return new MonteCarloAlgo(isYellow, timestamp, Duration.ofSeconds(150), selfSim, oppSim);}
            case "montecarlo(L,30s)":
                {Player selfSim = new RandomLargeCardPlayer(isYellow, false);
                Player oppSim = new RandomLargeCardPlayer(isYellow, true);
                return new MonteCarloAlgo(isYellow, timestamp, Duration.ofSeconds(30), selfSim, oppSim);}
            case "montecarlo(L,150s)":
                {Player selfSim = new RandomLargeCardPlayer(isYellow, false);
                Player oppSim = new RandomLargeCardPlayer(isYellow, true);
                return new MonteCarloAlgo(isYellow, timestamp, Duration.ofSeconds(150), selfSim, oppSim);}
            default:
                return null;
        }
    }


}