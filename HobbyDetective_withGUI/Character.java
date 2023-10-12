import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class Character extends Card<String>{
    private int startRow;
    private int startCol;
    
    Character(String name, int startRow, int startCol) {
        super("Character", name);
        this.startRow = startRow;
        this.startCol = startCol;
    }
    
    //gets the row position (x position) of the character
    public int getStartRow(){
        return startRow;
    }
    
    //gets the column position (y position) of the character
    public int getStartCol(){
        return startCol;
    }

    //returns character name
    public String getPrintable(){
        return name;
    }
}