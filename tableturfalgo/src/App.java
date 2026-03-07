public class App {
    public static void main(String[] args) throws Exception {
        Player yellowPlayer = new RandomDummyPlayer();
        Player bluePlayer = new RandomDummyPlayer();
        new GameMaster(yellowPlayer, bluePlayer);
    }
}

// TODO: figure out how to track stats uuhhhhhhhhhhhhhhh