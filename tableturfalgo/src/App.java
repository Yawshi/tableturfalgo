public class App {
    public static void main(String[] args) throws Exception {
        String cardString = """
                10101
                10101
                10101
                12111
                """;
        Card card = new Card(cardString, 5);

        Board board = new Board();

        Move move = new Move(board, card, 5, 18, false, true);
        System.out.println(move);
    }
}
