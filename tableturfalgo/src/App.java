public class App {
    public static void main(String[] args) throws Exception {
        String cErdString = """
                10101
                10101
                10101
                12111
                """;
        Card cErd = new Card(cErdString, 5);

        // String cArdString = """
        //         010
        //         210
        //         110
        //         111
        //         """;
        // Card cArd = new Card(cArdString, 3);

        Board board = new Board();

        for (int i=0; i<6; i++) {
            Move yellowMove = new Move(true);
            Move blueMove = board.getMoves(cErd, false, false).get(0);
            board.updateBoard(yellowMove, blueMove);
            System.out.println(board);
            System.out.println(board.getYellowOccupied() + "(" + board.getYellowSpecials() + ") - " + board.getBlueOccupied() + "(" + board.getBlueSpecials() +")");
            System.out.println();
        }
    }
}
