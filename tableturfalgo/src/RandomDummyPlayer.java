import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

/**
 * This {@code Player} picks a random move out of its available moves each turn. Optionally writes stats about its operations to log files ("randomdummy.txt" for this).
 */
public class RandomDummyPlayer extends Player {
    private boolean imYellow, playForOpponent, printToLog;
    private Duration timeTakenThisTurn;
    private int moveListSizeThisTurn;
    private String timestamp;

    /**
     * Initialise a random dummy player that picks random moves from its list of available moves. Does not write to log files.
     * @param imYellow if this plays for yellow
     * @param playForOpponent if this picks random opponent moves instead (for MCTS algo)
     */
    public RandomDummyPlayer(boolean imYellow, boolean playForOpponent) {
        this.imYellow = imYellow;
        this.playForOpponent = playForOpponent;
        this.printToLog = false;
    }

    /**
     * Initialise a random dummy player that picks random moves from its list of available moves. Writes to "randomdummy.txt".
     * @param imYellow if this plays for yellow
     * @param playForOpponent if this picks random opponent moves instead (for MCTS algo)
     * @param timestamp (optional) for identifying this game in log files ("randomdummy.txt" for this)
     */
    public RandomDummyPlayer(boolean imYellow, boolean playForOpponent, String timestamp) {
        this.imYellow = imYellow;
        this.playForOpponent = playForOpponent;
        this.timestamp = timestamp;
        this.printToLog = true; 
    }

    @Override
    public Move chooseMove(GameState gameState) throws IOException {
        Instant startTimestamp = Instant.now();
        Move output;
        if (!playForOpponent) {
            output = chooseMove(this.getAllMyMoves(gameState, imYellow));
        } else {
            output = chooseMove(this.getAllOpponentMoves(gameState, imYellow));
        }
        timeTakenThisTurn = Duration.between(startTimestamp, Instant.now());
        if (printToLog) {
            String msg = timestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += gameState.getTurnsRemaining();
        msg += " moveList=" + moveListSizeThisTurn;
        msg += " duration=" + timeTakenThisTurn.toMillis() + "ms";
        WriteToLog.write("randomdummy.txt", msg);
        }
        return output;
    }

    /**
     * Picks a random move out of the provided list of moves.
     * @param moveList list of moves to pick from
     * @return a random move from the list
     */
    public Move chooseMove(ArrayList<Move> moveList) {
        this.moveListSizeThisTurn = moveList.size();
        return moveList.get((int) Math.floor(Math.random()*moveListSizeThisTurn));
    }

    @Override
    public Duration getTimeTaken() {
        return timeTakenThisTurn;
    }

    @Override
    public void setTimeAllowed(Duration timeAllowed) {
        // does nothing, this algo has no time limit mechanism
    }
}
