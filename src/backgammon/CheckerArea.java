package backgammon;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * Η κλάση CheckerArea αντιπροσωπεύει μια περιοχή στο backgammon board.
 * @author Nikos Pyrgiotis
 * 
 */

 class CheckerArea {

    private static final byte WHITE_CHECKER = -1;  //Αντιστοιχίζουμε την τιμή -1 στα ασπρα πούλια,
    private static final byte RED_CHECKER = 1;     //την τιμή 1 στα κόκκινα και
    private static final byte EMPTY_AREA = 0;      //την τιμή 0 στις άδειες περιοχές.
    private byte[] CHECKER_ARRAY;           //Πίνακας από bytes,που αντιπροσωπεύει μια περιοχή καισε κάθε 
                                           //θέση του οποίου θα υπάρχει 1 πούλι.
    private int currentCapacity;           //Χωρητικότητα του checkerArray(μέχρι πόσα πούλια μπορεί να έχει μια περιοχή).

    /**
     * Κατασκευαστής αντικειμένου CheckerArea.Αρχικοποιεί τα checkerArray kai currentCapasity.
     * @param position Η θέση που θέλουμε να προσθέσουμε το πούλι.
     */
    protected CheckerArea(int position)
    {        
        this.CHECKER_ARRAY = new byte[15];
        this.currentCapacity = 0;
    }
    
    
    /**
     * Η μέθοδος getPiece επιστρέφει ένα πούλι της περιοχής.
     * @param index Η θέση που βρισκεται το πούλι που  επιστρέφεται.  
     * @return Το byte που αντιπροσωπεύει το πούλι.
     */
    protected byte getPiece(int index)
    {
        if(index < 0 || index >= this.currentCapacity)
        {
            return EMPTY_AREA;
        }
        return this.CHECKER_ARRAY[index];
    }

    /**
     * Η μέθοδος appendPieces προσθέτει πούλια στην περιοχή.
     * @param howManyPieces Πόσα πούλια πρέπει να προστεθούν.
     * @param isWhite  Τι είδους είναι τα πούλια,άσπρα(true) ή κοκκινα(false).
     */
    protected void appendPieces(int howManyPieces, boolean isWhite)
    {        
        if(isWhite)
        {
            //Προσθέτουμε ένα ένα τα πούλια με χρήση της appendPiece.
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPiece(CheckerArea.WHITE_CHECKER); 
            }
        }
        else
        {
            for(int i = 0 ; i < howManyPieces; i++)
            {
                this.appendPiece(CheckerArea.RED_CHECKER);
            }
        }        
    }

    /**
     * Η μάθοδος removePieceFromTop αφαιρεί και επιστρέφει το τελευταίο πούλι της περιοχής.
     * @return Το byte που αντιστοιχεί στο πούλι που αφαιρέθηκε.
     */
    protected byte removePieceFromTop()
    {        
        if(this.isEmpty()) //Έλεγχος αν η περιοχή είναι άδεια.
        {            
            return CheckerArea.EMPTY_AREA;
        }
        byte removedPiece = this.CHECKER_ARRAY[--this.currentCapacity];        
        return removedPiece;
    }

    /**
     *Η μέθοδος appendPiece προσθέτει 1 πούλι στην περιοχή.
     * @param piece Η τιμή byte που αντιστοιχεί στο πούλι(1=κόκκινο,-1=άσπρο).
     */
    protected void appendPiece(byte piece)
    {        
        //System.out.println("Cap " + capacity);
        this.CHECKER_ARRAY[this.currentCapacity] =  piece;    
        this.currentCapacity++;
    }
         
    /**
     * Η μέθοδος getSize επιστρέφει το τωρινό μέγεθος της περιοχής.
     * @return Το currentCapasity της περιοχής.
     */
    protected int getSize()
    {
        return this.currentCapacity;
    }

    /**
     * Η μέθοδος isEmpty μας δείχνει αν η περιοχή είναι άδεια ή όχι.
     * @return true αν είναι άδεια,false αν δεν είναι.
     */
    protected boolean isEmpty()
    {
        return this.currentCapacity == 0;
    }

    /**
     * Η μέθοδος isWhiteArea μας δείχνει αν η περιοχή έχει άσπρα ή κοκκινα πούλια.
     * @return true αν έχει άσπρα,false αν έχει κόκκινα.
     */
    protected boolean isWhiteArea()
    {
        if(this.isEmpty()) return true;
        if(this.CHECKER_ARRAY[0] == CheckerArea.WHITE_CHECKER) return true;
        return false;
    }

    /**
     * Η μέθοδος isOccupied μας δείχνει άν η περιοχή έχει πάνω απο δύο πούλια.
     * @return true αν έχει,false αν δεν έχει.
     */
    protected boolean isOccupied()
    {
        return this.currentCapacity  >= 2 ? true : false;
    }

    /**
     * Η μέθοδος toString επιστρέφει ένα String που αντιπροσωπεύει την περιοχή,
     * δηλαδή τα πούλια που αυτή έχει.
     * @return Την περιοχή σε String.
     */
    @Override
    public String toString()
    {
        String str = "";
        for(int i = 0; i < this.currentCapacity; i++)
        {
            if((this.CHECKER_ARRAY[i]) == 1)   
                str += "R";    
            else 
                str += "W";    
        }
        return str;
    }

}
