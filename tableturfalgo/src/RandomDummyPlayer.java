import java.util.ArrayList;

/**
 * This {@code Player} picks a random move out of its available move each turn.
 */
public class RandomDummyPlayer extends Player {
    @Override
    public Move chooseMove(ArrayList<Move> moveList) {
        return moveList.get((int) Math.floor(Math.random()*moveList.size()));
    }
}
