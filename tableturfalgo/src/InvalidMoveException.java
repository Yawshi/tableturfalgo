public class InvalidMoveException extends Exception {
    public InvalidMoveException(String msg) {
        super("The move is invalid: " + msg);
    }
}
