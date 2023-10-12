import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

class GUI{

    JButton button;
    JFrame frame;
    JPanel menu;
    JPanel game;
    
    public static void main (String[] args){
        GUI gui = new GUI();
        gui.run();
    }

    public void run(){
        //set up frame
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setSize(1080,720);
        frame.setTitle("i don't know");

        //set up menu (choose how many player) panel
        menu = new JPanel();
        menu.setBackground(Color.RED);
        menu.setBounds(0,0,1080,720);
        
        //set up game panel (main panel)
        game = new JPanel();
        game.setBackground(Color.GRAY);
        game.setBounds(0, 0, 1080, 720);
        game.setVisible(false);
        
        //set up menu label
        JLabel howManyPlayerQuestion = new JLabel();
        howManyPlayerQuestion.setText("How many Player?");
        howManyPlayerQuestion.setVerticalAlignment(JLabel.TOP);
        howManyPlayerQuestion.setHorizontalAlignment(JLabel.CENTER);

        //set up gameplay label
        JLabel gameplay = new JLabel("gameplay");
        gameplay.setVerticalAlignment(JLabel.TOP);
        gameplay.setHorizontalAlignment(JLabel.CENTER);
        game.add(gameplay);

        //set up button 3 
        JButton three = new JButton("3");
        three.setBounds(1080/2-100, 30, 100, 50);
        three.addActionListener(e -> {menu.setVisible(false);
                                        game.setVisible(true);});
  
        //set up button 4
        JButton four = new JButton("4");
        four.setBounds(1080/2+10, 30, 100,50); 
        four.addActionListener(e -> {menu.setVisible(false);
                                        game.setVisible(true);});

        //adding button and label to menu panel
        menu.add(howManyPlayerQuestion);
        menu.add(three);
        menu.add(four);
        //adding game and menu panel to frame
        frame.add(game);
        frame.add(menu);
    }

}