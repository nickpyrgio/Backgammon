package backgammon;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Dimension;
import java.util.Stack;
import javax.swing.JLayeredPane;

/**
 * Η κλάση Board αντιπροσωπεύει το ίδιο το τάβλι.
 * @author Nikos Pyrgiotis
 */
public final class Board extends JLayeredPane 
{

    private static final int RED_FINAL_AREA = 26;  //Η τελική περιοχή των κόκκινων.
    private static final int WHITE_FINAL_AREA = 27; //Η τελική περιοχή των άσπρων.
    
    private  Background boardBackground;    //Αντικείμενο Background που θα αποτελεί το φοντο για το παιχνίδι.
    private Stack<TempMove> tempMoveStack;  //Στοίβα με τις προσωρινές κινήσεις κάθε γύρου.       
    private Area[] stateArray;       //Πίνακας αντικειμένων τύπου Area.
    private Dice firstDice;          //2 αντικείμενα τύπου Dice που αντιπροσωπέυουν τα δύο ζάρια.
    private Dice secondDice;            
           
    
    //Εμφωλευμένη τάξη τα αντικείμενα της οποίας αποτελούν τις προσωρινές κινήσεις σε έναν γύρο.
    protected  class TempMove
    {   
        private int prevPos;  //Προηγούμενη θέση που είχε το πούλι.
        private int newPos;   //Νέα θέση που θα έχει το πούλι.
        private Piece piece;  //Το ίδιο το πούλι.

        /**
         * Κατασκευαστής αντικειμένου TempMove.
         * @param prevPos Προηγούμενη θέση που είχε το πούλι.
         * @param newPos Νέα θέση που θα έχει το πούλι.
         */
        TempMove(int prevPos ,int newPos)
        {
            this.prevPos = prevPos;
            this.newPos = newPos;
        }

        /**
         * Κατασκευαστής αντικειμένου TempMove.
         * @param prevPos Προηγούμενη θέση που είχε το πούλι.
         * @param newPos Νέα θέση που θα έχει το πούλι.
         * @param piece Ποιό πούλι θα μετακινήσουμε.
         */
        TempMove(int prevPos ,int newPos, Piece piece)
        {
            this.piece = piece;
            this.prevPos = prevPos;
            this.newPos = newPos;
        } 
        
        /**
         * Η μέθοδος getPrevPos επιστρέφει την προηγόυμενη θέση που είχε το πούλι.
         */
        protected int getPrevPos()
        {
            return this.prevPos;
        }
        
        /**
         * Η μέθοδος getNewPos επιστρέφει την νέα θέση που θα έχει το πούλι.
         */
        protected int getNewPos()
        {
            return this.newPos;
        }
        
        /**
         * Η μέθοδος getPieceMoved επιστρέφει το πούλι που μετακινήσαμε.
         */
        protected Piece getPieceMoved()
        {
            return this.piece;
        }            
    }
    /**
     * Κατασκευαστής αντικειμένου Board.
     */
    public Board()
    {      
        //Αρχικοποίηση των μεταβλητών και αντικειμένων του νέου Board.
        this.tempMoveStack = new Stack<TempMove>();
        this.stateArray = new Area[28];        //Οι 28 περιοχές του Board.
        this.boardBackground = new Background(1024, 680, "backgammon\\leather\\background1.png");        
        
        for (int i = 0; i < stateArray.length ; i++)
        {           
            this.stateArray[i] = new Area(i);    //Δημιουργία των Areas του πίνακα.        
            //0 - 23 , 24 - 25 , 26 - 27
        }
        this.firstDice = new Dice(CoordinatePos.FIRST_WHITE_DICE_X, CoordinatePos.DICE_Y, true);
        this.secondDice = new Dice(CoordinatePos.SEC_RED_DICE_X, CoordinatePos.DICE_Y, false);        
        this.firstDice.setVisible(true);
        this.secondDice.setVisible(true);   
        this.setBounds(0, 0, 1024, 768);
        this.setPreferredSize(new Dimension(1024, 690));                    
    }      
      
