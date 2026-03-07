import java.util.ArrayList;

/**
 * Base abstract class for all game playing algorithms for the project. <br>
 * <br>
 * Methods: <br>
 * > getAllMoves() <br>
 * > chooseMove() *abstract* 
 */
public abstract class Player {
    /**
     * Generates a list of moves available to a player at a certain game state: <br>
     * - the moves are ordered from biggest to smallest card size <br>
     * - if the card can be played as a special attack, those moves are listed after the card's regular moves (if any) before the next card's moves <br>
     * - null/pass moves for discarding each card are always appended at the end of the list <br>
     * @param gameState game state to be evaluated
     * @param isYellow the player in question
     * @return list of moves available
     */
    public ArrayList<Move> getAllMoves(GameState gameState, boolean isYellow) {
        ArrayList<Card> myHand;
        int mySpecialsHeld;
        if (isYellow) {
            myHand = gameState.getYellowHand();
            mySpecialsHeld = gameState.getYellowSpecialsHeld();
        } else {
            myHand = gameState.getBlueHand();
            mySpecialsHeld = gameState.getBlueSpecialsHeld();
        }
        
        ArrayList<Move> output = new ArrayList<Move>();
        for (Card card : myHand) {
            output.addAll(gameState.getBoard().getMoves(card, false, isYellow));
            if (card.getSpecialCost() <= mySpecialsHeld) {
                output.addAll(gameState.getBoard().getMoves(card, true, isYellow));
            }
        }
        for (Card card : myHand) {
            output.add(new Move(card, isYellow));
        }
        return output;
    }

    public abstract Move chooseMove(ArrayList<Move> moveList);
}
