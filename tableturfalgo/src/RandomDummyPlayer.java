import java.util.ArrayList;

/**
 * This {@code Player} picks a random move out of its available move each turn.
 */
public class RandomDummyPlayer extends Player {
    private boolean imYellow;

    public RandomDummyPlayer(boolean imYellow) {
        this.imYellow = imYellow;
    }

    @Override
    public Move chooseMove(GameState gameState) {
        ArrayList<Move> moveList = getAllMyMoves(gameState, imYellow);
        return moveList.get((int) Math.floor(Math.random()*moveList.size()));
    }
}
