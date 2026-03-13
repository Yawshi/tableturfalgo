import java.util.ArrayList;

/**
 * Representation of the board. Each space on the board is stored as three booleans: <br>
 * - {@code permanent} the space is either a special space or a grey clash wall <br>
 * - {@code blue} the space is occupied by the blue player <br>
 * - {@code yellow} the space is occupied by the yellow player <br>
 * Each of these booleans are stored in separate 2D arrays to be interpreted and manipulated.
 * When printed, each space is interpreted as a 3 digit binary, with {@code permanent} worth 4 and {@code yellow} worth 1. <br>
 * <br>
 * Getters:<br>
 * getYellow()<br>
 * getBlue()<br>
 * getPermanent()<br>
 * getEmpty()<br>
 * getYellowSpecials()<br>
 * getBlueSpecials()<br>
 * getYellowOccupied()<br>
 * getBlueOccupied()<br>
 * <br>
 * Methods:<br>
 * getMoves()<br>
 * updateBoard()
 */
public class Board {
    private boolean[][] yellow = new boolean[9][26];
    private boolean[][] blue = new boolean[9][26];
    private boolean[][] permanent = new boolean[9][26];
    private boolean[][] empty = new boolean[9][26];
    private int yellowSpecialsActivated, blueSpecialsActivated, yellowOccupiedCount, blueOccupiedCount;
    private int yellowTurnsPassed = 0, blueTurnsPassed = 0;

    /**
     * Generate a fresh board for the start of a new game.
     */
    public Board() {
        yellow[4][22] = true;
        permanent[4][22] = true;
        blue[4][3] = true;
        permanent[4][3] = true;
        updateEmpty();
        yellowSpecialsActivated = 0;
        blueSpecialsActivated = 0;
        yellowOccupiedCount = 1;
        blueOccupiedCount = 1;
    }

    /**
     * Generate a clone of the given board.
     * @param board board to be cloned
     */
    public Board(Board board) {
        for (int c=0; c<9; c++) {
            this.yellow[c] = board.getYellow()[c].clone();
            this.blue[c] = board.getBlue()[c].clone();
            this.permanent[c] = board.getPermanent()[c].clone();
        }
        updateEmpty();
        this.yellowSpecialsActivated = board.yellowSpecialsActivated;
        this.blueSpecialsActivated = board.blueSpecialsActivated;
        this.yellowOccupiedCount = board.yellowOccupiedCount;
        this.blueOccupiedCount = board.blueOccupiedCount;
        this.yellowTurnsPassed = board.yellowTurnsPassed;
        this.blueTurnsPassed = board.blueTurnsPassed;
    }

    public String toString() {
        String output = "";
        for (int r=0; r<26; r++) {
            for (int c=0; c<9; c++) {
                int space = 0;
                if (yellow[c][r]) {space += 1;} 
                if (blue[c][r]) {space += 2;}
                if (permanent[c][r]) {space += 4;}
                if (space == 0) {output += "-";}
                else {output += space;}
            }
            output += "\n";
        }
        return output;
    }

    /**
     * Update the empty field for getEmpty(). Run this at the end of resolving a turn.
     */
    public void updateEmpty() {
        for (int r=0; r<26; r++) {
            for (int c=0; c<9; c++) {
                this.empty[c][r] = !(this.yellow[c][r] || this.blue[c][r] || this.permanent[c][r]);
            }
        }
    }

    public boolean[][] getYellow() {
        return this.yellow;
    }

    public boolean[][] getBlue() {
        return this.blue;
    }

    public boolean[][] getPermanent() {
        return this.permanent;
    }

    public boolean[][] getEmpty() {
        return this.empty;
    }

    public int getYellowSpecials() {
        return this.yellowSpecialsActivated + this.yellowTurnsPassed;
    }

    public int getBlueSpecials() {
        return this.blueSpecialsActivated + this.blueTurnsPassed;
    }

    public int getYellowOccupied() {
        return this.yellowOccupiedCount;
    }

    public int getBlueOccupied() {
        return this.blueOccupiedCount;
    }

