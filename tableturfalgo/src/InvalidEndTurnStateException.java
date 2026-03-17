public class InvalidEndTurnStateException extends Exception {
    public InvalidEndTurnStateException() {
        super("The game state where endTurn() is called does not have both player's moves decided.");
    }
}