    /**
     * Η μέθοδος boardSetUp ορίζει την θέση των αντικειμένων του Board όταν πρωτοξεκινάμε 
     * το παιχνίδι.Τα πούλια κάθε παίχτη στην αντίστοιχη FinalArea και τα ζάρια στο κέντρο.
     */
    void boardSetUp()
    {        
        this.add(this.firstDice, JLayeredPane.DRAG_LAYER);
        this.add(this.secondDice, JLayeredPane.DRAG_LAYER);        
        this.add(this.boardBackground, JLayeredPane.DEFAULT_LAYER);             
        this.stateArray[Board.WHITE_FINAL_AREA].appendPieces(15, true);
        
        this.stateArray[Board.RED_FINAL_AREA].appendPieces(15, false);
       
        for (Area stateArray1 : this.stateArray) {
            for (int k = 0; k < stateArray1.getSize(); k ++) {
                this.add(stateArray1.getPiece(k), JLayeredPane.DRAG_LAYER);
                this.setLayer(stateArray1.getPiece(k), JLayeredPane.DRAG_LAYER + k);
            }
        }                                                     
     }
     
    /**
     * Η μέθοδος initBoard ορίζει εκ νέου την θέση των αντικειμένων του Board μετά το τέλος 
     * κάποιου παιχνιδιού.Επαναφέρει τα πούλια στις αντίστοιχες FinalArea
     * @param state Η κατάσταση του παιχνιδιού που θα ενημερώσουμε
     */
    private void initBoard(State state)
    {
        //Επαναφέρουμε στην αρχική κατάσταση τα άσπρα πούλια(σε επίπεδο interface με τον παίχτη)..
        for (Area stateArray1 : this.stateArray) {
            if (!stateArray1.isWhiteArea()) {
                continue;
            }
            //Αποθηκεύουμε το size γιατί αλλιώς θα άλλαζε κάθε φορά στο for που ακολουθεί.
            int tempOriginalSize = stateArray1.getSize();
            for (int k = 0; k < tempOriginalSize; k++) {
                this.stateArray[Board.WHITE_FINAL_AREA].appendPiece(stateArray1.removePiece(stateArray1.getPiece(0)));
            }
        }   
        //Ενημερώνουμε και το GUI.
        for (int k = 0; k < this.stateArray[Board.WHITE_FINAL_AREA].getSize(); k ++)
        {
            this.add(this.stateArray[Board.WHITE_FINAL_AREA].getPiece(k), JLayeredPane.DRAG_LAYER);
            this.setLayer(this.stateArray[Board.WHITE_FINAL_AREA].getPiece(k), JLayeredPane.DRAG_LAYER + k);
        } 
        
        //..και το ίδιο κάνουμε και για τα κόκκινα.
        for (Area stateArray1 : this.stateArray) {
            if (stateArray1.isWhiteArea()) {
                continue;
            }
            //Αποθηκεύουμε το size γιατί αλλιώς θα άλλαζε κάθε φορά στο for που ακολουθεί.
            int tempOriginalSize = stateArray1.getSize();
            for (int k = 0; k < tempOriginalSize; k++) {
                this.stateArray[Board.RED_FINAL_AREA].appendPiece(stateArray1.removePiece(stateArray1.getPiece(0)));
            }                    
        }
        //Ενημερώνουμε και το GUI.
        for (int k = 0; k < this.stateArray[Board.RED_FINAL_AREA].getSize(); k ++)
        {
            this.add(this.stateArray[Board.RED_FINAL_AREA].getPiece(k), JLayeredPane.DRAG_LAYER);
            this.setLayer(this.stateArray[Board.RED_FINAL_AREA].getPiece(k), JLayeredPane.DRAG_LAYER + k);
        }    
        //Επαναφέρουμε τα ζάρια.
        this.firstDice.setIsWhite(true);
        this.secondDice.setIsWhite(false);
        this.firstDice.setLocation(CoordinatePos.FIRST_WHITE_DICE_X, CoordinatePos.DICE_Y);
        this.secondDice.setLocation(CoordinatePos.SEC_RED_DICE_X, CoordinatePos.DICE_Y);
        
        //Ανανεώνουμε και την εσωτερική δομή.
        for(int i = 0; i < this.stateArray.length;i++)
        {
            CheckerArea temp = state.getArea(i);
            if(temp.isEmpty()) continue;
            int area = temp.isWhiteArea()? Board.WHITE_FINAL_AREA:Board.RED_FINAL_AREA;
            appendPieces(state.getArea(i).getSize(), area, i);
        }   
     }
     
