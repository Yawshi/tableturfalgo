import java.util.ArrayList;

/**
 * Represents a game state, storing info on the board and each player's possessions.
 */
public class GameState {
    private Board board;
    private int turnsRemaining, yellowSpecialsSpent, blueSpecialsSpent;
    private ArrayList<Card> yellowDeck, blueDeck, yellowHand, blueHand;
    private Move yellowMove, blueMove;

    /**
     * Generate a fresh game state for a new game.
     * @param yellowDeckStart ordered list of yellow player's deck: cards will be drawn in order from this
     * @param blueDeckStart ordered list of blue player's deck: cards will be drawn in order from this
     */
    public GameState(ArrayList<Card> yellowDeckStart, ArrayList<Card> blueDeckStart) {
        this.board = new Board();
        this.turnsRemaining = 12;
        this.yellowSpecialsSpent = 0;
        this.blueSpecialsSpent = 0;

        this.yellowDeck = yellowDeckStart;
        this.blueDeck = blueDeckStart;
        this.yellowHand = new ArrayList<Card>();
        this.blueHand = new ArrayList<Card>();
        drawCards();
        drawCards();
        drawCards();
        drawCards();

        this.yellowMove = null;
        this.blueMove = null;
    }

    /**
     * Generate a cloned game state, for algorithm manipulation and evaluation purposes.
     * @param prevState game state to be cloned
     */
    public GameState(GameState prevState) {
        this.board = new Board(prevState.getBoard());
        this.turnsRemaining = prevState.getTurnsRemaining();
        this.yellowSpecialsSpent = prevState.yellowSpecialsSpent;
        this.blueSpecialsSpent = prevState.blueSpecialsSpent;

        this.yellowDeck = (ArrayList<Card>) prevState.yellowDeck.clone();
        this.blueDeck = (ArrayList<Card>) prevState.blueDeck.clone();
        this.yellowHand = (ArrayList<Card>) prevState.yellowHand.clone();
        this.blueHand = (ArrayList<Card>) prevState.blueHand.clone();

        this.yellowMove = prevState.yellowMove;
        this.blueMove = prevState.blueMove;
    }

    private void drawCards() {
        Card yellowCard = this.yellowDeck.remove(0);
        for (int i=0; i<=yellowHand.size(); i++) {
            if (i == yellowHand.size()) {
                yellowHand.add(yellowCard);
                break;
            }
            else {
                if (yellowCard.getSize() > yellowHand.get(i).getSize()) {
                    yellowHand.add(i, yellowCard);
                    break;
                }
            }
        }

        Card blueCard = this.blueDeck.remove(0);
        for (int i=0; i<=blueHand.size(); i++) {
            if (i == blueHand.size()) {
                blueHand.add(blueCard);
                break;
            }
            else {
                if (blueCard.getSize() > blueHand.get(i).getSize()) {
                    blueHand.add(i, blueCard);
                    break;
                }
            }
        }
    }

    /**
     * Finds and draws a specific card from a player's deck into their hand.
     * @param card card in question
     * @param isYellow player in question
     */
    public void drawCardFromDeck(Card card, boolean isYellow) {
        if (isYellow) {
            yellowDeck.remove(card);
            for (int i=0; i<yellowHand.size(); i++) {
                if (card.getSize() > yellowHand.get(i).getSize()) {
                    yellowHand.add(i, card);
                    return;
                }
            }
            if (!yellowHand.contains(card)) {
                yellowHand.add(card);
            }
        } else {
            blueDeck.remove(card);
            for (int i=0; i<blueHand.size(); i++) {
                if (card.getSize() > blueHand.get(i).getSize()) {
                    blueHand.add(i, card);
                    return;
                }
            }
            if (!blueHand.contains(card)) {
                blueHand.add(card);
            }
        }
    }

    /**
     * Finds and draws the indexed card in a player's deck into their hand.
     * @param index index of card in the deck (list of cards)
     * @param isYellow player in question
     */
    public void drawCardFromDeck(int index, boolean isYellow) {
        Card card;
        if (isYellow) {card = this.getYellowDeck().get(index);}
        else {card = this.getBlueDeck().get(index);}
        this.drawCardFromDeck(card, isYellow);
    }

