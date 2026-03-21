import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * A play agent based on the Monte Carlo Tree Search (MCTS) algorithm. 
 * Requires defining/assigning other decision-making algorithms for simulation phase. <br>
 * Writes stats about its operations to "montecarlo.txt".
 */
public class MonteCarloAlgo extends Player {
    private boolean imYellow;
    private String gameTimestamp;
    private Duration timeAllowed;
    private float UCBconst;
    private Player mySimulationAlgo, oppSimulationAlgo;

    /**
     * Initialise a MCTS play agent.
     * @param imYellow if this plays for yellow
     * @param timestamp for identifying this game in log files ("montecarlo.txt" for this)
     * @param timeAllowed time allotted to making a decision per turn
     * @param UCBconst (optional) constant for calculating Upper Confidence Bounds in selection phase (defaults to sqrt(2))
     * @param mySimulationAlgo decision-making algorithm for simulating this player's moves in simulation phase
     * @param oppSimulationAlgo decision-making algorithm for simulating the opponent's moves in simulation phase
     */
    public MonteCarloAlgo(boolean imYellow, String timestamp, Duration timeAllowed, float UCBconst, Player mySimulationAlgo, Player oppSimulationAlgo) {
        this.imYellow = imYellow;
        this.gameTimestamp = timestamp;
        this.timeAllowed = timeAllowed;
        this.UCBconst = UCBconst;
        this.mySimulationAlgo = mySimulationAlgo;
        this.oppSimulationAlgo = oppSimulationAlgo;
    }

    /**
     * Initialise a MCTS play agent. Constant for calculating Upper Confidence Bounds in selection phase is set to the default value of sqrt(2).
     * @param imYellow if this plays for yellow
     * @param timestamp for identifying this game in log files ("montecarlo.txt" for this)
     * @param timeAllowed time allotted to making a decision per turn
     * @param mySelectionAlgo decision-making algorithm for simulating this player's moves in simulation phase
     * @param oppSelectionAlgo decision-making algorithm for simulating the opponent's moves in simulation phase
     */
    public MonteCarloAlgo(boolean imYellow, String timestamp, Duration timeAllowed, Player mySelectionAlgo, Player oppSelectionAlgo) {
        this.imYellow = imYellow;
        this.gameTimestamp = timestamp;
        this.timeAllowed = timeAllowed;
        this.UCBconst = (float) Math.sqrt(2);
        this.mySimulationAlgo = mySelectionAlgo;
        this.oppSimulationAlgo = oppSelectionAlgo;
    }

    @Override
    public Move chooseMove(GameState rootState) throws IOException, InvalidEndTurnStateException {
        MonteCarloNode rootNode = new MonteCarloNode(this, rootState, UCBconst);
        
        Instant endTimestamp = Instant.now().plus(timeAllowed);
        int statSimulations = 0;
        while (Instant.now().isBefore(endTimestamp)) {
            MonteCarloNode selectedNode = select(rootNode);
            Board simulatedResult = simulate(selectedNode);
            backprop(selectedNode, (simulatedResult.getYellowOccupied() > simulatedResult.getBlueOccupied()), (simulatedResult.getYellowOccupied() < simulatedResult.getBlueOccupied()));
            statSimulations += 1;
        }

        int mostVisits = 0;
        Move mostVisitedMove = null;
        for (int i=0; i<rootNode.getChildrenMoves().size(); i++) {
            if (rootNode.getChildrenNodes().get(i).getVisits() > mostVisits) {
                mostVisits = rootNode.getChildrenNodes().get(i).getVisits();
                mostVisitedMove = rootNode.getChildrenMoves().get(i);
            }
        }

        String msg = gameTimestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += rootNode.getGameState().getTurnsRemaining();
        msg += " plays=" + statSimulations;
        msg += " most=" + mostVisits;
        msg += " children=" + rootNode.getChildrenMoves().size();
        msg += " moveList=" + this.getAllMyMoves(rootState, imYellow).size();
        WriteToLog.write("montecarlo.txt", msg);

        return mostVisitedMove;
    }

