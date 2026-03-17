import java.io.IOException;
import java.util.ArrayList;

public class AlphaBetaAlgo extends Player {
    private boolean imYellow;
    private String timestamp;
    private int explorationDepth;
    private float evalWeightSpecialHeld, evalWeightFurthestOccupied, evalWeightFurthestSpecial;

    public AlphaBetaAlgo(boolean imYellow, String timestamp, int maxExplorationDepth, float evalWeightSpecialHeld, float evalWeightFurthestOccupied, float evalWeightFurthestSpecial) {
        this.imYellow = imYellow;
        this.timestamp = timestamp;
        this.explorationDepth = maxExplorationDepth;
        this.evalWeightSpecialHeld = evalWeightSpecialHeld;
        this.evalWeightFurthestOccupied = evalWeightFurthestOccupied;
        this.evalWeightFurthestSpecial = evalWeightFurthestSpecial;
    }

    @Override
    public Move chooseMove(GameState gameState) throws IOException, InvalidEndTurnStateException {
        ArrayList<Move> moveList = this.getAllMyMoves(gameState, imYellow);
        float value = -100;
        Move output = null;

        String msg = timestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += gameState.getTurnsRemaining() + " depth 1/" + this.explorationDepth + " maxNode=" + moveList.size();
        WriteToLog.write("alphabeta.txt", msg);

        for (Move move : moveList) {
            GameState minNode = new GameState(gameState);
            if (imYellow) {minNode.setYellowMove(move);}
            else {minNode.setBlueMove(move);}
            
            float nodeValue = minFunc(minNode, 0, -100, 100);
            if (value < nodeValue) {
                value = nodeValue;
                output = move;
            }
        }
        return output;
    }

    private float maxFunc(GameState maxState, int depth, float alpha, float beta) throws IOException, InvalidEndTurnStateException {
        ArrayList<Move> moveList = this.getAllMyMoves(maxState, imYellow);
        float value = -100;

        String msg = timestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += maxState.getTurnsRemaining()+depth + " depth " + depth+1 + "/" + this.explorationDepth + " maxNode=" + moveList.size();
        WriteToLog.write("alphabeta.txt", msg);

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

    private float minFunc(GameState minState, int depth, float alpha, float beta) throws IOException, InvalidEndTurnStateException {
        ArrayList<Move> moveList = this.getAllOpponentMoves(minState, imYellow);
        float value = 100;

        String msg = timestamp;
        if (imYellow) {msg += " Y: t=";}
        else {msg += " B: t=";}
        msg += minState.getTurnsRemaining()+depth + " depth " + depth+1 + "/" + this.explorationDepth + " minNode=" + moveList.size();
        WriteToLog.write("alphabeta.txt", msg);

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

    private float endFunc(GameState endState, int depth, float alpha, float beta) throws IOException, InvalidEndTurnStateException {
        endState.endTurn(false);

        // check if this is a terminating node on the tree
        if (endState.getTurnsRemaining() == 0) {
            float value = endState.getBoard().getYellowOccupied() - endState.getBoard().getBlueOccupied();
            if (!imYellow) {value *= -1;}
            return value;
        } 
        depth += 1;
        if (depth >= this.explorationDepth) {
            float value = endState.getBoard().getYellowOccupied() - endState.getBoard().getBlueOccupied();
            value += this.evalWeightSpecialHeld * (endState.getYellowSpecialsHeld() - endState.getBlueSpecialsHeld());
            value += this.evalWeightFurthestOccupied * (boardFurthestRank(endState.getBoard(), true, false) - boardFurthestRank(endState.getBoard(), false, false));
            value += this.evalWeightFurthestSpecial * (boardFurthestRank(endState.getBoard(), true, true) - boardFurthestRank(endState.getBoard(), false, true));
            if (!imYellow) {value *= -1;}

            // System.out.print(endState.getBoard());
            // System.out.println(value);
            // System.out.println();

            return value;
        }
        
        // if not generate new max nodes
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
}
