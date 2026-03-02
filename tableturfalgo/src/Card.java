/**
 * Representation of a single card, in hand or in deck.<br>
 * <br>
 * Getters: <br>
 * > getPattern()<br>
 * > getPermanent()<br>
 * > getSize()<br>
 * > getSpecialCost()<br>
 * > getWidth()<br>
 * > getHeight()<br>
 * <br>
 * Methods:<br>
 * > toString()<br>
 * > rotateCard()
 */
public class Card {
    private boolean[][] pattern;
    private boolean[][] permanent;
    private int size, specialCost, width, height;

    /**
     * Create a card with the provided ink pattern and special cost.
     * @param input String representation of ink pattern, with a '2' as the special space, '1's as regular ink spaces, and '0's as empty spaces
     * @param specialCost special cost of the card
     */
    public Card(String input, int specialCost) {
        this.specialCost = specialCost;
        String[] inputStrings = input.split("\n");
        this.height = inputStrings.length;
        this.width = inputStrings[0].length();

        this.pattern = new boolean[width][height];
        this.permanent = new boolean[width][height];
        this.size = 0;
        for (int r=0; r<height; r++) {
            for (int c=0; c<width; c++) {
                switch (inputStrings[r].charAt(c)) {
                    case '2':
                        permanent[c][r] = true;
                        pattern[c][r] = true;
                        size += 1;
                        break;
                    case '1':
                        permanent[c][r] = false;
                        pattern[c][r] = true;
                        size += 1;
                        break;
                    default:
                        permanent[c][r] = false;
                        pattern[c][r] = false;
                        break;
                }
            }
        }
    }

    public String toString() {
        String output = "";
        for (int r=0; r<height; r++) {
            for (int c=0; c<width; c++) {
                if (pattern[c][r]) {
                    if (permanent[c][r]) {output += '2';}
                    else {output += '1';}
                }
                else {output += '0';}
            }
            output += "\n";
        }
        return output;
    }

    public boolean[][] getPattern() {
        return pattern;
    }

    public boolean[][] getPermanent() {
        return permanent;
    }

    public int getSize() {
        return size;
    }

    public int getSpecialCost() {
        return specialCost;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Rotate a card's ink pattern by clockwise 90'.
     * @return a new instance of the rotated card
     */
    public Card rotateCard() {
        String rotated = "";
        for (int c=0; c<width; c++) {
            for (int r=height-1; r>=0; r--) {
                if (pattern[c][r]) {
                    if (permanent[c][r]) {rotated += '2';}
                    else {rotated += '1';}
                }
                else {rotated += '0';}
            }
            rotated += "\n";
        }
        return new Card(rotated, this.specialCost);
    }

    /**
     * Rotate a card's ink pattern by a specified amount.
     * @param repeat rotate the card by clockwise 90' * {@Code repeat}, a negative amount rotates it anticlockwise instead
     * @return a new instance of the rotated card
     */
    public Card rotateCard(int repeat) {
        repeat = repeat % 4;
        if (repeat < 0) {repeat = (repeat+4)%4;}
        
        if (repeat == 0) {return this;}
        else {
            if (repeat == 1) {return rotateCard();}
            else {return rotateCard().rotateCard(repeat-1);}
        }
    }
}
