public class Board {
    private boolean[][] yellow = new boolean[9][26];
    private boolean[][] blue = new boolean[9][26];
    private boolean[][] permanent = new boolean[9][26];

    public Board() {
        yellow[4][22] = true;
        permanent[4][22] = true;
        blue[4][3] = true;
        permanent[4][3] = true;
    }

    public String toString() {
        String output = "";
        for (short r=0; r<26; r++) {
            for (short c=0; c<9; c++) {
                short space = 0;
                if (yellow[c][r]) {space += 1;} 
                if (blue[c][r]) {space += 2;}
                if (permanent[c][r]) {space += 4;}
                output += space;
            }
            output += "\n";
        }
        return output;
    }
}