    private MonteCarloNode select(MonteCarloNode parentNode) throws InvalidEndTurnStateException {
        if (parentNode.getGameState().getTurnsRemaining() == 0) {
            return parentNode;
        }
        if (parentNode.hasUnvisitedMoves()) {
            return expand(parentNode, parentNode.popUnvisitedMove(false));
        }
        
        float highestChildUCB = -1;
        MonteCarloNode highestChild = null;
        for (MonteCarloNode child : parentNode.getChildrenNodes()) {
            if (child.getUCB()>highestChildUCB) {
                highestChildUCB = child.getUCB();
                highestChild = child;
            }
        }
        return select(highestChild);
    }

    private MonteCarloNode expand(MonteCarloNode parentNode, Move move) throws InvalidEndTurnStateException {
        GameState childState = new GameState(parentNode.getGameState());
        if (!parentNode.isYellow()) {childState.setYellowMove(move);}
        else {childState.setBlueMove(move);}

        if (!childState.hasBothMovesAssigned()) {
            MonteCarloNode childNode = new MonteCarloNode(this, childState, parentNode, UCBconst);
            parentNode.addChild(childNode, move);
            return childNode;
        } else {
            childState.endTurn(false);
            int deckSize;
            if (this.imYellow) {deckSize = childState.getYellowDeck().size();}
            else {deckSize = childState.getBlueDeck().size();}
            if (deckSize == 0) {
                MonteCarloNode childNode = new MonteCarloNode(this, childState, parentNode, UCBconst);
                parentNode.addChild(childNode, move);
                return childNode;
            }
            // add all possible card draws as children nodes
            int r = (int) Math.floor(Math.random()*deckSize);
            MonteCarloNode output = null;
            for (int i=0; i<deckSize; i++) {
                GameState newTurnState = new GameState(childState);
                newTurnState.drawCardFromDeck(i, this.imYellow);
                MonteCarloNode newTurnNode = new MonteCarloNode(this, newTurnState, parentNode, UCBconst);
                if (i==r) {output = newTurnNode;}
                parentNode.addChild(newTurnNode, move);
            }
            return output;
        }
    }

    private Board simulate(MonteCarloNode leafNode) throws InvalidEndTurnStateException, IOException {
        GameState simulatedGame = new GameState(leafNode.getGameState());
        while (simulatedGame.getTurnsRemaining() > 0) {
            if (simulatedGame.hasBothMovesAssigned()) {
                simulatedGame.endTurn(false);
                if (simulatedGame.getTurnsRemaining() < 1) {break;}
                simulatedGame.drawRandomCardFromDeck(imYellow);
            } else {
                // simulate opponent move
                if (imYellow) {simulatedGame.setBlueMove(oppSimulationAlgo.chooseMove(simulatedGame));}
                else {simulatedGame.setYellowMove(oppSimulationAlgo.chooseMove(simulatedGame));}
                // simulate my move (if not assigned)
                if (!simulatedGame.hasBothMovesAssigned()) {
                    if (imYellow) {simulatedGame.setYellowMove(mySimulationAlgo.chooseMove(simulatedGame));}
                    else {simulatedGame.setBlueMove(mySimulationAlgo.chooseMove(simulatedGame));}
                }
            }
        }
        return simulatedGame.getBoard();
    }

    private void backprop(MonteCarloNode leafNode, boolean yellowWon, boolean blueWon) {
        MonteCarloNode pointer = leafNode;
        while (true) {
            pointer.incrementVisits();
            if (yellowWon && pointer.isYellow()) {
                pointer.incrementWins();
            }
            if (blueWon && !pointer.isYellow()) {
                pointer.incrementWins();
            }
            if (pointer.getParent() == null) {
                return;
            }
            pointer = pointer.getParent();
        }
    }

    public boolean isYellow() {
        return this.imYellow;
    }
}
