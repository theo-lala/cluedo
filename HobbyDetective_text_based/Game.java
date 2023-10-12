import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Game {
    private static int numPlayers;
    private List<Player> players;
    private List<Card<?>> tempDeck;
    private ArrayList<Character> deckCharacter = new ArrayList<>();
    private ArrayList<Character> deckCharacterCopy = new ArrayList<>();
    private ArrayList<Weapon> deckWeapon = new ArrayList<>();
    private ArrayList<Weapon> deckWeaponCopy = new ArrayList<>();
    private ArrayList<Estate> deckEstate = new ArrayList<>();
    private ArrayList<Estate> deckEstateCopy = new ArrayList<>();
    private ArrayList<Guess> solveAttempts = new ArrayList<>();
    HashMap<String, String> estateDoors = new HashMap<>();
    private Guess murderSolution;

    private Board board;
    private static Character murdererCharacter;
    private static Card<?> murdererWeapon;
    private static Card<?> murdererEstate;

    Thread gameThread;

    //asks user how many players they want to play with to begin game
    public static void main(String[] args) {
        
        Scanner inputN = new Scanner(System.in);
        do{
            System.out.println("How many players? (3 or 4): ");
            try{
                numPlayers = Integer.parseInt(inputN.nextLine());
            }catch (NumberFormatException nfe){
                System.err.println("Invalid Input!");
            }
            System.out.println("You entered : " + numPlayers);
        }while (numPlayers > 4 || numPlayers < 3);

        Game cluedo = new Game(numPlayers);
        cluedo.startGame();
        inputN.close();
    }

    //draws the game board
    public void startGame(){
        board = new Board(this);
        board.draw();
        run();
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    //main method of the game
    //gives user options to roll dice, see their hand, solve, guess or quit
    public void run() {
        Scanner input = new Scanner(System.in);
        int currentPlayerIndex = 0;

        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);

            if (currentPlayer.hasMadeIncorrectSolveAttempt()) {
                System.out.println(currentPlayer.getCharacter().getPrintable() + " has made an incorrect solve attempt. Skipping their turn.");
                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
                continue; // Skip the current turn and move to the next player
            }
            System.out.println("It is " + currentPlayer.getCharacter().getPrintable() + "s turn");
            System.out.println("Enter 'roll' to roll the dice, 'hand' to see your hand,'solve' to try solve the mystery or 'quit' to end the game:");

            boolean guessing = false;
            boolean canEndTurn = false;
            boolean skipTurn = false;
            String action = input.nextLine();

            if (action.equalsIgnoreCase("quit")) { 
                System.out.println("Game ended.");
                break; // Exit the loop if the player wants to quit
            } else if (action.equalsIgnoreCase("roll")) {
                if (!currentPlayer.hasMadeIncorrectSolveAttempt()) { //if player solves wrong, they cannot continue playing
                    int numMoves = Dice.rollDice(); //gives the player a random number of moves from 2 to 12
                    System.out.println("You rolled a " + numMoves + ".");

                    int remainingMoves = numMoves;

                    //as long as the player has remaining moves
                    while (remainingMoves > 0) {
                        if(guessing == false){
                            System.out.println("Enter movement direction (w/a/s/d) or 'quit' to end the game:");
                        }
                        String move = input.nextLine();

                        if (move.equalsIgnoreCase("quit")) {
                            System.out.println("Game ended.");
                            return; // Exit the loop if the player wants to quit during their movement
                        }

                        // Move the player
                        if(board.movePlayer(currentPlayer, move)){
                            board.draw(); // Draw the updated board
                            remainingMoves--;
                        } 

                        //lets player know how many moves they have left
                        if ((remainingMoves > 0) && (guessing == false)) {
                            System.out.println("You have " + remainingMoves + " turn(s) left.");
                        }

                        //checks whether player has entered an estate, if so lets them make a guess
                        int xPos = currentPlayer.getX();
                        int yPos = currentPlayer.getY();
                        Estate estate = null;

                        // Get the message based on the xPos and yPos
                        String position = xPos + "," + yPos;
                        if (estateDoors.containsKey(position)) {
                            String estateName = estateDoors.get(position);
                            System.out.println("You are in " + estateName + ". Enter 'guess' to make a guess.");
                            for(Estate e : deckEstateCopy){
                                if(e.getName().equals(estateName)){
                                    estate = e;
                                }
                            }
                        }

                        //allows guesser to guess and lets other players refute
                        if (move.equalsIgnoreCase("guess") && estate != null) {
                            guessing = true;
                            Guess g = makeGuess(currentPlayer, estate);
                            estate = null;
                            doRefutation(currentPlayer, g);
                            guessing = false;
                            canEndTurn = true;
                            System.out.println("Enter 'next' to end your turn");
                        }

                        //moves to the next players turn
                        if (move.equalsIgnoreCase("next") && (canEndTurn == true)) {
                            remainingMoves = 0;
                        }
                    }
                }
                //cycles through the players
                currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
                System.out.println("Player " + (currentPlayerIndex + 1) + "'s turn.");
            } else if (action.equalsIgnoreCase("hand")) {
                // Show the current player's hand
                System.out.println("Your hand:");
                for (Card<?> card : currentPlayer.getHand()) {
                    System.out.println(card.getName());
                }
            } else if (action.equalsIgnoreCase("solve")) {
                if (!skipTurn) { // Check if the turn should be skipped, i.e. if the player has made an incorrect solve
                    doSolve(currentPlayer);
                } else {
                    System.out.println("You have attempted to solve incorrectly. You cannot roll, guess, or attempt to solve again.");
                }
            } else {
                System.out.println("Invalid input. Please enter 'roll', 'hand', 'solve', or 'quit'.");
            }
        }
        //closes the scanner
        input.close();
    }

    //if the player is allowed to (i.e. has not made incorrect solve prior), 
    //lets them select a potential solution, if the selected solution matches the murder-solution they win
    private void doSolve(Player solver) {
        if (solver.hasMadeIncorrectSolveAttempt()) {
            System.out.println("You have already made an incorrect solve attempt. You cannot make more solve attempts.");
            return;
        }

        Guess chosenSolveAttempt = null;
        if (solveAttempts.isEmpty()) { //if no unrefutable guesses are available
            System.out.println("No solutions to choose from");
        } else {
            Scanner input = new Scanner(System.in);

            System.out.println("Enter a number corresponding to a potential solution:");

            for (int i = 0; i < solveAttempts.size(); i++) {
                System.out.println("[" + i + "] " + solveAttempts.get(i).getPrintable());
            }

            String choice = input.nextLine();

            if (choice.matches("\\d+")) {
                int solveAttemptIndex = Integer.parseInt(choice);
                if (solveAttemptIndex >= 0 && solveAttemptIndex < solveAttempts.size()) {
                    chosenSolveAttempt = solveAttempts.get(solveAttemptIndex);
                    System.out.println("Selected solve attempt is " + chosenSolveAttempt.getPrintable());
                }
            }

            //used to compare cards in potential solution to the murder solution
            String charName1 = chosenSolveAttempt.getCharacter().getName();
            String charName2 = murdererCharacter.getName();
            String weapName1 = chosenSolveAttempt.getWeapon().getName();
            String weapName2 = murdererWeapon.getName();
            String estName1 = chosenSolveAttempt.getEstate().getName();
            String estName2 = murdererEstate.getName();

            if (charName1.equals(charName2) && weapName1.equals(weapName2) && estName1.equals(estName2)) {
                System.out.println("You solved the murder!!");
                // Game ends here since the solve attempt is correct
                System.exit(0);
            } else {
                System.out.println("This solution is incorrect. You cannot make any more solve attempts.");
                solver.setMadeIncorrectSolveAttempt(true); // Set the flag for an incorrect solve attempt
            }
        }
    }

    //all players besides the guesser show a refutation card to the current guess if they are able to
    private void doRefutation(Player guesser, Guess g) {
        int refutationCount = 0;
        boolean refuted = false; 
        for (Player p : players) {
            if (p == guesser) {
                continue;
            } else {
                if (p.canRefute(g) != null) {
                    Card<?> refutation = p.canRefute(g);
                    System.out.println("Guess refuted by " + p.getCharacter().getPrintable() + " using " + refutation.getName() + " card.");
                    refutationCount++;
                    refuted = true; // A refutation occurred
                } else {
                    System.out.println(p.getCharacter().getPrintable() + " could not provide a refutation.");
                }
            }
        }

        //if no one could refute, set the guess as a potential solution and add it to the solveAttempts list
        if (refuted) {
            // A refutation occurred, continue the game
            refutationCount = 0;
        } else {
            // No player could provide a refutation, but that doesn't mean the solution is correct
            System.out.println("No player could provide a refutation.");
            g.setSolveAttempt();
            solveAttempts.add(g);
        }
    }

    //allow the guesser to select a weapon and character card, to go along with a given estate card
    private Guess makeGuess(Player guesser, Estate guessEstate) {
        Weapon guessWeapon = null;
        Character guessCharacter = null;

        Scanner input = new Scanner(System.in);

        //allows user to guess a weapon
        System.out.println("Enter a number corresponding to a weapon:");
        for (int i = 0; i < deckWeaponCopy.size(); i++) {
            System.out.println("[" + i + "] " + deckWeaponCopy.get(i).getName());
        }

        String choice = input.nextLine();

        if (choice.matches("\\d+")) {
            int weaponIndex = Integer.parseInt(choice);
            if (weaponIndex >= 0 && weaponIndex < deckWeaponCopy.size()) {
                guessWeapon = deckWeaponCopy.get(weaponIndex);
                System.out.println("Selected weapon is " + guessWeapon.getName());
            }
        } else {
            System.out.println("Invalid input. Please enter a valid number.");
        }

        //allows user to guess a character
        System.out.println("Enter a number corresponding to a character:");
        for (int i = 0; i < deckCharacterCopy.size(); i++) {
            System.out.println("[" + i + "] " + deckCharacterCopy.get(i).getName());
        }

        String choice2 = input.nextLine();

        if (choice2.matches("\\d+")) {
            int characterIndex = Integer.parseInt(choice2);
            if (characterIndex >= 0 && characterIndex < deckCharacterCopy.size()) {
                guessCharacter = deckCharacterCopy.get(characterIndex);
                System.out.println("Selected character is " + guessCharacter.getName());
            }
        }

        //tells user the estate, weapon and character they have chosen  
        Guess g = new Guess(guesser, guessCharacter, guessEstate, guessWeapon, false);
        System.out.println(g.getPrintable());
        return g;
    }

    //sets up the game based on the number of players specified (3 or 4)
    Game(int numPlayers) {
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        this.tempDeck = new ArrayList<>();

        setUpEstateDoors();
        setUpCards();
        initializePlayers();
    }

    //randomly generates a murder solution
    //shuffles the remaing card (not part of the murder solution) and deals them to the players
    private void initializePlayers() {
        Scanner input = new Scanner(System.in);
        int currentPlayerIndex = 0;

        Character[] characters = {
                new Character("Lucilla", 1, 11),
                new Character("Percy", 14, 22),
                new Character("Bert", 9, 1),
                new Character("Malina", 22, 9)
            };

        deckCharacter = new ArrayList<>(Arrays.asList(characters));
        List<Character> characterList = new ArrayList<>(Arrays.asList(characters));
        Collections.shuffle(characterList);

        //if only 3 players are playing, do not allow Percy to be playable 
        if (numPlayers == 3) {
            characterList.removeIf(character -> character.getName().equals("Percy"));
        }

        // Randomly select the murderer solution and remove the murder-cards from the deck that will be dealt to players
        int murdererIndex = (int) (Math.random() * numPlayers);
        murdererCharacter = characterList.get(murdererIndex);

        murdererWeapon = deckWeapon.get(murdererIndex);
        deckWeapon.remove(murdererWeapon);

        murdererEstate = deckEstate.get(murdererIndex);
        deckEstate.remove(murdererEstate);

        Collections.shuffle(deckCharacter); // Shuffle the character cards

        //deals the remaining cards to players
        for (int i = 0; i < numPlayers; i++) {
            Character character = characterList.get(i);
            Card<?> weaponCard;
            Card<?> estateCard;

            // Randomly assign weapon and estate cards to players
            weaponCard = deckWeapon.get(i);
            estateCard = deckEstate.get(i);
            Player player = new Player(character.getStartRow(), character.getStartCol());

            // Add the random character card to the player's hand
            if(!deckCharacter.get(i).getName().equals(getMurdererCharacter().getName())){
                Card<?> characterCard = deckCharacter.get(i);
                player.addCard(characterCard);
            } 

            player.addCard(weaponCard);
            player.addCard(estateCard);
            player.addCharacter(character);

            players.add(player);
        }
    }

    public static Character getMurdererCharacter() {
        return murdererCharacter;
    }

    public static Card<?> getMurdererWeapon() {
        return murdererWeapon;
    }

    public static Card<?> getMurdererEstate() {
        return murdererEstate;
    }

    //adds cards to various decks that will be used in gameplay
    private void setUpCards() {
        // Character cards
        deckCharacterCopy.add(new Character("Lucilla", 1, 11));
        deckCharacterCopy.add(new Character("Percy", 14, 22));
        deckCharacterCopy.add(new Character("Bert", 9, 1));
        deckCharacterCopy.add(new Character("Malina", 22, 9));

        // Weapon Cards
        deckWeapon.add(new Weapon("Broom"));
        deckWeapon.add(new Weapon("Scissors"));
        deckWeapon.add(new Weapon("Knife"));
        deckWeapon.add(new Weapon("Shovel"));
        deckWeapon.add(new Weapon("iPad"));

        deckWeaponCopy.add(new Weapon("Broom"));
        deckWeaponCopy.add(new Weapon("Scissors"));
        deckWeaponCopy.add(new Weapon("Knife"));
        deckWeaponCopy.add(new Weapon("Shovel"));
        deckWeaponCopy.add(new Weapon("iPad"));

        // Estate Cards
        deckEstate.add(new Estate("Haunted House"));
        deckEstate.add(new Estate("Manic Manor"));
        deckEstate.add(new Estate("Peril Palace"));
        deckEstate.add(new Estate("Calamity Castle"));
        deckEstate.add(new Estate("Visitation Villa"));

        deckEstateCopy.add(new Estate("Haunted House"));
        deckEstateCopy.add(new Estate("Manic Manor"));
        deckEstateCopy.add(new Estate("Peril Palace"));
        deckEstateCopy.add(new Estate("Calamity Castle"));
        deckEstateCopy.add(new Estate("Visitation Villa"));
    }

    //puts estate doors into a hashMap, to identify when a player has entered an estate door
    private void setUpEstateDoors() {
        estateDoors.put("3,6", "Haunted House");
        estateDoors.put("6,5", "Haunted House");
        estateDoors.put("17,3", "Calamity Castle");
        estateDoors.put("18,6", "Calamity Castle");
        estateDoors.put("3,17", "Manic Manor");
        estateDoors.put("6,20", "Manic Manor");
        estateDoors.put("17,20", "Peril Palace");
        estateDoors.put("18,17", "Peril Palace");
        estateDoors.put("10,12", "Visitation Villa");
        estateDoors.put("11,14", "Visitation Villa");
        estateDoors.put("12,9", "Visitation Villa");
        estateDoors.put("13,11", "Visitation Villa");
    }
}