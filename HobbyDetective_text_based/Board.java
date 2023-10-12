import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

public class Board {
    private static final int BOARD_SIZE = 24;
    private Tile[][] board;
    private Game game;
    private int numPlayers;

    public Board(Game game) {
        this.game = game;
        this.numPlayers = game.getNumPlayers();
        board = new Tile[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }
 
    //creates the text based 24 x 24 board with estates and greyed-out areas
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Tile(i,j,"empty"); 
            }
        }
        
        //hauntedHouse
        board[2][2] = new Tile(2,2,"hauntedHouse");
        board[2][3] = new Tile(2,3,"hauntedHouse");
        board[2][4] = new Tile(2,4,"hauntedHouse");
        board[2][5] = new Tile(2,5,"hauntedHouse");
        board[2][6] = new Tile(2,6,"hauntedHouse");
        board[3][2] = new Tile(3,2,"hauntedHouse");
        board[3][3] = new Tile(3,3,"hauntedHouse");
        board[3][4] = new Tile(3,4,"hauntedHouse");
        board[3][5] = new Tile(3,5,"hauntedHouse");
        board[4][2] = new Tile(4,2,"hauntedHouse");
        board[4][3] = new Tile(4,3,"hauntedHouse");
        board[4][4] = new Tile(4,4,"hauntedHouse");
        board[4][5] = new Tile(4,5,"hauntedHouse");
        board[4][6] = new Tile(4,6,"hauntedHouse");
        board[5][2] = new Tile(5,2,"hauntedHouse");
        board[5][3] = new Tile(5,3,"hauntedHouse");
        board[5][4] = new Tile(5,4,"hauntedHouse");
        board[5][5] = new Tile(5,5,"hauntedHouse");
        board[5][6] = new Tile(5,6,"hauntedHouse");
        board[6][2] = new Tile(6,2,"hauntedHouse");
        board[6][3] = new Tile(6,3,"hauntedHouse");
        board[6][4] = new Tile(6,4,"hauntedHouse");
        board[6][6] = new Tile(6,5,"hauntedHouse");

        //calamityCastle
        board[17][2] = new Tile(17,2,"calamityCastle");
        board[17][4] = new Tile(17,4,"calamityCastle");
        board[17][5] = new Tile(17,5,"calamityCastle");
        board[17][6] = new Tile(17,6,"calamityCastle");
        board[18][2] = new Tile(18,2,"calamityCastle");
        board[18][3] = new Tile(18,3,"calamityCastle");
        board[18][4] = new Tile(18,4,"calamityCastle");
        board[18][5] = new Tile(18,5,"calamityCastle");
        board[19][2] = new Tile(19,2,"calamityCastle");
        board[19][3] = new Tile(19,3,"calamityCastle");
        board[19][4] = new Tile(19,4,"calamityCastle");
        board[19][5] = new Tile(19,5,"calamityCastle");
        board[19][6] = new Tile(19,6,"calamityCastle");
        board[20][2] = new Tile(20,2,"calamityCastle");
        board[20][3] = new Tile(20,3,"calamityCastle");
        board[20][4] = new Tile(20,4,"calamityCastle");
        board[20][5] = new Tile(20,5,"calamityCastle");
        board[20][6] = new Tile(20,6,"calamityCastle");
        board[21][2] = new Tile(21,2,"calamityCastle");
        board[21][3] = new Tile(21,3,"calamityCastle");
        board[21][4] = new Tile(21,4,"calamityCastle");
        board[21][5] = new Tile(21,5,"calamityCastle");
        board[21][6] = new Tile(21,5,"calamityCastle");

        //manicManor
        board[2][17] = new Tile(2,2,"manicManor");
        board[2][18] = new Tile(2,3,"manicManor");
        board[2][19] = new Tile(2,4,"manicManor");
        board[2][20] = new Tile(2,5,"manicManor");
        board[2][21] = new Tile(2,6,"manicManor");
        board[3][18] = new Tile(3,3,"manicManor");
        board[3][19] = new Tile(3,4,"manicManor");
        board[3][20] = new Tile(3,5,"manicManor");
        board[3][21] = new Tile(3,5,"manicManor");
        board[4][17] = new Tile(4,2,"manicManor");
        board[4][18] = new Tile(4,3,"manicManor");
        board[4][19] = new Tile(4,4,"manicManor");
        board[4][20] = new Tile(4,5,"manicManor");
        board[4][21] = new Tile(4,6,"manicManor");
        board[5][17] = new Tile(5,2,"manicManor");
        board[5][18] = new Tile(5,3,"manicManor");
        board[5][19] = new Tile(5,4,"manicManor");
        board[5][20] = new Tile(5,5,"manicManor");
        board[5][21] = new Tile(5,6,"manicManor");
        board[6][17] = new Tile(6,2,"manicManor");
        board[6][18] = new Tile(6,3,"manicManor");
        board[6][19] = new Tile(6,4,"manicManor");
        board[6][21] = new Tile(6,5,"manicManor");

        //perilPalace
        board[2+15][17] = new Tile(2,2,"perilPalace");
        board[2+15][18] = new Tile(2,3,"perilPalace");
        board[2+15][19] = new Tile(2,4,"perilPalace");
        board[2+15][21] = new Tile(2,6,"perilPalace");
        board[3+15][18] = new Tile(3,3,"perilPalace");
        board[3+15][19] = new Tile(3,4,"perilPalace");
        board[3+15][20] = new Tile(3,5,"perilPalace");
        board[3+15][21] = new Tile(3,5,"perilPalace");
        board[4+15][17] = new Tile(4,2,"perilPalace");
        board[4+15][18] = new Tile(4,3,"perilPalace");
        board[4+15][19] = new Tile(4,4,"perilPalace");
        board[4+15][20] = new Tile(4,5,"perilPalace");
        board[4+15][21] = new Tile(4,6,"perilPalace");
        board[5+15][17] = new Tile(5,2,"perilPalace");
        board[5+15][18] = new Tile(5,3,"perilPalace");
        board[5+15][19] = new Tile(5,4,"perilPalace");
        board[5+15][20] = new Tile(5,5,"perilPalace");
        board[5+15][21] = new Tile(5,6,"perilPalace");
        board[6+15][17] = new Tile(6,2,"perilPalace");
        board[6+15][18] = new Tile(6,3,"perilPalace");
        board[6+15][19] = new Tile(6,4,"perilPalace");
        board[6+15][20] = new Tile(6,4,"perilPalace");
        board[6+15][21] = new Tile(6,5,"perilPalace");

        //visitationVilla
        board[2+8][1+8] = new Tile(2,2,"visitationVilla");
        board[2+8][2+8] = new Tile(2,2,"visitationVilla");
        board[2+8][3+8] = new Tile(2,3,"visitationVilla");
        board[2+8][5+8] = new Tile(2,5,"visitationVilla");
        board[2+8][6+8] = new Tile(2,6,"visitationVilla");
        board[3+8][1+8] = new Tile(3,2,"visitationVilla");
        board[3+8][2+8] = new Tile(3,2,"visitationVilla");
        board[3+8][3+8] = new Tile(3,3,"visitationVilla");
        board[3+8][4+8] = new Tile(3,4,"visitationVilla");
        board[3+8][5+8] = new Tile(3,5,"visitationVilla");
        board[4+8][2+8] = new Tile(4,2,"visitationVilla");
        board[4+8][3+8] = new Tile(4,3,"visitationVilla");
        board[4+8][4+8] = new Tile(4,4,"visitationVilla");
        board[4+8][5+8] = new Tile(4,5,"visitationVilla");
        board[4+8][6+8] = new Tile(4,6,"visitationVilla");
        board[5+8][1+8] = new Tile(5,2,"visitationVilla");
        board[5+8][2+8] = new Tile(5,2,"visitationVilla");
        board[5+8][4+8] = new Tile(5,4,"visitationVilla");
        board[5+8][5+8] = new Tile(5,5,"visitationVilla");
        board[5+8][6+8] = new Tile(5,6,"visitationVilla");

        //gray left
        board[3+8][1+4] = new Tile(3,2,"gray");
        board[3+8][1+3] = new Tile(3,2,"gray");
        board[4+8][1+4] = new Tile(3,2,"gray");
        board[4+8][1+3] = new Tile(3,2,"gray");
        
        //gray right
        board[3+8][1+17] = new Tile(3,2,"gray");
        board[3+8][1+16] = new Tile(3,2,"gray");
        board[4+8][1+17] = new Tile(3,2,"gray");
        board[4+8][1+16] = new Tile(3,2,"gray");
        
        //gray up
        board[5][11] = new Tile(5,11,"gray");
        board[5][12] = new Tile(5,12,"gray");
        board[6][11] = new Tile(6,11,"gray");
        board[6][12] = new Tile(6,12,"gray");
        
        //gray bot
        board[5+12][11] = new Tile(5,11,"gray");
        board[5+12][12] = new Tile(5,12,"gray");
        board[6+12][11] = new Tile(6,11,"gray");
        board[6+12][12] = new Tile(6,12,"gray");
        
        //Players
        board[1][11] = new Tile(1,11,"Lucilla");
        board[9][1] = new Tile(9,1,"Bert");
        board[22][9] = new Tile(22,9,"Malina");
        
        //if the game has 3 players, Percy is not drawn
        if (game.getNumPlayers() == 4) {
            board[14][22] = new Tile(14, 22, "Percy");
        }
    }
 
    //gets a specified tile on the board
    public Tile get(int row, int col) {
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            return board[row][col];
        }
        return null;
    }

    //draws out the board
    public void draw() {
        System.out.println("------------------------------------------------------");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(" " + board[i][j].getPrintable());
            }
            System.out.println();
        }
        System.out.println("------------------------------------------------------");
    }

    //return true if move is valid
    public boolean movePlayer(Player player, String move) {
        int currentX = player.getX();
        int currentY = player.getY();

        int futureX = player.getX();
        int futureY = player.getY();
    
        switch(move){
            case "w":
                futureX -= 1;
                break;
            case "a":
                futureY -= 1;
                break;
            case "s":
                futureX += 1;
                break;
            case "d":
                futureY += 1;
                break;
        }
        
        if(isValidMove(futureX, futureY) && isFreeCell(futureX, futureY) ){
            // replace player with empty tile
            board[currentX][currentY] = new Tile(currentX, currentY, "empty");
            // Move player
            player.move(move);
            // redraw player in new tile
            board[player.getX()][player.getY()] = new Tile(player.getX(), player.getY(), player.getCharacter().getPrintable());
            return true;
        }else{
            System.out.println("INVALID MOVE");
            return false;
        }
    }

    public void addPlayer(int x, int y) {
        board[x][y] = new Tile(x,y,"player"); // Assuming Tile is a class representing a cell on the map
    }

    public void addPlayer(Player player){
        board[player.getX()][player.getY()] = new Tile(player.getX(), player.getY(), "player"); // Assuming Tile is a class representing a cell on the map
    }

    //checks that player can move to the tile they want to move to
    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }
    
    //checks that the tile that a player wants to move to is vacant
    private boolean isFreeCell(int x, int y) {
        return board[x][y].getType().equals("empty");
    }
}