import java.io.IOException;
import java.util.ArrayList;

/**
 * Base abstract class for all game playing algorithms for the project.
 */
public abstract class Player {
    /**
     * Generates a list of moves available to a player at a certain game state: <br>
     * - the moves are ordered from biggest to smallest card size <br>
     * - if the card can be played as a special attack, those moves are listed after the card's regular moves (if any) before the next card's moves <br>
     * - null/pass moves for discarding each card are always appended at the end of the list <br>
     * @param gameState game state to be evaluated
     * @param imYellow if the player is yellow
     * @return list of moves available
     */
    public ArrayList<Move> getAllMyMoves(GameState gameState, boolean imYellow) {
        ArrayList<Card> myHand;
        int mySpecialsHeld;
        if (imYellow) {
            myHand = gameState.getYellowHand();
            mySpecialsHeld = gameState.getYellowSpecialsHeld();
        } else {
            myHand = gameState.getBlueHand();
            mySpecialsHeld = gameState.getBlueSpecialsHeld();
        }
        
        ArrayList<Move> output = new ArrayList<Move>();
        for (Card card : myHand) {
            output.addAll(gameState.getBoard().getMoves(card, false, imYellow));
            if (card.getSpecialCost() <= mySpecialsHeld) {
                output.addAll(gameState.getBoard().getMoves(card, true, imYellow));
            }
        }
        for (Card card : myHand) {
            output.add(new Move(card, imYellow));
        }
        return output;
    }

    /**
     * Generates a list of moves available to the opponent at a certain game state: <br>
     * - the list includes cards from both the opponent's hand and their deck <br>
     * - the moves are ordered from biggest to smallest card size <br>
     * - if the card can be played as a special attack, those moves are listed after the card's regular moves (if any) before the next card's moves <br>
     * - null/pass moves for discarding each card are always appended at the end of the list <br>
     * @param gameState game state to be evaluated
     * @param imYellow if the player is yellow
     * @return list of moves available
     */
    public ArrayList<Move> getAllOpponentMoves(GameState gameState, boolean imYellow) {
        ArrayList<Card> opponentCards;
        int opponentSpecialsHeld;
        if (imYellow) {
            // collect both opponent's hand and deck, and sort in order of size
            opponentCards = (ArrayList<Card>) gameState.getBlueHand().clone();
            for (Card card : gameState.getBlueDeck()) {
                for (int i=0; i<opponentCards.size(); i++) {
                    if (card.getSize() > opponentCards.get(i).getSize()) {
                        opponentCards.add(i, card);
                        break;
                    }
                }
            }
            opponentSpecialsHeld = gameState.getBlueSpecialsHeld();
        } else {
            opponentCards = (ArrayList<Card>) gameState.getYellowHand().clone();
            for (Card card : gameState.getYellowDeck()) {
                for (int i=0; i<opponentCards.size(); i++) {
                    if (card.getSize() > opponentCards.get(i).getSize()) {
                        opponentCards.add(i, card);
                        break;
                    }
                }
            }
            opponentSpecialsHeld = gameState.getYellowSpecialsHeld();
        }

        ArrayList<Move> output = new ArrayList<Move>();
        for (Card card : opponentCards) {
            output.addAll(gameState.getBoard().getMoves(card, false, !imYellow));
            if (card.getSpecialCost() <= opponentSpecialsHeld) {
                output.addAll(gameState.getBoard().getMoves(card, true, !imYellow));
            }
        }
        for (Card card : opponentCards) {
            output.add(new Move(card, !imYellow));
        }
        return output;
    }

    /**
     * Prompt the algorithm to decide on a move at a certain game state.
     * @param gameState game state to be evaluated
     * @return the decided move
     * @throws IOException if an error occurs when writing to the algorithm's log files
     * @throws InvalidEndTurnStateException if an error occurs when attempting to perform end of turn actions
     */
    public abstract Move chooseMove(GameState gameState) throws IOException, InvalidEndTurnStateException;
}
