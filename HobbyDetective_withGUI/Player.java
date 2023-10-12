import java.util.ArrayList;
import java.util.List;

public class Player {
    int xPos;
    int yPos;
    List<Card<?>> hand;
    private Character character;

    private boolean madeIncorrectSolveAttempt;

    public boolean hasMadeIncorrectSolveAttempt() {
        return madeIncorrectSolveAttempt;
    }

    public void setMadeIncorrectSolveAttempt(boolean madeIncorrectSolveAttempt) {
        this.madeIncorrectSolveAttempt = madeIncorrectSolveAttempt;
    }

    public Player(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        hand = new ArrayList<>();
        this.madeIncorrectSolveAttempt = false;
    }

    public void addCard(Card<?> c){
        hand.add(c);
    }

    public void addCharacter(Character c){
        this.character = c;
    }

    //returns the cards in a players hand
    public List<Card<?>> getHand() {
        return hand;
    }

    public Character getCharacter(){
        return character;
    }
    
    //if a player has a card in their hand, which matches part of the guess return it
    public Card<?> canRefute(Guess guess){
        for(Card<?> c : hand){
            if(c.getName().equals(guess.getCharacter().getName()) || c.getName().equals(guess.getEstate().getName()) || c.getName().equals(guess.getWeapon().getName())){
                return c;
            }
        }
        return null;
    }
    
    //allows the player to move based on keyboard input (w/a/s/d)
    public void move(String move){
        switch(move){
            case "w":
                xPos -= 1;
                break;
            case "a":
                yPos -= 1;
                break;
            case "s":
                xPos += 1;
                break;
            case "d":
                yPos += 1;
                break;
        }
    }

    // Getter methods to access the player's current X and Y positions
    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public void setPos(int x, int y){
        this.xPos = x;
        this.yPos = y;
    }
}