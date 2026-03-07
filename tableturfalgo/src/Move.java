/**
 * Encode a move ie a card's ink pattern placed on the board.<br>
 * The ink pattern is stored as a mask over the board, for future operations.<br>
 * Note that the {@code Move}'s special cost is the card's special cost if the move is a special attack and 0 otherwise.<br>
 * <br>
 * Getters: <br>
 * getInk()<br>
 * getPermanent()<br>
 * isYellow()<br>
 * isSpecial()<br>
 * getSize()<br>
 * getCard()<br>
 * getBaseCard()
 */
public class Move {
    boolean[][] ink = new boolean[9][26];
    boolean[][] permanent = new boolean[9][26];
    boolean isSpecial;
    boolean isYellow;
    int size;
    Card card, baseCard;

    /**
     * A null (pass) move. The "mask" is empty since it makes no changes to the board.
     * @param isYellow true if the move is played by yellow
     */
    public Move(Card card, boolean isYellow) {
        this.isSpecial = false;
        this.isYellow = isYellow;
        this.size = 0;
        this.card = card;
        this.baseCard = card;
    }

    /**
     * A move placed on {@code board}, using {@code card} with the bottom left corner of the card's bounding box at the specified tile. <br>
     * This throws an {@code InvalidMoveException} if the move is invalid. This could be one of five reasons: <br>
     * > The ink pattern of the card goes out of bounds with the specified placement.<br>
     * > The ink pattern of the card overlaps a regular tile and is not a special attack.<br>
     * > The ink pattern of the card overlaps a special/permanent tile.<br>
     * > If the move is a special attack, the ink pattern of the card is not adjacent to an occupied special tile.<br>
     * > If the move is not a special attack, the ink pattern of the card is not adjacent to an occupied tile.<br>
     * The violating cause is outlined in the error message thrown.
     * @param board game board to be used
     * @param card card used this move
     * @param column column index (0-8) of the tile where the bottom left corner of the card's bounding box is placed
     * @param row row index (0-25) of the tile where the bottom left corner of the card's bounding box is placed
     * @param isSpecial true if the move is a special attack
     * @param isYellow true if the move is played by yellow
     * @param baseCard pointer to unmodified card in hand
     * @throws InvalidMoveException if the move is invalid
     */
    public Move(Board board, Card card, int column, int row, boolean isSpecial, boolean isYellow, Card baseCard) throws InvalidMoveException {
        // check if the ink pattern is out of bounds
        if ((column+card.getWidth() > 9) || (row+card.getHeight() > 26)) {
            throw new InvalidMoveException("Ink pattern placed is out of bounds.");
        }

        // check if the ink pattern overlaps anything illegal (and while iterating store info on the move)
        for (int r=0; r<card.getHeight(); r++) {
            for (int c=0; c<card.getWidth(); c++) {
                if (card.getPattern()[c][r] && !board.getEmpty()[c+column][r+row]) {
                    if (!isSpecial) {
                        throw new InvalidMoveException("The move is not a special move and the ink pattern placed overlaps a regular tile.");
                    } else {
                        if (board.getPermanent()[c+column][r+row]) {
                            throw new InvalidMoveException("Ink pattern placed overlaps a special/permanent tile.");
                        }
                    }
                }
                this.ink[c+column][r+row] = card.getPattern()[c][r];
                this.permanent[c+column][r+row] = card.getPermanent()[c][r];
            }
        }
        this.isSpecial = isSpecial;
        this.isYellow = isYellow;
        this.size = card.getSize();
        this.card = card;
        this.baseCard = baseCard;

        boolean[][] boardMyColour;
        if (isYellow) {boardMyColour = board.getYellow();}
        else {boardMyColour = board.getBlue();}

        // if the ink pattern is a special move, check if the ink pattern is adjacent to a special tile
        if (isSpecial) {
            boolean flagAdjacent = false;
            // draw a bounding box around the dimensions of the card plus 1 padding on all four edges, check for special tiles only in that box
            for (int r=row-1; r<=row+card.getHeight()+1; r++) {
                if (r<0 || 26<=r) {continue;}
                for (int c=column-1; c<=column+card.getWidth()+1; c++) {
                    if (c<0 || 9<=c) {continue;}
                    if (boardMyColour[c][r] && board.getPermanent()[c][r] && anyAdjacentTiles(c, r, this.ink)) {
                        // if any are adjacent, check is passed and break out of the loops
                        flagAdjacent = true;
                        break;
                    }
                }
                if (flagAdjacent) {break;}
            }
            if (!flagAdjacent) {
                throw new InvalidMoveException("Special move's ink pattern is not adjacent to an occupied special tile.");
            }
        }
        // if the ink pattern is not a special move, check if the ink pattern is adjacent to a regular tile
        else {
            boolean flagAdjacent = false;
            // draw a bounding box around the dimensions of the card plus 1 padding on all four edges, check for occupied tiles only in that box
            for (int r=row-1; r<=row+card.getHeight()+1; r++) {
                if (r<0 || 26<=r) {continue;}
                for (int c=column-1; c<=column+card.getWidth()+1; c++) {
                    if (c<0 || 9<=c) {continue;}
                    if (boardMyColour[c][r] && anyAdjacentTiles(c, r, this.ink)) {
                        // if any are adjacent, check is passed and break out of the loops
                        flagAdjacent = true;
                        break;
                    }
                }
                if (flagAdjacent) {break;}
            }
            if (!flagAdjacent) {
                throw new InvalidMoveException("Move's ink pattern is not adjacent to an occupied tile.");
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

    public String toString() {
        String output = "";
        for (int r=0; r<26; r++) {
            for (int c=0; c<9; c++) {
                int space = 0;
                if (isYellow && ink[c][r]) {space += 1;} 
                if (!isYellow && ink[c][r]) {space += 2;}
                if (permanent[c][r]) {space += 4;}
                output += space;
            }
            output += "\n";
        }
        return output;
    }

    public boolean[][] getInk() {
        return this.ink;
    }

    public boolean[][] getPermanent() {
        return this.permanent;
    }

    public boolean isYellow() {
        return this.isYellow;
    }

    public boolean isSpecial(){
        return this.isSpecial;
    }

    public int getSize() {
        return this.size;
    }

    public Card getCard() {
        return this.card;
    }

    public Card getBaseCard() {
        return this.baseCard;
    }
}
