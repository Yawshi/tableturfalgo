import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        // String cErdString = """
        //         10101
        //         10101
        //         10101
        //         12111
        //         """;
        // Card cErd = new Card(cErdString, 5);

        // String cArdString = """
        //         010
        //         210
        //         110
        //         111
        //         """;
        // Card cArd = new Card(cArdString, 3);

        String dynamoString = """
                1111111
                0002111
                0001000
                0001000
                """;
        Card dynamo = new Card(dynamoString, 5);

        ArrayList<Card> yellowDeck = new ArrayList<>();
        for (int i=0; i<15; i++) {yellowDeck.add(dynamo);}

        ArrayList<Card> blueDeck = new ArrayList<>();
        for (int i=0; i<15; i++) {blueDeck.add(dynamo);}

        GameState game = new GameState(yellowDeck, blueDeck);
        System.out.println(game);

        for (int i=0; i<12; i++) {
            Move yellowMove = new Move(dynamo, true);
            game.setYellowMove(yellowMove);

            Move blueMove = game.getBoard().getMoves(dynamo, false, false).get(0);
            game.setBlueMove(blueMove);

            game.endTurn();
            System.out.println(game);
        }

        // TODO: verify if deck and hand drawing if it makes sense
    }
}
