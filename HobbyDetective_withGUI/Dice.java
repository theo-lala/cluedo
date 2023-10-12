public class Dice {
    public Dice() {
        // Constructor (currently empty)
    }

    // Produces a random number between 2 and 12 (inclusive)
    public static int rollDice() {
        int num = (int) (Math.random() * 11) + 2;
        return num;
    }
}