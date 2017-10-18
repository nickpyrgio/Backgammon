package backgammon;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 * Η κλάση CheckerArea αντιπροσωπεύει μια περιοχή στο backgammon board(αφορά το 
 * GUI-interface με το χρήστη).
 * @author Nikos
 */
public class Area {
    
    private ArrayList<Piece> array;  //Πίνακας αντικειμένων τύπου Piece(τα πούλια που έχει η Area).   
    private final int POSITION;     //Ο αριθμός της Area(ποιά περιοχή είναι).
   
    /**
     * Κατασκευαστής αντικειμένου Area.
     * @param position Ποιά Area έιναι.
     */
    public Area(int position)
    {
        this.POSITION = position;
        //Έλεγχος ότι το position είναι σωστό.
        if(position < 0 || position > 27)
        {
            System.exit(0);
        }                
        this.array = new ArrayList<Piece>();        
    }
        
   
    /**
     * Η μέθοδος getPiece επιστρέφει το πούλι μιας συγκεκριμένης θέσης στην Area.
     * @param index Σε ποιά θέση της Area είναι το πούλι.
     * @return Το πούλι που θέλουμε.
     */
    public Piece getPiece(int index)
    {
        if(index < 0 || index >= this.array.size())
        {
            return null;
        }
        return this.array.get(index);
    }
    