    /**
     * Draws a random card from a player's deck into their hand.
     * @param isYellow player in question
     */
    public void drawRandomCardFromDeck(boolean isYellow) {
        int deckSize;
        if (isYellow) {deckSize = this.getYellowDeck().size();}
        else {deckSize = this.getBlueDeck().size();}
        this.drawCardFromDeck((int) Math.floor(Math.random()*deckSize), isYellow);
    }

    /**
     * Resolves and performs all end of turn actions, including: <br>
     * - update the board to lay out ink patterns placed this turn <br>
     * - update special points held by both players <br>
     * - remove the played card from both players' hands <br>
     * - draw a card from each players' decks into their hands <br>
     * - decrement the remaining turn counter <br>
     * - reset {@code yellowMove} and {@code blueMove} to null for a new turn
     * @throws InvalidEndTurnStateException if the state does not have both player's moves decided
     */
    public void endTurn() throws InvalidEndTurnStateException {
        endTurn(true);
    }

    /**
     * Resolves and performs most/all end of turn actions, including: <br>
     * - update the board to lay out ink patterns placed this turn <br>
     * - update special points held by both players <br>
     * - remove the played card from both players' hands <br>
     * - decrement the remaining turn counter <br>
     * - reset {@code yellowMove} and {@code blueMove} to null for a new turn <br>
     * - (optional) draw a card from each players' decks into their hands 
     * @param drawcards whether or not to draw a card into each player's hands (set to false for algo evals)
     * @throws InvalidEndTurnStateException if the state does not have both player's moves decided
     */
    public void endTurn(boolean drawcards) throws InvalidEndTurnStateException {
        if (!this.hasBothMovesAssigned()) {
            throw new InvalidEndTurnStateException();
        }
        board.updateBoard(yellowMove, blueMove);
        turnsRemaining -= 1;
        if (turnsRemaining <= 0) {return;}

        if (yellowMove.isSpecial()) {yellowSpecialsSpent += yellowMove.getCard().getSpecialCost();}
        if (blueMove.isSpecial()) {blueSpecialsSpent += blueMove.getCard().getSpecialCost();}

        if (!yellowHand.remove(yellowMove.getBaseCard())) {yellowDeck.remove(yellowMove.getBaseCard());}
        if (!blueHand.remove(blueMove.getBaseCard())) {blueDeck.remove(blueMove.getBaseCard());}

        yellowMove = null;
        blueMove = null;

        if (drawcards) {drawCards();}
    }

    public String toString() {
        String output = board.toString();
        output += "t=" + turnsRemaining + ": " + board.getYellowOccupied() + "(" + this.getYellowSpecialsHeld() 
        + ") - " + board.getBlueOccupied() + "(" + this.getBlueSpecialsHeld() +")\n";
        return output;
    }

    public Board getBoard() {
        return board;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public int getYellowSpecialsHeld() {
        return board.getYellowSpecials() - yellowSpecialsSpent;
    }

    public int getBlueSpecialsHeld() {
        return board.getBlueSpecials() - blueSpecialsSpent;
    }

    public ArrayList<Card> getYellowDeck() {
        return yellowDeck;
    }

    public ArrayList<Card> getBlueDeck() {
        return blueDeck;
    }

    public ArrayList<Card> getYellowHand() {
        return yellowHand;
    }

    public ArrayList<Card> getBlueHand() {
        return blueHand;
    }

    public void setYellowMove(Move yellowMove) {
        this.yellowMove = yellowMove;
    }

    public void setBlueMove(Move blueMove) {
        this.blueMove = blueMove;
    }

    /**
     * Resets the game state to the start of the turn, where neither player has made a decision.
     */
    public void clearMoves() {
        this.yellowMove = null;
        this.blueMove = null;
    }

    /**
     * Checks if both players have moves decided at this game state. If true, {@code endTurn()} may be run.
     * @return true if both players have moves assigned
     * @see GameState#endTurn()
     */
    public boolean hasBothMovesAssigned() {
        return (this.yellowMove != null && this.blueMove != null);
    }
}