    /**
     * Η μέθοδος appendPieces μεταφέρει πούλια απο μία περιοχή σε μια άλλη.
     * @param times Πόσα πούλια θα μεταφέρουμε.
     * @param areaToTakeFrom Από ποια περιοχή θα τα πάρουμε.
     * @param areaToTakeTO  Σε ποιά περιοχή θα τα πάμε.
     */
    void appendPieces(int times, int areaToTakeFrom, int areaToTakeTO)     
    {         
        for (int i = 0; i < times; i++) 
        {
            this.stateArray[areaToTakeTO].appendPiece(this.stateArray[areaToTakeFrom].removePiece(this.stateArray[areaToTakeFrom].getPiece(0)));            
        }
     }
     
    
    /**
     * Η μέθοδος startGame καλείται όταν ξεκινάει ένα νέο παιχνίδι.
     * @param first Ο αριθμός που έφερε το πρώτο ζάρι.
     * @param second Ο αριθμός που έφερε το δεύτερο ζάρι.
     * @param state  Από ποιά κατάσταση θα συνεχίσουμε(αρχικοποιώντας την).
     */
    void startGame(int first, int second, State state)
    {                
        initBoard(state); //Κλήση της initBoard για επαναφορά στην αρχική κατάσταση.
        this.firstDice.setDice(first);  //Ρίχνουμε τα δύο ζάρια.        
        this.secondDice.setDice(second);        
        this.tempMoveStack.clear();    //Αδειάζουμε την στόιβα με τις προσωρινές κινήσεις.                     
     }
     
    /**
     * Η μέθοδος setDicePos καθορίζει την θέση των ζαριών(αφορά το interface με τον παίχτη).
     * @param first Ο αριθμός που έφερε το πρώτο ζάρι.
     * @param second Ο αριθμός που έφερε το πρώτο ζάρι.
     * @param isWhiteTurn Ποιός παίχτης έχει σειρά.
     */
    void setDicePos(int first, int second, boolean isWhiteTurn)
    {        
        this.tempMoveStack.clear(); //Αδειάζουμε την στόιβα με τις προσωρινές κινήσεις.
                        
        if(isWhiteTurn)
        {            
            this.firstDice.setIsWhite(true);
            this.secondDice.setIsWhite(true);            
            this.firstDice.setLocation(CoordinatePos.FIRST_WHITE_DICE_X, CoordinatePos.DICE_Y);
            this.secondDice.setLocation(CoordinatePos.SEC_WHITE_DICE_X, CoordinatePos.DICE_Y);
        }
        else
        {
            this.firstDice.setIsWhite(false);
            this.secondDice.setIsWhite(false);
            this.firstDice.setLocation(CoordinatePos.FIRST_RED_DICE_X, CoordinatePos.DICE_Y);
            this.secondDice.setLocation(CoordinatePos.SEC_RED_DICE_X, CoordinatePos.DICE_Y);
        }                      
        this.firstDice.setRandomNum(first);
        this.firstDice.setDice(first);
        this.secondDice.setRandomNum(second);                
        this.secondDice.setDice(second);        
     }               
       
    /**
     * Η μέθοδος cancelAllMoves ακυρώνει τις τελευταίες κινήσεις που έκανε κάποιος παίχτης 
     * σε έναν γύρο.
     */
    void cancelAllMoves()
    {
        TempMove tempMove;          
        while(!this.tempMoveStack.isEmpty()) //Όσο η στοιβα με τις προσωρινές κινήσεις δεν είναι άδεια..  
        {    
            //Παίρνουμε κάθε πούλι που του αλλάξαμε θέση σε αυτόν τον γύρο και το επιστρέφουμε
            //εκεί απ'όπου το πήραμε.                     
            tempMove = this.tempMoveStack.pop(); 
            Piece piece = this.stateArray[tempMove.newPos].removePiece(tempMove.piece );               
            this.stateArray[tempMove.prevPos].appendPiece(piece);              
            for(int i = 0; i < this.stateArray[tempMove.prevPos].getSize(); i++)
            {
                this.setLayer(this.stateArray[tempMove.prevPos].getPiece(i), Board.DRAG_LAYER + i);
            }
        }       
     }       
    
