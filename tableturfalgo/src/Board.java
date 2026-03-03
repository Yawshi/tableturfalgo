import java.util.ArrayList;

public class Board {
    private boolean[][] yellow = new boolean[9][26];
    private boolean[][] blue = new boolean[9][26];
    private boolean[][] permanent = new boolean[9][26];
    private boolean[][] empty = new boolean[9][26];

    /**
     * Generate a fresh board for the start of a new game.
     */
    public Board() {
        yellow[4][22] = true;
        permanent[4][22] = true;
        blue[4][3] = true;
        permanent[4][3] = true;
        updateEmpty();
    }

    public String toString() {
        String output = "";
        for (int r=0; r<26; r++) {
            for (int c=0; c<9; c++) {
                int space = 0;
                if (yellow[c][r]) {space += 1;} 
                if (blue[c][r]) {space += 2;}
                if (permanent[c][r]) {space += 4;}
                output += space;
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

    /**
     * Searches and returns a list of moves using the specified card.
     * @param card the card to search for moves with
     * @param isSpecial if true return only special attack moves, otherwise return only regular moves
     * @param isYellow if true return moves for yellow, otherwise return moves for blue
     * @return a list of legal moves using the card
     * @see Move#Move()
     */
    public ArrayList<Move> getMoves(Card card, boolean isSpecial, boolean isYellow) {
        ArrayList<Move> output = new ArrayList<Move>();
        for (int x=0; x<4; x++) {
            // scan board and look for moves from this rotation
            for (int r=0; r<26-card.getHeight()+1; r++) {
                for (int c=0; c<9-card.getWidth()+1; c++) {
                    try {
                        output.add(new Move(this, card, c, r, isSpecial, isYellow));
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
}
