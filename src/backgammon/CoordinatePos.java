package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Nikos Pyrgiotis
 * Class CoordinatePos contains all GUI information for the placement of the Objects to the screen
 * It could be an ?.ini file
 */
public class CoordinatePos {
    
    protected final static int LEFT_UP_CORNER_X = 108; //column
    protected final static int RIGHT_DOWN_CORNER_X = 852;

    protected final static int PIECE_SIZE_X = 62; //62x62 square(thus no X , Y coordinates the same)                
    
    protected final static int DAMAGED_AREA_X = 481;
    protected final static int FINAL_AREA_X = 932; 
    protected final static int FIRST_WHITE_DICE_X = 650; 
    protected final static int SEC_WHITE_DICE_X = 770; 
    protected final static int FIRST_RED_DICE_X = 220;         
    protected final static int SEC_RED_DICE_X = 349; 
        
    protected final static int DICE_Y = 315; 
    protected final static int PIECE_SIZE_Y = 50; //62x62 square(thus no X , Y coordinates the same)                
    protected final static int LEFT_UP_CORNER_Y = 22; // row
    protected final static int LEFT_DOWN_CORNER_Y = 597;    
    protected final static int UP_LIMIT_AREA_23_TO_12 = 400; 
    protected final static int UP_LIMIT_AREA_0_TO_11 = 275;     
}
