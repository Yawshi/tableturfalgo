import java.util.ArrayList;

/**
 * A node in a MCTS algorithm search tree.
 */
public class MonteCarloNode {
    private GameState state;
    private MonteCarloNode parent;
    private ArrayList<MonteCarloNode> childrenNodes;
    private ArrayList<Move> childrenMoves, unvisitedMoves;
    private boolean isYellow;
    private int wins, visits;
    private float UCBconst;

    /**
     * Generate a new child node from a parent.
     * @param algo the MCTS algorithm in use
     * @param gameState the game state at this node
     * @param parent the parent node to this
     * @param UCBconst constant for calculating Upper Confidence Bounds (typically sqrt(2))
     */
    public MonteCarloNode(MonteCarloAlgo algo, GameState gameState, MonteCarloNode parent, float UCBconst) {
        this.state = gameState;
        this.parent = parent;
        this.isYellow = !parent.isYellow();
        this.childrenNodes = new ArrayList<MonteCarloNode>();
        this.childrenMoves = new ArrayList<Move>();
        this.wins = 0;
        this.visits = 0;
        this.UCBconst = UCBconst;

        if (gameState.getTurnsRemaining() != 0) {
            if (!this.isYellow == algo.isYellow()) {this.unvisitedMoves = algo.getAllMyMoves(gameState, algo.isYellow());}
            else {this.unvisitedMoves = algo.getAllOpponentMoves(gameState, algo.isYellow());}
        }
    }

    /**
     * Generate a fresh node. It has no parent (getParent returns null).
     * @param algo the MCTS algorithm in use
     * @param gameState the game state at this node
     * @param UCBconst constant for calculating Upper Confidence Bounds (typically sqrt(2))
     */
    public MonteCarloNode(MonteCarloAlgo algo, GameState gameState, float UCBconst) {
        this.state = gameState;
        this.parent = null;
        this.isYellow = !algo.isYellow();
        this.childrenNodes = new ArrayList<MonteCarloNode>();
        this.childrenMoves = new ArrayList<Move>();
        this.wins = 0;
        this.visits = 0;
        this.UCBconst = UCBconst;
        this.unvisitedMoves = algo.getAllMyMoves(gameState, algo.isYellow());
    }

    /**
     * Calculates and returns the Upper Confidence Bounds for this node.
     * @return UCB value
     */
    public float getUCB() {
        if (this.visits == 0) {return Float.MAX_VALUE;}
        else {return (float) ((this.wins/this.visits) + (this.UCBconst * Math.sqrt(Math.log(parent.visits)/this.visits)));}
    } 

    /**
     * Calculates what the Upper Confidence Bounds would be for a new child node from this, without creating any child nodes.
     * @return UCB of a new child node
     */
    public float getUCBofEmptyChildNode() {
        return (float) (this.UCBconst * Math.sqrt(this.visits));
    }

    public GameState getGameState() {
        return this.state;
    }

    public MonteCarloNode getParent() {
        return this.parent;
    }

    public ArrayList<MonteCarloNode> getChildrenNodes() {
        return this.childrenNodes;
    }

    public ArrayList<Move> getChildrenMoves() {
        return this.childrenMoves;
    }

    /**
     * Adds the given info to the list of children of this (the parent).
     * @param childNode a child node
     * @param childMove move leading from this (the parent) to the child node
     */
    public void addChild(MonteCarloNode childNode, Move childMove) {
        this.childrenNodes.add(childNode);
        this.childrenMoves.add(childMove);
    }

    /**
     * Pops and returns a move from the list of unvisited moves from this node. The popped move is removed from the list but not added to any other lists.
     * @param popRandom if true, pops a random move from the list, otherwise pops the first element
     * @return a move from the list
     */
    public Move popUnvisitedMove(boolean popRandom) {
        int index = 0;
        if (popRandom) {index = (int) Math.floor(Math.random()*this.unvisitedMoves.size());}
        return this.unvisitedMoves.remove(index);
    }

    public boolean hasUnvisitedMoves() {
        return this.unvisitedMoves.size() > 0;
    }

    public boolean isYellow() {
        return this.isYellow;
    }

    /**
     * Increments wins counter at this node by 1.
     */
    public void incrementWins() {
        this.wins += 1;
    }

    /**
     * Increments visits counter at this node by 1.
     */
    public void incrementVisits() {
        this.visits += 1;
    }

    public int getVisits() {
        return this.visits;
    }
}
