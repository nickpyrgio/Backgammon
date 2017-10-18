package backgammon;



import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Η κλάση Piece αντιπροσωπεύει ένα πούλι στο τάβλι.
 * @author Nikos
 */
public class Piece extends JLabel 
{
        
    private static final int PIECE_WIDTH = 62;   //Το πλάτος που έχει το πούλι.
    private static final int PIECE_HEIGHT = 62;  //Το ύψος που έχει το πούλι.   
    protected static final String PATH =  "backgammon\\leather\\"; //Σχετικό PATH για τις εικόνες.
    private int position; //Σε ποια θέση-περιοχή είναι το πούλι( κάθε πούλι ξέρει που βρίσκεται).
    
    public Piece()
    {        
        this(0,0);        
    }
       
    public Piece(int row )
    {
        this(0,0, row);
    }
    
    public Piece(int x,int y )
    {                         
        this(x,y,-1);                        
    }
    
    /**
     * Κατασκευαστής αντικειμένων Piece.
     * @param x  Συντεταγμένη x της πάνω-αριστερά γωνίας.
     * @param y  Συντεταγμένη y της πάνω-αριστερά γωνίας.
     * @param row  Θέση στην οποία θα βρίσκεται το πούλι.
     */
    public Piece( int x, int y , int row )
    {                 
        super.setBounds( x ,y , PIECE_WIDTH, PIECE_HEIGHT);
        this.position = row;
    } 
    
    /**
     * @return Την θέση που βρίσκεται το πούλι.
     */
    public int getPosition()
    {
        return this.position;
    }
    
    /**
      * Η μέθοδος setPosition καθορίζει την θέση που βρίσκεται το πούλι.
     * @param row
      */
    public void setPosition(int row)
    {
        this.position = row;
    }
    
    @Override
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds( x ,y , PIECE_WIDTH, PIECE_HEIGHT);
    }
    
    /**
     * Καθορισμός του μεγέθους και της θέσης για το πούλι.
     * @param x  Συντεταγμένη x της πάνω-αριστερά γωνίας.
     * @param y  Συντεταγμένη y της πάνω-αριστερά γωνίας.
     */
    public void setBounds(int x, int y)
    {
        super.setBounds( x ,y , PIECE_WIDTH, PIECE_HEIGHT);
    }
    
    /**
     * Κατασκευαστής που καθορίζει την εικόνα για το πούλι.
     */
    private Piece(ImageIcon img)
    {
        super(img);
    }                    
    
    @Override
    public String toString()               
   {       
       return ("X :" + this.getX() + "Y :" + this.getY() + " ");
   }
    
}
