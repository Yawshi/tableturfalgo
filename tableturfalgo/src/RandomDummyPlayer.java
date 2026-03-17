import java.util.ArrayList;

/**
 * This {@code Player} picks a random move out of its available moves each turn.
 */
public class RandomDummyPlayer extends Player {
    private boolean imYellow, playForOpponent;

    public RandomDummyPlayer(boolean imYellow, boolean playForOpponent) {
        this.imYellow = imYellow;
        this.playForOpponent = playForOpponent;
    }

    @Override
    public Move chooseMove(GameState gameState) {
        if (!playForOpponent) {
            return chooseMove(this.getAllMyMoves(gameState, imYellow));
        } else {
            return chooseMove(this.getAllOpponentMoves(gameState, imYellow));
        }
        
    }

    /**
     * Picks a random move out of the provided list of moves.
     * @param moveList list of moves to pick from
     * @return a random move from the list
     */
    public Move chooseMove(ArrayList<Move> moveList) {
        return moveList.get((int) Math.floor(Math.random()*moveList.size()));
    }
}
