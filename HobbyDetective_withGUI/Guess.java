import java.util.ArrayList;
import java.util.List;

//set of cards (one estate, one weapon, one character) used to make the solution, guesses and solve attempts
public class Guess {
    Player guesser;
    Character character;
    Estate estate;
    Weapon weapon;
    boolean isSolveAttempt;
    
    public Guess(Player guesser, Character character, Estate estate, Weapon weapon, boolean isSolveAttempt){
        this.guesser = guesser;
        this.character = character;
        this.estate = estate;
        this.weapon = weapon;
        this.isSolveAttempt = isSolveAttempt;
    }
    
    Player getGuesser(){
        return this.guesser;
    }
    
    Character getCharacter(){
        return this.character;
    }
    
    Estate getEstate(){
        return this.estate;
    }
    
    Weapon getWeapon(){
        return this.weapon;
    }
    
    //checks whether the guess is considered a potential solution or not
    boolean getIsSolveAttempt(){
        return this.isSolveAttempt;
    }
    
    //used to change the guess into a potential solution
    void setSolveAttempt(){
        this.isSolveAttempt = true;
    }
    
    //returns a string that states what the player has guessed
    String getPrintable(){
        String s = "Estate guessed: " + this.estate.getName()+ "    Weapon guessed: " + this.weapon.getName() + "    Character guessed: " + this.character.getName();
        return s;
    }

    @Override
    public String toString(){
        return this.estate.getName()+" "+ this.weapon.getName()+" " + this.character.getName();
    }
}