    /**
     * Η μέθοδος appendPiecesWithoutAdjust προσθέτει πούλια στην Area χωρίς να ενημερώνει 
     * το GUI-interface.
     * @param howManyPieces Πόσα πούλια θα προσθέσουμε.
     * @param isWhite true αν τα πούλια θα είναι άσπρα,false αν θα είναι κόκκινα.
     */
    public void appendPiecesWithoutAdjust(int howManyPieces, boolean isWhite)
    {
        if(isWhite)
        {
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPieceWithoutAdjust(new WhitePiece(this.POSITION));
            }
        }
        else
        {
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPieceWithoutAdjust(new RedPiece(this.POSITION));
            }            
        }        
    }
    
    /**
     * Η μέθοδος appendPieces προσθέτει πούλια στην Area ενημερώνωντας και το GUI-interface.
     * @param howManyPieces Πόσα πούλια θα προσθέσουμε.
     * @param isWhite true αν τα πούλια θα είναι άσπρα,false αν θα είναι κόκκινα.
     */
    public void appendPieces(int howManyPieces, boolean isWhite)
    {        
        if(isWhite)
        {
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPiece(new WhitePiece(this.POSITION));
            }
        }
        else
        {
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPiece(new RedPiece(this.POSITION));
            }            
        }
    }        
    
    /**
     * Η μέθοδος removePiece αφαιρέι ένα πούλι από την περιοχή.
     * @param piece Ποιό πούλι θα αφαιρέσουμε.
     * @return Το πούλι που αφαιρέσαμε.
     */
    public Piece removePiece(Piece piece)
    {  
        
        if(!(this.array.isEmpty()))
        {
            //Αφαιρούμε το πούλι.
            int index = this.array.indexOf(piece);            
            Piece removedPiece = this.array.get(index) ;
          
           //Προσαρμόζουμε την περιοχή.
            if(!(this.array.size() == 1))
            {
                for(int i = index; i < this.array.size(); i++ )
                {
                    if( this.array.get(i).getY() < 481)                        
                    {
                        this.array.get(i).setBounds(this.array.get(i).getX(), this.array.get(i).getY() - this.calcDistance());
                    }
                    else               
                    {
                        this.array.get(i).setBounds(this.array.get(i).getX(), this.array.get(i).getY() + this.calcDistance());
                    }
                }
            }

            this.array.remove(index);
            
            this.adjustArea(); 
            
            return removedPiece;
        }
         return null;                
    }
    
    
     /**
     * Η μέθοδος appendPieceWithoutAdjust προσθέτει ένα πούλι στην Area χωρίς να ενημερώνει 
     * το GUI-interface.
     * @param piece Ποιό πούλι θα προσθέσουμε.
     */
    public void appendPieceWithoutAdjust(Piece piece)
    {
        piece.setPosition(POSITION); 
        this.array.add(piece);        
    }
    
    /**
     * Η μέθοδος appendPiece προσθέτει ένα πούλι στην Area ενημερώνωντας και το GUI-interface.
     * @param piece Ποιό πούλι θα προσθέσουμε.
     */
    public void appendPiece(Piece piece)
    {        
        
         if((POSITION >= 0 && POSITION < 12 || POSITION == 24 || POSITION == 26) )
        {         
            piece.setLocation(this.xCoordinateCalc(), CoordinatePos.LEFT_UP_CORNER_Y); 
        }        
        else 
         {
            piece.setLocation(this.xCoordinateCalc(), CoordinatePos.LEFT_DOWN_CORNER_Y); 
         }        
        piece.setPosition(POSITION); //Ενημερώνουμε το position που έχει το πούλι.
        this.array.add(piece);  //Προσθέτουμε το πούλι στην περιοχή.
        adjustArea();  //Προσαρμόζουμε την περιοχή(αφορά το GUI).
    }  
    
     /**
      * Η μέθοδος adjustArea προσαρμόζει το πώς εμφανίζονται τα πούλια της περιοχής στο GUI-interface.
      */       
    private void adjustArea()
    {        
        for(int i = 1; i < this.array.size();i ++)
        {                               
            this.array.get(i).setBounds(this.array.get(i).getX(), this.yCoordinateCalc(i));
        }        
    }
    
    private int calcDistance()
    {
        int distance;       
        if(this.array.size() >= 6)
        {           
            distance = CoordinatePos.PIECE_SIZE_Y  - (CoordinatePos.PIECE_SIZE_Y - ((((CoordinatePos.LEFT_DOWN_CORNER_Y + 50) - CoordinatePos.UP_LIMIT_AREA_23_TO_12 ))/this.array.size()));
        }
        else
        {
            distance = CoordinatePos.PIECE_SIZE_Y;
        }        
        return distance;
    }
    private int yCoordinateCalc(int i)
    {                       
            
         if((POSITION >= 0 && POSITION < 12 || POSITION == 24 || POSITION == 26) )
        {            
            if(this.array.isEmpty()) return CoordinatePos.LEFT_UP_CORNER_Y;
            else return this.array.get(i - 1).getY() + calcDistance();
        }                                           
        else
         {
            if(this.array.isEmpty()) return CoordinatePos.LEFT_DOWN_CORNER_Y;
            else return this.array.get(i - 1).getY() - calcDistance();
         }         
     }
    
    private int xCoordinateCalc()
    {        
        if(POSITION >= 0 && POSITION < 12)
        {
            if(POSITION >= 6) return CoordinatePos.LEFT_UP_CORNER_X + CoordinatePos.PIECE_SIZE_X *(POSITION + 1);
            return CoordinatePos.LEFT_UP_CORNER_X + CoordinatePos.PIECE_SIZE_X * POSITION;
            //area 6 is damaged area
        }
        else if(POSITION == 24 || POSITION == 25)
        {
            //Middle column X coordinate
            return CoordinatePos.DAMAGED_AREA_X;
        }
        else if(POSITION == 26 || POSITION == 27)                
        {        
            //Right corner colummn column X coordinate
            return CoordinatePos.FINAL_AREA_X;
        }             
        else
        {            
            if(POSITION >= 18) return CoordinatePos.RIGHT_DOWN_CORNER_X - ((POSITION + 2) %13)*CoordinatePos.PIECE_SIZE_X;
            return CoordinatePos.RIGHT_DOWN_CORNER_X - ((POSITION + 1)%13)*CoordinatePos.PIECE_SIZE_X  ;            
        }        
    }
    
    /**
     * Η μέθοδος removeAll αφαιρεί όλα τα πούλια από την περιοχή.
     */
    public void removeAll()
    {
        this.array.clear();
    }
    
   
   /**
    * @return Το μέγεθος της περιοχής(πόσα πούλια έχει).
    */
    public int getSize()
    {
        return this.array.size();
    }
    
    /**
    * @return true αν είναι άδεια η περιοχή,false αν δεν είναι άδεια.
    */
    public boolean isEmpty()
    {
        return this.array.isEmpty();
    }
    
    /**
    * @return true αν είναι άσπρη η περιοχή,false αν είναι κόκκινη η περιοχή.
    */
    public boolean isWhiteArea()
    {
        if(this.array.isEmpty()) return true;
        if(this.array.get(0) instanceof WhitePiece) return true;
        return false;
    }
    
    /**
    * @return true αν είναι η περιοχή έχει από δυο και πάνω πούλια,false αν έχει ένα πούλι.
    */
    public boolean isOccupied()
    {          
        return this.array.size()  >= 2 ? true : false;
    }
               
}
