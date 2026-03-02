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
}
