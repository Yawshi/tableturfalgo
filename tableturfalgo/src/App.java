public class App {
    public static void main(String[] args) throws Exception {
        String cardString = """
                10101
                10101
                10101
                12111
                """;
        Card card = new Card(cardString, 5);
        System.out.println(card);
        card = card.rotateCard();
        System.out.println(card);
        card = card.rotateCard();
        System.out.println(card);
        card = card.rotateCard();
        System.out.println(card);
        card = card.rotateCard();
        System.out.println(card);
    }
}
