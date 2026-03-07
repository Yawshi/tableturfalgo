import java.util.ArrayList;

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
        drawCard();
        drawCard();
        drawCard();
        drawCard();
    }

    private void drawCard() {
        Card yellowCard = this.yellowDeck.remove(0);
        for (int i=0; i<=yellowHand.size(); i++) {
            if (i == yellowHand.size()) {
                yellowHand.add(yellowCard);
                break;
            }
            else {
                if (yellowCard.getSize() < yellowHand.get(i).getSize()) {
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
                if (blueCard.getSize() < blueHand.get(i).getSize()) {
                    blueHand.add(i, blueCard);
                    break;
                }
            }
        }
    }

    /**
     * Resolves and performs end of turn actions, including: <br>
     * - update the board to lay out ink patterns placed this turn <br>
     * - update special points held by both players <br>
     * - remove the played card from both players' hands <br>
     * - draw a card from each players' decks into their hands <br>
     * - decrement the remaining turn counter <br>
     * - reset {@code yellowMove} and {@code blueMove} to null for a new turn
     */
    public void endTurn() {
        board.updateBoard(yellowMove, blueMove);
        turnsRemaining -= 1;
        if (turnsRemaining <= 0) {return;}

        if (yellowMove.isSpecial()) {yellowSpecialsSpent += yellowMove.getCard().getSpecialCost();}
        if (blueMove.isSpecial()) {blueSpecialsSpent += blueMove.getCard().getSpecialCost();}
        yellowHand.remove(yellowMove.getCard());
        blueHand.remove(blueMove.getCard());
        drawCard();
        yellowMove = null;
        blueMove = null;
    }

    public String toString() {
        String output = board.toString();
        output += "t=" + turnsRemaining + ": " + board.getYellowOccupied() + "(" + board.getYellowSpecials() 
        + ") - " + board.getBlueOccupied() + "(" + board.getBlueSpecials() +")\n";
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
}