    /**
     * Searches and returns a list of moves using the specified card.
     * @param card the card to search for moves with
     * @param isSpecial if true return only special attack moves, otherwise return only regular moves
     * @param isYellow if true return moves for yellow, otherwise return moves for blue
     * @return a list of legal moves using the card
     * @see Move#Move()
     */
    public ArrayList<Move> getMoves(Card card, boolean isSpecial, boolean isYellow) {
        Card baseCard = card;
        ArrayList<Move> output = new ArrayList<Move>();
        for (int x=0; x<4; x++) {
            // scan board and look for moves from this rotation
            for (int i=0; i<26-card.getHeight()+1; i++) {
                int r = i;
                if (!isYellow) {r = 26-card.getHeight() - i;}
                for (int c=0; c<9-card.getWidth()+1; c++) {
                    try {
                        output.add(new Move(this, card, c, r, isSpecial, isYellow, baseCard));
                    } catch (InvalidMoveException e) {
                        continue;
                    }
                }
            }
            // then rotate and repeat
            card = card.rotateCard();
        }
        return output;
    }

    /**
     * Resolves actions from both players at the end of a turn and updates the board and all info this object holds.
     * @param yellowMove move from the yellow player
     * @param blueMove move from the blue player
     */
    public void updateBoard(Move yellowMove, Move blueMove) {
        if (yellowMove.getSize() == 0) {
            yellowTurnsPassed += 1;
        }
        if (blueMove.getSize() == 0) {
            blueTurnsPassed += 1;
        }
        if (yellowMove.getSize() == blueMove.getSize()) {
            for (int r=0; r<26; r++) {
                for (int c=0; c<9 ; c++) {
                    if (yellowMove.getPermanent()[c][r]) {
                        this.yellow[c][r] = !blueMove.getPermanent()[c][r];
                        this.blue[c][r] = false;
                        this.permanent[c][r] = true;
                    } else {
                        if (blueMove.getPermanent()[c][r]) {
                            this.yellow[c][r] = false;
                            this.blue[c][r] = true;
                            this.permanent[c][r] = true;
                        } else {
                            if (yellowMove.getInk()[c][r]) {
                                this.yellow[c][r] = !blueMove.getInk()[c][r];
                                this.blue[c][r] = false;
                                this.permanent[c][r] = blueMove.getInk()[c][r];
                            } else {
                                if (blueMove.getInk()[c][r]) {
                                    this.yellow[c][r] = false;
                                    this.blue[c][r] = true;
                                    this.permanent[c][r] = false;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Move smallerMove = yellowMove, largerMove = yellowMove;
            boolean yellowIsSmaller;
            if (blueMove.getSize() < yellowMove.getSize()) {
                smallerMove = blueMove;
                yellowIsSmaller = false;
            } else {
                largerMove = blueMove;
                yellowIsSmaller = true;
            }
            for (int r=0; r<26; r++) {
                for (int c=0; c<9; c++) {
                    if (smallerMove.getPermanent()[c][r]) {
                        this.yellow[c][r] = yellowIsSmaller;
                        this.blue[c][r] = !yellowIsSmaller;
                        this.permanent[c][r] = true;
                    } else {
                        if (largerMove.getPermanent()[c][r]) {
                            this.yellow[c][r] = !yellowIsSmaller;
                            this.blue[c][r] = yellowIsSmaller;
                            this.permanent[c][r] = true;
                        } else {
                            if (smallerMove.getInk()[c][r]) {
                                this.yellow[c][r] = yellowIsSmaller;
                                this.blue[c][r] = !yellowIsSmaller;
                                this.permanent[c][r] = false;
                            } else {
                                if (largerMove.getInk()[c][r]) {
                                    this.yellow[c][r] = !yellowIsSmaller;
                                    this.blue[c][r] = yellowIsSmaller;
                                    this.permanent[c][r] = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.updateEmpty();

        yellowSpecialsActivated = 0;
        blueSpecialsActivated = 0;
        yellowOccupiedCount = 0;
        blueOccupiedCount = 0;
        for (int r=0; r<26; r++) {
            for (int c=0; c<9; c++) {
                if (this.yellow[c][r]) {yellowOccupiedCount += 1;}
                if (this.blue[c][r]) {blueOccupiedCount += 1;}
                if (this.permanent[c][r] && (this.yellow[c][r] || this.blue[c][r])) {
                    if (!anyAdjacentTiles(c, r, this.empty)) {
                        if (this.yellow[c][r]) {yellowSpecialsActivated += 1;}
                        if (this.blue[c][r]) {blueSpecialsActivated += 1;}
                    }
                }
            }
        }
    }

    private boolean anyAdjacentTiles(int column, int row, boolean[][] checkAgainst) {
        for (int r=row-1; r<=row+1; r++) {
            if (r<0 || 26<=r) {continue;}
            for (int c=column-1; c<=column+1; c++) {
                if (c<0 || 9<=c) {continue;}
                if (checkAgainst[c][r]) {
                    return true;
                }
            }
        }
        return false;
    }
}
