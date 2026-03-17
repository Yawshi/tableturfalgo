import java.io.IOException;
import java.util.ArrayList;

/**
 * When created, runs a full game of Tableturf, played with the two specified game agents. <br>
 * Prints game stats to a log file named "gameend.txt".
 */
public class GameMaster {
    private GameState game;
    Player yellowPlayer, bluePlayer;

    public GameMaster(Player yellowPlayer, Player bluePlayer, String timestamp) throws IOException, InvalidEndTurnStateException {
        ArrayList<Card> deck = makeStarterDeck();
        ArrayList<Card> yellowDeck = (ArrayList<Card>) deck.clone();
        ArrayList<Card> blueDeck = (ArrayList<Card>) deck.clone();
        game = new GameState(yellowDeck, blueDeck);

        System.out.println(game);

        while (game.getTurnsRemaining() > 0) {
            game.setYellowMove(yellowPlayer.chooseMove(game));
            game.setBlueMove(bluePlayer.chooseMove(game));
            game.endTurn();
            System.out.println(game);
        }

        String msg = timestamp + ": " + game.getBoard().getYellowOccupied() + " - " + game.getBoard().getBlueOccupied();
        WriteToLog.write("gameend.txt", msg);
    }

    private ArrayList<Card> makeStarterDeck() {
        ArrayList<Card> cardList = new ArrayList<>();

        String splattershotString = """
                1120
                1111
                1000
                """;
        Card splattershot = new Card(splattershotString, 3);
        cardList.add(splattershot);

        String slosherString = """
                100
                021
                111
                """;
        Card slosher = new Card(slosherString, 3);
        cardList.add(slosher);

        String zapfishString = """
                0001
                0110
                0121
                1011
                """;
        Card zapfish = new Card(zapfishString, 4);
        cardList.add(zapfish);

        String blasterString = """
                10012
                01111
                01000
                """;
        Card blaster = new Card(blasterString, 3);
        cardList.add(blaster);

        String dualiesString = """
                01111
                01200
                11000
                """;
        Card dualies = new Card(dualiesString, 3);
        cardList.add(dualies);

        String flooderString = """
                12111
                10101
                10101
                10101
                """;
        Card flooder = new Card(flooderString, 5);
        cardList.add(flooder);

        String rollerString = """
                11211
                00111
                00100
                """;
        Card roller = new Card(rollerString, 4);
        cardList.add(roller);

        String stringerString = """
                12111
                10100
                11000
                10000
                """;
        Card stringer = new Card(stringerString, 4);
        cardList.add(stringer);

        String chumString = """
                010
                210
                011
                """;
        Card chum = new Card(chumString, 2);
        cardList.add(chum);

        String chargerString = """
                1111111
                0020000
                """;
        Card charger = new Card(chargerString, 3);
        cardList.add(charger);

        String wiperString = """
                21111
                """;
        Card wiper = new Card(wiperString, 2);
        cardList.add(wiper);

        String squidforceString = """
                00100
                00100
                11111
                00120
                00100
                """;
        Card squidforce = new Card(squidforceString, 4);
        cardList.add(squidforce);

        String splatlingString = """
                11000
                11000
                11000
                01100
                00120
                00011
                """;
        Card splatling = new Card(splatlingString, 5);
        cardList.add(splatling);

        String bombString = """
                02
                11
                """;
        Card bomb = new Card(bombString, 1);
        cardList.add(bomb);

        String marigoldString = """
                00100
                01110
                10101
                11211
                01110
                """;
        Card marigold = new Card(marigoldString, 5);
        cardList.add(marigold);
        
        ArrayList<Card> deck = new ArrayList<Card>();
        for (int i=14; i>=0; i--) {
            deck.add(cardList.remove((int) Math.floor(Math.random()*i)));
        }
        return deck;
    }

}
