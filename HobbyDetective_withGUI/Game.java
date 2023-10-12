import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.awt.event.*;
import javax.swing.*;
import java.util.Enumeration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.awt.*;

import javax.swing.*;

public class Game {

    private enum state{
        MENU, PLAY,
        }

        private static int numPlayers;
        private static List<Player> players;
        private static int rolledNumber;
        private static int currentPlayerIndex = 0;
        private static int potentialSolvesNum = 0;
        private List<Card<?>> tempDeck;
        private static ArrayList<Character> deckCharacter = new ArrayList<>();
        private static  ArrayList<Character> deckCharacterCopy = new ArrayList<>();
        private static  ArrayList<Weapon> deckWeapon = new ArrayList<>();
        private static  ArrayList<Weapon> deckWeaponCopy = new ArrayList<>();
        private static  ArrayList<Estate> deckEstate = new ArrayList<>();
        private static  ArrayList<Estate> deckEstateCopy = new ArrayList<>();
        private static  ArrayList<Guess> solveAttempts = new ArrayList<>();
        private static HashMap<String, String> estateDoors = new HashMap<>();
        private static  ArrayList<Card<?>> murderSolution;

        private static Board board;
        private static Game game;
        private static Character murdererCharacter;
        private static Weapon murdererWeapon;
        private static Estate murdererEstate;
        private static Game.state state;

        private static JPanel panel1;
        private static JPanel panel2;
        private static JPanel panel3;
        private static JPanel panel4;

        private static JButton wButton;
        private static JButton aButton;
        private static JButton sButton;
        private static JButton dButton;
        private static JButton solve;
        private static JFrame frame;

        private static JList<String> potentialSolvesList;

        static DefaultListModel<String> potentialSolves;

        Thread gameThread;

        //asks user how many players they want to play with to begin game
        public static void main(String[] args) {
            game = new Game();
            frame = new JFrame("Hobby Detective");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1080, 720);

            // Create a menu bar
            JMenuBar menuBar = new JMenuBar();

            // Create a menu
            JMenu Menu = new JMenu("Menu");

            // Give user option to exit
            JMenuItem exitMenuItem = new JMenuItem("Exit");
            exitMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to leave?","Confirm Exit",JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                });