    /**
     * Η μέθοδος isTerminalState μας απαντά αν η τωρινή κατάσταση είναιτελική ή όχι.
     * @return true,αν είναι τελική,false αν δεν είναι.
     */
    boolean isTerminalState()
    {
        return this.stateArray[RED_FINAL_AREA].getSize() == 15 || this.stateArray[WHITE_FINAL_AREA].getSize() == 15;
    }                              
     
    /**
     * Η μέθοδος getArea επιστρέφει την Area με αριθμό το index που της δώσαμε σαν όρισμα.
     * @param index Ο αριθμός Area της που ζητάμε.
     * @return Αντικείμενο τύπου Area.
     */
    Area getArea(int index)
    {
        return this.stateArray[index];
    } 
 
    /**
     * Η μέθοδος movePiece μετακινεί ένα πούλι στη νέα του θέση(αφορά το GUI-interface με τον παίχτη)
     * @param piece Το πούλι που θα μετακινήσουμε.
     * @param newPos Η νεά θέση που θα έχει το πούλι.
     */
    void movePiece(Piece piece, int newPos)
    {                     
        this.tempMoveStack.add(new TempMove(piece.getPosition(), newPos, piece));            
        this.stateArray[piece.getPosition()].removePiece(piece);        
        this.stateArray[newPos].appendPiece(piece);                        
        for(int i = 0; i < this.getArea(newPos).getSize(); i++)
        {
            this.setLayer(this.getArea(newPos).getPiece(i), Board.DRAG_LAYER + i);
        }                              
    }
    
    /**
     * Η μέθοδος moveHitPieces .
     * @param stack 
     */
    void moveHitPieces(Stack<State.TempMove> stack)
    {                       
        while(!stack.isEmpty())
        {
            State.TempMove tmp = stack.pop();
            Piece piece = this.stateArray[tmp.getPrevPos()].getPiece(0);
            TempMove temp = new TempMove(tmp.getPrevPos(),tmp.getNewPos(), piece );
            this.tempMoveStack.add(temp);                 
            this.stateArray[tmp.getPrevPos()].removePiece(piece);        
            this.stateArray[temp.newPos].appendPiece(piece); 
            for(int i = 0; i < this.getArea(tmp.getNewPos()).getSize(); i++)
            {
                this.setLayer(this.getArea(tmp.getNewPos()).getPiece(i), Board.DRAG_LAYER + i);
            }                                          
        }        

    }    
    /**
     * Η μέθοδος equals ελέγχει αν δυο αντικείμενα Board είναι ίδια.
     * @param b Το αντικείμενο με το οποίο θα συγκρίνουμε το τωρινό αντικείμενο.
     * @return  true αν είναι ίδια,false αν δεν είναι.
     */
    @Override
    public boolean equals(Object b)
    {
        if(!(b instanceof Board)) return false;
        Board board = (Board)b;
        for(int i = 0; i < this.stateArray.length; i ++)
        {
            if(stateArray[i].isWhiteArea() == board.stateArray[i].isWhiteArea() &&stateArray[i].getSize() == board.stateArray[i].getSize() )
                continue;
            else
                return false;
        }            
        return true;
    }
    
    @Override
    public String toString()
    {
        //System.out.println(this.tempMoveStack.size());

        String str = "";
        /*
        for(int i = 0 ; i < this.tempMoveStack.size();i++)
        {
            if(this.isWhiteTurn && this.tempMoveStack.get(i).piece instanceof RedPiece)continue;
            if(!this.isWhiteTurn && this.tempMoveStack.get(i).piece instanceof WhitePiece) continue;            
            str += String.valueOf(this.tempMoveStack.get(i).prevPos) + " " + String.valueOf(this.tempMoveStack.get(i).newPos + "\n");
        }
        str += "--------------------------\n";
         *
         */
       
        return str;
    }          
}
