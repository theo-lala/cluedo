import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

//represents one tile on the board
public class Tile {
    private int row;
    private int col;

    private Estate estate;
    private Player player;

    private boolean canEnter;
    private String type;

    Tile(int row, int col, String type){
        this.row = row;
        this.col = col;
        this.player = null;
        this.canEnter = true;
        this.type = type;
    }

    public boolean isEnterable() {
        return canEnter;
    }

    public void setEnterable(boolean canEnter) {
        this.canEnter = canEnter;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public Estate getEstate(){
        return estate;
    }

    public String getType(){
        return type;
    }

    public void setEstate(Estate estate){
        this.estate = estate;
    }

    //shows a string represnetation of the tile on the board, depending on its type
    public String getPrintable() {
        switch(type){
            case "Lucilla":
                return "L";
            case "Bert":
                return "B";
            case "Malina":
                return "M";
            case "Percy":
                return "P";
            case "gray":
                return "#";
            case "hauntedHouse":
                return "H";
            case "manicManor":
                return "M";
            case "visitationVilla":
                return "V";
            case "calamityCastle":
                return "C";
            case "perilPalace":
                return "P";
            case "empty":
                return "*";
        }
        return null;
    }
}