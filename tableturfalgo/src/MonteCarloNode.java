import java.util.ArrayList;

/**
 * A node in a MCTS algorithm search tree.
 */
public class MonteCarloNode {
    private GameState state;
    private MonteCarloNode parent;
    private ArrayList<MonteCarloNode> childrenNodes;
    private ArrayList<Move> childrenMoves;
    private boolean isYellow;
    private int wins, visits;
    private float ucb, UCBconst;

    /**
     * Generate a new child node from a parent.
     * @param gameState the game state at this node
     * @param parent the parent node to this
     * @param UCBconst constant for calculating Upper Confidence Bounds (typically sqrt(2))
     */
    public MonteCarloNode(GameState gameState, MonteCarloNode parent, float UCBconst) {
        this.state = gameState;
        this.parent = parent;
        this.isYellow = !parent.isYellow();
        this.childrenNodes = new ArrayList<MonteCarloNode>();
        this.childrenMoves = new ArrayList<Move>();
        this.wins = 0;
        this.visits = 0;
        this.UCBconst = UCBconst;
    }

    /**
     * Generate a fresh node.
     * @param gameState the game state at this node
     * @param isYellow which player this node belongs to (tracks that player's wins through this node)
     * @param UCBconst constant for calculating Upper Confidence Bounds (typically sqrt(2))
     */
    public MonteCarloNode(GameState gameState, boolean isYellow, float UCBconst) {
        this.state = gameState;
        this.parent = null;
        this.isYellow = isYellow;
        this.childrenNodes = new ArrayList<MonteCarloNode>();
        this.childrenMoves = new ArrayList<Move>();
        this.wins = 0;
        this.visits = 0;
        this.UCBconst = UCBconst;
    }

    /**
     * Update the Upper Confidence Bounds for this node, for backpropagation phase.
     */
    public void updateUCB() {
        this.ucb = (float) ((this.wins/this.visits) + (this.UCBconst * Math.sqrt(Math.log(parent.visits)/this.visits)));
    }

    public float getUCB() {
        return this.ucb;
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
