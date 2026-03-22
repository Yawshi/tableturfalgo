import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Play agent based on the alpha-beta (minimax) search algorithm. Writes stats about its operations to log file "alphabeta.txt". <br>
 * The evaluation function (called at a terminal game state) is simply the difference in the number of occupied spaces of both players. <br>
 * The heuristic function has 4 features: <br>
 * - difference in number of occupied spaces of both players, always weighted at 1 <br>
 * - difference in number of special points held by both players, weight variable <br>
 * - difference in rank of furthest occupied space reached by both players, weight variable <br>
 * - difference in rank of furthest occupied special space reached by both players, weight variable <br> <br>
 * (difference is positive if this player has a higher amount than the opponent, negative otherwise; <br>
 * rank is the number of rows away from a player's side of the board, starting at 1 on the player's side up to 26)
 */
public class AlphaBetaAlgo extends Player {
    private boolean imYellow;
    private String timestamp;
    private int explorationDepth;
    private float heuristicSpecialHeld, heuristicFurthestOccupied, heuristicFurthestSpecial;
    private Duration timeTakenThisTurn;
    private int statNodes, statHeuristic, statEval;

    /**
     * Initialises an alpha-beta play agent.
     * @param imYellow if this plays for yellow
     * @param timestamp for identifying this game in log files ("alphabeta.txt" for this)
     * @param maxExplorationDepth maximum amount of turns the search tree extends to, 1 being 1 turn (ie this player's move, then the opponent move, then cutoff)
     * @param heuristicSpecialHeld weight (scalar multiplier) for "special points held" feature in heuristic function
     * @param heuristicFurthestOccupied weight (scalar multiplier) for "rank of furthest occupied tile" feature in heuristic function
     * @param heuristicFurthestSpecial weight (scalar multiplier) for "rank of furthest occupied special tile" feature in heuristic function
     */
    public AlphaBetaAlgo(boolean imYellow, String timestamp, int maxExplorationDepth, float heuristicSpecialHeld, float heuristicFurthestOccupied, float heuristicFurthestSpecial) {
        this.imYellow = imYellow;
        this.timestamp = timestamp;
        this.explorationDepth = maxExplorationDepth;
        this.heuristicSpecialHeld = heuristicSpecialHeld;
        this.heuristicFurthestOccupied = heuristicFurthestOccupied;
        this.heuristicFurthestSpecial = heuristicFurthestSpecial;
    }

    @Override
    public Move chooseMove(GameState gameState) throws IOException, InvalidEndTurnStateException {
        Instant startTimestamp = Instant.now();

        GameState rootState = new GameState(gameState);
        rootState.clearMoves();
        ArrayList<Move> moveList = this.getAllMyMoves(rootState, imYellow);
        float value = -100;
        Move output = null;
        this.statNodes = 1;
        this.statHeuristic = 0;
        this.statEval = 0;

        for (Move move : moveList) {
            GameState minNode = new GameState(rootState);
            if (imYellow) {minNode.setYellowMove(move);}
            else {minNode.setBlueMove(move);}
            
            float nodeValue = minFunc(minNode, 0, -100, 100);
            if (value < nodeValue) {
                value = nodeValue;
                output = move;
            }
        }

        timeTakenThisTurn = Duration.between(startTimestamp, Instant.now());
        String msg = timestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += rootState.getTurnsRemaining();
        msg += " heur=" + statHeuristic;
        msg += " eval=" + statEval;
        msg += " nodes=" + statNodes;
        msg += " moveList=" + moveList.size();
        msg += " duration=" + timeTakenThisTurn.toMillis() + "ms";
        WriteToLog.write("alphabeta.txt", msg);

        return output;
    }

    private float maxFunc(GameState maxState, int depth, float alpha, float beta) throws InvalidEndTurnStateException {
        ArrayList<Move> moveList = this.getAllMyMoves(maxState, imYellow);
        float value = -100;
        this.statNodes += 1;

        for (Move move : moveList) {
            GameState minNode = new GameState(maxState);
            if (imYellow) {minNode.setYellowMove(move);}
            else {minNode.setBlueMove(move);}
            
            float nodeValue = minFunc(minNode, depth, alpha, beta);
            if (value < nodeValue) {
                value = nodeValue;
                if (value > alpha) {alpha = value;}
            }
            if (value >= beta) {
                return value;}
        }
        return value;
    }

    private float minFunc(GameState minState, int depth, float alpha, float beta) throws InvalidEndTurnStateException {
        ArrayList<Move> moveList = this.getAllOpponentMoves(minState, imYellow);
        float value = 100;
        this.statNodes += 1;

        for (Move move : moveList) {
            GameState endNode = new GameState(minState);
            if (imYellow) {endNode.setBlueMove(move);}
            else {endNode.setYellowMove(move);}

            float nodeValue = endFunc(endNode, depth, alpha, beta);
            if (value > nodeValue) {
                value = nodeValue;
                if (value < beta) {beta = value;}
            }
            if (value <= alpha) {return value;}
        }
        return value;
    }

    private float endFunc(GameState endState, int depth, float alpha, float beta) throws InvalidEndTurnStateException {
        endState.endTurn(false);

        // check if this is a terminating node on the tree
        if (endState.getTurnsRemaining() == 0) {
            this.statEval += 1;
            float value = endState.getBoard().getYellowOccupied() - endState.getBoard().getBlueOccupied();
            if (!imYellow) {value *= -1;}
            return value;
        } 
        depth += 1;
        if (depth >= this.explorationDepth) {
            this.statHeuristic += 1;
            float value = endState.getBoard().getYellowOccupied() - endState.getBoard().getBlueOccupied();
            value += this.heuristicSpecialHeld * (endState.getYellowSpecialsHeld() - endState.getBlueSpecialsHeld());
            value += this.heuristicFurthestOccupied * (boardFurthestRank(endState.getBoard(), true, false) - boardFurthestRank(endState.getBoard(), false, false));
            value += this.heuristicFurthestSpecial * (boardFurthestRank(endState.getBoard(), true, true) - boardFurthestRank(endState.getBoard(), false, true));
            if (!imYellow) {value *= -1;}
            return value;
        }
        
        // if not generate new max nodes
        this.statNodes += 1;
        ArrayList<Card> myDeck;
        if (imYellow) {myDeck = endState.getYellowDeck();}
        else {myDeck = endState.getBlueDeck();}
        float value = 0;

        for (Card card : myDeck) {
            GameState maxNode = new GameState(endState);
            maxNode.drawCardFromDeck(card, imYellow);
            value += maxFunc(maxNode, depth, alpha, beta);
        }

        return value/myDeck.size();
    }

    private int boardFurthestRank(Board board, boolean forYellow, boolean isPermanent) {
        boolean[][] checkAgainst;
        if (forYellow) {checkAgainst = board.getYellow();}
        else {checkAgainst = board.getBlue();}

        for (int i=0; i<26; i++) {
            int r = i;
            if (!forYellow) {r = 25-i;}
            for (int c=0; c<9; c++) {
                if (checkAgainst[c][r]) {
                    if (!isPermanent || board.getPermanent()[c][r]) {
                        return 25-i;
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public Duration getTimeTaken() {
        return timeTakenThisTurn;
    }

    @Override
    public void setTimeAllowed(Duration timeAllowed) {
        // does nothing, this algo has no time limit mechanism
    }
}
