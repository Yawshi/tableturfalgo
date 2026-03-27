import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

/**
 * This algorithm first picks a card randomly (with bias towards larger cards) from its list of available cards, then picks a random move available using that card. If the chosen card has no available moves then it instead picks a random pass (null) move. <br>
 * INTENDED USE ONLY FOR MCTS SIMULATION, DO NOT USE FOR DECISION MAKING ON ITS OWN
 */
public class RandomLargeCardPlayer extends Player {
private boolean imYellow, playForOpponent;

    /**
     * Initialises a random player (biased towards larger cards). <br>
     * DOES NOT PRINT TO LOG FILES AND CANNOT OPERATE OUTSIDE OF USE FOR MCTS (for now)
     * @param imYellow if this plays for yellow
     * @param playForOpponent if this picks opponent moves instead (for MCTS algo)
     */
    public RandomLargeCardPlayer(boolean imYellow, boolean playForOpponent) {
        this.imYellow = imYellow;
        this.playForOpponent = playForOpponent;
    }

    @Override
    public Move chooseMove(GameState gameState) throws IOException, InvalidEndTurnStateException {
        ArrayList<Move> moveList;
        if (!playForOpponent) {moveList = getAllMyMoves(gameState, imYellow);} 
        else {moveList = getAllOpponentMoves(gameState, imYellow);}
        if (moveList.size()<15) {return moveList.get((int) Math.floor(Math.random()*moveList.size()));}

        Card chosenCard = weightedRandomCard(gameState);

        int startIndex = -1;
        int endIndex = moveList.size();
        for (int i=0; i<moveList.size(); i++) {
            if (startIndex == -1 && moveList.get(i).getBaseCard() == chosenCard) {startIndex = i;}
            if (endIndex > i && startIndex != -1 && moveList.get(i).getBaseCard() != chosenCard) {
                endIndex = i;
                break;
            }
        }
        if (startIndex == -1) {startIndex = endIndex;}
        return moveList.get((int) Math.floor(Math.random()*(endIndex-startIndex)) + startIndex);
    }

    private Card weightedRandomCard(GameState gameState) {
        ArrayList<Card> cards;
        if (imYellow != playForOpponent) {cards = gameState.getYellowHand();}
        else {cards = gameState.getBlueHand();}
        if (playForOpponent) {
            if (!imYellow) {cards.addAll(gameState.getYellowDeck());}
            else {cards.addAll(gameState.getBlueDeck());}
        }

        int sumSquares = 0;
        for (Card card : cards) {
            sumSquares += Math.pow(2, card.getSize());
        }
        int squaredIndex = (int) Math.floor(Math.random()*sumSquares);
        for (Card card : cards) {
            squaredIndex -= Math.pow(2, card.getSize());
            if (squaredIndex < 0) {return card;}
        }
        return cards.get(0); // insurnace safety net, shouldnt be reachable
    }

    @Override
    public Duration getTimeTaken() {
        throw new UnsupportedOperationException("This algo is not intended for decision making on its own.");
    }

    @Override
    public void setTimeAllowed(Duration timeAllowed) {
        throw new UnsupportedOperationException("This algo is not intended for decision making on its own.");
    }

}