            JMenuItem rulesMenuItem = new JMenuItem("Rules");
            rulesMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        displayRules(); // Call a function to display the rules
                    }
                });

            // Add menu items to the menu
            Menu.add(exitMenuItem);
            Menu.add(rulesMenuItem);
            menuBar.add(Menu);
            frame.setJMenuBar(menuBar);

            JLabel howManyPlayerQuestion = new JLabel();
            howManyPlayerQuestion.setText("How many Players?");
            howManyPlayerQuestion.setVerticalAlignment(JLabel.TOP);
            howManyPlayerQuestion.setHorizontalAlignment(JLabel.CENTER);

            JButton three = new JButton("3");
            JButton four = new JButton("4");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(three);
            buttonPanel.add(four);

            frame.add(howManyPlayerQuestion, BorderLayout.NORTH);
            frame.add(buttonPanel, BorderLayout.CENTER);

            frame.setVisible(true);

            three.addActionListener(e -> {
                numPlayers = 3;
                Game.board = new Board(game, frame);
                setUpCards();
                initializePlayers();
                frame.getContentPane().removeAll();
                showCharacterDialog();
            });
            
            four.addActionListener(e -> {
                numPlayers = 4;
                Game.board = new Board(game, frame);
                setUpCards();
                initializePlayers();
                frame.getContentPane().removeAll();
                showCharacterDialog();
            });
        }

        //displays the available characters prior to starting game
        private static void showCharacterDialog() {
            String[] characters3 = {"Lucilla", "Bert", "Malina"}; // 3 player characters
            String[] characters4 = {"Lucilla", "Bert", "Malina", "Percy"}; // 4 player characters
            String charactersList;

            if(numPlayers == 3){
                charactersList = String.join("\n", characters3);
            } else {
                charactersList = String.join("\n", characters4);
            }

            //displays the names of characters, user presses ok, then game starts
            JOptionPane.showMessageDialog(frame, "The characters available for play are:\n" + charactersList, "Character Selection", JOptionPane.PLAIN_MESSAGE);

            setupMain();

            int choice = JOptionPane.showConfirmDialog(frame, "Do you want to start the game?", "Character Selection", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                commenceGame();
            } else {
                System.exit(0);
            }
        }

        private static void commenceGame() {
            Player currentPlayer = players.get(currentPlayerIndex);

            // Create a custom option pane with a "Roll Dice" button
            Object[] options = {"Roll Dice"};
            int optionChosen = JOptionPane.showOptionDialog(frame, "It's " + currentPlayer.getCharacter().getPrintable() + "'s turn.", "Turn", JOptionPane.DEFAULT_OPTION,
                               JOptionPane.INFORMATION_MESSAGE, null, options,options[0]);

            if (optionChosen == 0) { // The player clicked "Roll Dice"
                rolledNumber = Dice.rollDice(); // Roll the dice
                JOptionPane.showMessageDialog(frame, currentPlayer.getCharacter().getPrintable() + " rolled a " + rolledNumber, "Dice Roll Result", JOptionPane.INFORMATION_MESSAGE);

                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        // Function to display the rules in the menu
        private static void displayRules() {
            JOptionPane.showMessageDialog(null,
                "Welcome to Hobby Detective!\n" +
                "Rules:\n" +
                "- When it is your turn roll the dice\n" +
                "- Move the number you rolled on the board\n" + 
                "- If you enter an estate, make a suggestion comprising the estate you are in and a chosen weapon and character\n" +
                "- If no one can refute your suggestion, this is a potential solve\n" +
                "- Solve the game to win!\n" +
                "- Good luck and have fun!");
        }

        public static void setupMain(){
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            //creates panels
            panel1 = new JPanel();
            panel2 = new JPanel();
            panel3 = new JPanel();
            panel4 = new JPanel();

            panel1.setBackground(Color.lightGray);
            panel2.setBackground(Color.lightGray);
            panel3.setBackground(Color.lightGray);
            panel4.setBackground(Color.lightGray);

            panel1.setPreferredSize(new Dimension(100,100)); //top yellow
            panel2.setPreferredSize(new Dimension(125,100)); //left red
            panel3.setPreferredSize(new Dimension(150,150)); //middle blue
            panel4.setPreferredSize(new Dimension(125,100)); //right green

            setupPanel1();
            setupPanel2();
            setupPanel3();
            setupPanel4(currentPlayerIndex);

            frame.add(panel1, BorderLayout.NORTH);
            frame.add(panel2, BorderLayout.WEST);
            frame.add(panel3, BorderLayout.CENTER);
            frame.add(panel4, BorderLayout.EAST);

            frame.setVisible(true);

        }

        Game(){
            startGame();
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

        public static void setupPanel1(){
            // Create a FlowLayout for panel1
            panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

            // Add title label to panel1 (on a separate line)
            JLabel gameTitle = new JLabel("Hobby Detectives");
            gameTitle.setFont(new Font("Arial", Font.BOLD, 60)); // Set font size to 24
            panel1.add(gameTitle);

            // Create a panel to hold the remaining labels (on the same line)
            JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            // Add labels to the labelsPanel
            JLabel lostLabel = new JLabel("Lost?");
            JLabel player1Label = new JLabel("Player1");
            JLabel player2Label = new JLabel("Player2");
            JLabel player3Label = new JLabel("Player3");
            JLabel player4Label = new JLabel("Player4");

            // Add labels to the labelsPanel
            labelsPanel.add(lostLabel);
            labelsPanel.add(player1Label);
            labelsPanel.add(player2Label);
            labelsPanel.add(player3Label);
            labelsPanel.add(player4Label);

            // Add the labelsPanel to panel1
            panel1.add(labelsPanel);
        }

        public static void setupPanel2(){
            setUpEstateDoors();

            // Add a JTextField to panel2
            JTextField textField = new JTextField(10);
            panel2.add(textField);

            // when dice button is pressed, player is told what number they rolled
            /*JButton dice = new JButton("Roll Dice");
            panel2.add(dice);

            dice.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int rolledNumber = Dice.rollDice(); // Call the rollDice() method from Dice class
                    JOptionPane.showMessageDialog(frame, "You rolled a " + rolledNumber, "Dice Roll Result", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // Button to intiate suggesting
            JButton suggestion = new JButton("Suggestion");
            panel2.add(suggestion);*/

            // Button to intiate solving
            solve = new JButton("Solve");
            solve.setPreferredSize(new Dimension(100, solve.getPreferredSize().height)); // Set width to 100
            solve.setEnabled(false);
            panel2.add(solve);
            
            solve.addActionListener(e -> {

                String[] potentialSolveStrings = new String[potentialSolves.size()];
            for (int i = 0; i < potentialSolves.size(); i++) {
                potentialSolveStrings[i] = potentialSolves.getElementAt(i);
            }
                int solveAttemptIndex = JOptionPane.showOptionDialog(
                    frame,
                    "choose potential solve!",
                    "Solve",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    potentialSolveStrings,
                    potentialSolveStrings[0]);

                doSolve(players.get(currentPlayerIndex),solveAttemptIndex);
                // Guess chosenSolveAttempt = null;
                //     if (solveAttemptIndex >= 0 && solveAttemptIndex < solveAttempts.size()) {
                //         chosenSolveAttempt = solveAttempts.get(solveAttemptIndex);
                //         System.out.println("Selected solve attempt is " + chosenSolveAttempt.getPrintable());
                //     }
                
                
                // //used to compare cards in potential solution to the murder solution
                // String charName1 = chosenSolveAttempt.getCharacter().getName();
                // String charName2 = murdererCharacter.getName();
                // String weapName1 = chosenSolveAttempt.getWeapon().getName();
                // String weapName2 = murdererWeapon.getName();
                // String estName1 = chosenSolveAttempt.getEstate().getName();
                // String estName2 = murdererEstate.getName();

                // if (charName1.equals(charName2) && weapName1.equals(weapName2) && estName1.equals(estName2)) {
                //     System.out.println("You solved the murder!!");
                //     // Game ends here since the solve attempt is correct
                //     System.exit(0);
                // } else {
                //     System.out.println("This solution is incorrect. You cannot make any more solve attempts.");
                //     solver.setMadeIncorrectSolveAttempt(true); // Set the flag for an incorrect solve attempt
                // }
                
                
            });

            // Create data for the JList of potential solves
            JLabel potentialSolvesLabel = new JLabel("Potential Solves");
            panel2.add(potentialSolvesLabel);

            potentialSolves = new DefaultListModel<>();
            potentialSolves.addElement(" ");
            potentialSolves.addElement(" ");
            potentialSolves.addElement(" ");
            potentialSolvesList = new JList<>(potentialSolves);
            potentialSolvesList.setPreferredSize(new Dimension(100, potentialSolvesList.getPreferredSize().height));

            panel2.add(potentialSolvesList);

            // buttons for moving
            wButton = new JButton("^");
            aButton = new JButton("<");
            sButton = new JButton("v");
            dButton = new JButton(">");

            panel2.add(wButton);
            panel2.add(aButton);
            panel2.add(sButton);
            panel2.add(dButton);

            //allows player to  ove the number they have rolled
            //decrements the moves, starts next players turn when number of moves runs out
            ActionListener buttonListener = e -> {
                        //player's moves left
                        for(Card<?> c : murderSolution){
                            System.out.println(c.getPrintable());
                        }
                        if (!players.isEmpty()) {
                            String direction = "";
                            Object source = e.getSource();

                            if (source == wButton) direction = "a";
                            else if (source == aButton) direction = "w";
                            else if (source == sButton) direction = "d";
                            else if (source == dButton) direction = "s";

                            if (!direction.isEmpty()) {
                                if(!board.movePlayer(players.get(currentPlayerIndex), direction)) return;
                                board.repaint();

                                int xPos = players.get(currentPlayerIndex).getX();
                                int yPos = players.get(currentPlayerIndex).getY();

                                String position = xPos + "," + yPos;
                                Estate estate = null;

                                if (estateDoors.containsKey(position)) {
                                    String estateName = estateDoors.get(position);
                                    String estateMessage = "You are in " + estateName + "\nYou can now make a suggestion";

                                    for (Estate es : deckEstateCopy) {
                                        if (es.getName().equals(estateName)) {
                                            estate = es;
                                        }
                                    }
                                    JOptionPane.showMessageDialog(frame, estateMessage, "Entered an estate", JOptionPane.INFORMATION_MESSAGE);

                                    showSuggestionDialog(estate, players.get(currentPlayerIndex));
                                }
                                rolledNumber -= 1;
                                if(rolledNumber == 0){
                                    currentPlayerIndex = (currentPlayerIndex + 1) % numPlayers;
                                    setupPanel4(currentPlayerIndex);
                                    commenceGame();
                                    return;
                                }
                            }
                        } else {
                            System.out.println("players is empty");
                        }
                };

            wButton.addActionListener(buttonListener);
            aButton.addActionListener(buttonListener);
            sButton.addActionListener(buttonListener);
            dButton.addActionListener(buttonListener);
        }

        public static void setupPanel3(){
            panel3.add(board);
        }

        public static void setupPanel4(int currentPlayerIndex){
            //when this button is pressed the hand of the current player is displayed
            panel4.removeAll();
            JButton showHandButton = new JButton("Show Hand");
            showHandButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showHandDialog(currentPlayerIndex);
                }
            });
            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerPanel.add(showHandButton);
            panel4.add(showHandButton);
            panel4.revalidate();
            panel4.repaint();
        }

        //gets info about players hand to display
        private static void showHandDialog(int currentPlayerIndex) {
            Player currentPlayer = players.get(currentPlayerIndex);

            StringBuilder handInfo = new StringBuilder("Player's Hand:\n");
            for (Card card : currentPlayer.getHand()) {
                handInfo.append(card.getPrintable()).append("\n");
            }

            JOptionPane.showMessageDialog(frame, handInfo.toString(), "Player's Hand", JOptionPane.INFORMATION_MESSAGE);
        }

        //draws the game board
        public void startGame(){
            Game.players = new ArrayList<>();
            this.tempDeck = new ArrayList<>();
            setUpEstateDoors();
        }

        //allows users to select one character and one weapon from a list
        private static void showSuggestionDialog(Estate estate, Player guesser) {
            JDialog suggestionDialog = new JDialog(frame, "Make a Suggestion", true);
            suggestionDialog.setSize(300, 400);
            suggestionDialog.setLayout(new BoxLayout(suggestionDialog.getContentPane(), BoxLayout.Y_AXIS));

            JLabel characterLabel = new JLabel("Select a character:");
            suggestionDialog.add(characterLabel);

            JRadioButton Bert = new JRadioButton("Bert");
            JRadioButton Lucilla = new JRadioButton("Lucilla");
            JRadioButton Malina = new JRadioButton("Malina");
            JRadioButton Percy = new JRadioButton("Percy");

            ButtonGroup characterGroup = new ButtonGroup();
            characterGroup.add(Bert);
            characterGroup.add(Lucilla);
            characterGroup.add(Malina);
            characterGroup.add(Percy);

            suggestionDialog.add(Bert);
            suggestionDialog.add(Lucilla);
            suggestionDialog.add(Malina);
            suggestionDialog.add(Percy);

            JLabel weaponLabel = new JLabel("Select a weapon:");
            suggestionDialog.add(weaponLabel);

            JRadioButton Broom = new JRadioButton("Broom");
            JRadioButton Scissors = new JRadioButton("Scissors");
            JRadioButton Knife = new JRadioButton("Knife");
            JRadioButton Shovel = new JRadioButton("Shovel");
            JRadioButton iPad = new JRadioButton("iPad");

            ButtonGroup weaponGroup = new ButtonGroup();
            weaponGroup.add(Broom);
            weaponGroup.add(Scissors);
            weaponGroup.add(Knife);
            weaponGroup.add(Shovel);
            suggestionDialog.add(iPad);

            suggestionDialog.add(Broom);
            suggestionDialog.add(Scissors);
            suggestionDialog.add(Knife);
            suggestionDialog.add(Shovel);
            suggestionDialog.add(iPad);

            JButton suggestButton = new JButton("Suggest");
            suggestionDialog.add(suggestButton);

            suggestButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedCharacter = getSelectedButtonText(characterGroup);
                    String selectedWeapon = getSelectedButtonText(weaponGroup);

                    Character selectC = null;
                    for(Character c : deckCharacterCopy){
                        if(c.getPrintable().equals(selectedCharacter)){
                            selectC = c;
                            break;
                        }
                    }

                    Weapon selectW = null;
                    for(Weapon w : deckWeaponCopy){
                        if(w.getPrintable().equals("Weapon "+selectedWeapon)){
                            selectW = w;
                            break;
                        }
                    }

                    Guess g = new Guess(guesser, selectC , estate, selectW, false);

                    String suggestionMessage = "You suggested:\nCharacter: " + selectedCharacter+ "\n" + estate.getPrintable() +"\nWeapon: " + selectedWeapon;
                    JOptionPane.showMessageDialog(frame, suggestionMessage, "Suggestion Result", JOptionPane.INFORMATION_MESSAGE);

                    suggestionDialog.dispose();
                    doRefutation(guesser, g);
                }
            });

            suggestionDialog.setVisible(true);
        }

        //gets the text on a selected button
        private static String getSelectedButtonText(ButtonGroup buttonGroup) {
            for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
                AbstractButton button = buttons.nextElement();
                if (button.isSelected()) {
                    return button.getText();
                }
            }
            return null;
        }

        public int getNumPlayers() {
            return numPlayers;
        }

        //main method of the game
        //gives user options to roll dice, see their hand, solve, guess or quit
        //if the player is allowed to (i.e. has not made incorrect solve prior),
        //lets them select a potential solution, if the selected solution matches the murder-solution they win
        private static void doSolve(Player solver, int solveAttemptIndex) {
            if (solver.hasMadeIncorrectSolveAttempt()) {
                System.out.println("You have already made an incorrect solve attempt. You cannot make more solve attempts.");
                return;
            }

            Guess chosenSolveAttempt = null;
            if (solveAttempts.isEmpty()) { //if no unrefutable guesses are available
                System.out.println("No solutions to choose from");
            } else {
               
            if (solveAttemptIndex >= 0 && solveAttemptIndex < solveAttempts.size()) {
                chosenSolveAttempt = solveAttempts.get(solveAttemptIndex);
                System.out.println("Selected solve attempt is " + chosenSolveAttempt.getPrintable());
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
                    JOptionPane.showMessageDialog(frame, "CONGRATS YOU WIN!!!", "WINNNn", JOptionPane.PLAIN_MESSAGE);
                    System.exit(0);
                } else {
                    System.out.println("This solution is incorrect. You cannot make any more solve attempts.");
                    solver.setMadeIncorrectSolveAttempt(true); // Set the flag for an incorrect solve attempt
                }
            }
        }

        //all players besides the guesser show a refutation card to the current guess if they are able to
        private static void doRefutation(Player guesser, Guess g) {
            int refutationCount = 0;
            boolean refuted = false;
            StringBuilder refutationMessage = new StringBuilder();

            for (Player p : players) {
                if (p == guesser) {
                    continue;
                } else {
                    Card<?> refutationCard = p.canRefute(g);
                    if (refutationCard != null) {
                        String message = "Guess refuted by " + p.getCharacter().getPrintable() + " using " + refutationCard.getName() + " card.";
                        refutationMessage.append(message).append("\n");
                        refutationCount++;
                        refuted = true; // A refutation occurred
                    } else {
                        String message = p.getCharacter().getPrintable() + " could not provide a refutation.";
                        refutationMessage.append(message).append("\n");
                    }
                }
            }

            // If no one could refute, set the guess as a potential solution and add it to the solveAttempts list
            if (refuted) {
                // A refutation occurred, continue the game
                // Do something with the refutationMessage if needed
                
            } else {
                // No player could provide a refutation, but that doesn't mean the solution is correct
                refutationMessage.append("No player could provide a refutation.");
                g.setSolveAttempt();
                solveAttempts.add(g);
                potentialSolvesNum += 1;
                potentialSolves.setElementAt(g.toString(), potentialSolvesNum-1);
                solve.setEnabled(true);
            }

            // Display the refutation messages in a dialog
            if (refutationMessage.length() > 0) {
                JOptionPane.showMessageDialog(frame, refutationMessage.toString(), "Refutation Result", JOptionPane.INFORMATION_MESSAGE);
            }

            /*int refutationCount = 0;
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
            }*/
        }

        //randomly generates a murder solution
        //shuffles the remaing card (not part of the murder solution) and deals them to the players
        private static void initializePlayers() {

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

            murderSolution = new ArrayList<>();
            murderSolution.add(murdererCharacter);
            murderSolution.add(murdererEstate);
            murderSolution.add(murdererWeapon);

            Collections.shuffle(deckCharacter); // Shuffle the character cards

            System.out.println(numPlayers);
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
        private static void setUpCards() {
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
        private static void setUpEstateDoors() {
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
