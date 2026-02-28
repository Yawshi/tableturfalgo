public class Move {
    boolean[][] ink = new boolean[9][26];
    boolean[][] permanent = new boolean[9][26];
    boolean isSpecial;
    boolean isYellow;
    int size;

    public Move(Board board, Card card, int column, int row, boolean isSpecial, boolean isYellow) throws InvalidMoveException {
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

        boolean[][] boardMyColour;
        if (isYellow) {boardMyColour = board.getYellow();}
        else {boardMyColour = board.getBlue();}

        // if the ink pattern is a special move, check if the ink pattern is adjacent to a special tile
        if (isSpecial) {
            boolean flagAdjacent = false;
            for (int r=row-1; r<=row+card.getHeight()+1; r++) {
                if (r<0 || 26<r) {continue;}
                for (int c=column-1; c<=column+card.getWidth()+1; c++) {
                    if (c<0 || 9<c) {continue;}
                    if (boardMyColour[c][r] && board.getPermanent()[c][r]) {
                        // call adjacency method
                    }
                }
            }
        }
        // if the ink pattern is not a special move, check if the ink pattern is adjacent to a regular tile

    }

    // private isAdjacent(int c, int r)
    // if this.ink adjacent to board[c][r] return true else false
}
